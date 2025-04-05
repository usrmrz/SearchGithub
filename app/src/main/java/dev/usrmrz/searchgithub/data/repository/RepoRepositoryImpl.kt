package dev.usrmrz.searchgithub.data.repository

import android.util.Log
import androidx.annotation.OpenForTesting
import androidx.room.withTransaction
import dev.usrmrz.searchgithub.data.api.ApiEmptyResponse
import dev.usrmrz.searchgithub.data.api.ApiErrorResponse
import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.ApiSuccessResponse
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.api.RepoSearchResponse
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.RepoDao
import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.data.db.entity.mapper.toDomain
import dev.usrmrz.searchgithub.data.db.entity.mapper.toEntity
import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import dev.usrmrz.searchgithub.util.RateLimiter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

//@Suppress("unused")
@Singleton
class RepoRepositoryImpl(
    private val api: GithubService,
    private val dao: RepoDao,
    private val db: GithubDb,
) : RepoRepository {

    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    override fun loadRepos(owner: String): Flow<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<RepoEntity>>() {
            override suspend fun saveCallResult(item: List<RepoEntity>) = dao.insertRepos(item)
            override fun shouldFetch(data: List<Repo>?) =
                data.isNullOrEmpty() || repoListRateLimit.shouldFetch(owner)

            override suspend fun createCall() = api.getRepos(owner)
            override fun loadFromDb(): Flow<List<Repo>> = dao.loadRepositories(owner)
                .map { entities -> entities.map { it.toDomain() } }

            override fun onFetchFailed() = repoListRateLimit.reset(owner)
        }.asFlow()
    }

    override fun loadRepo(owner: String, name: String): Flow<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, RepoEntity>() {
            override suspend fun saveCallResult(item: RepoEntity) {
                dao.insert(item)
            }

            override fun shouldFetch(data: Repo?) = data == null
            override suspend fun createCall() = api.getRepo(owner, name)
            override fun loadFromDb() = dao.load(owner, name).map { it.toDomain() }
        }.asFlow()
    }

    override fun loadContributors(owner: String, name: String): Flow<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<ContributorEntity>>() {
            override suspend fun saveCallResult(item: List<ContributorEntity>) {
                val contributorsWithRepoInfo = item.map {
                    it.copy(repoName = name, repoOwner = owner)
                }
                db.withTransaction {
                    dao.createRepoIfNotExists(
                        RepoEntity(
                            id = Repo.UNKNOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            owner = RepoEntity.OwnerEntity(owner, null),
                            description = "",
                            createdAt = "",
                            updatedAt = "",
                            watchers = 0,
                            issues = 0,
                            stars = 0,
                            forks = 0,
                        )
                    )
                    dao.insertContributors(contributorsWithRepoInfo)
                }
            }

            override fun shouldFetch(data: List<Contributor>?): Boolean = data.isNullOrEmpty()
            override fun loadFromDb(): Flow<List<Contributor>> {
                return dao.loadContributors(owner, name)
                    .map { list -> list.map { it.toDomain() } }
            }

            override suspend fun createCall() = api.getContributors(owner, name)
        }.asFlow()
    }

    override fun searchNextPage(query: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val current = db.repoDao().findSearchResult(query)
        Log.d(
            "RRI",
            "val current = db.repoDao().findSearchResult(query);;query: $query; next: ${current?.next};"
        )
        if(current == null) {
            emit(Resource.Success(false))
            return@flow
        }

        val nextPage = current.next
        Log.d(
            "RRI",
            "val nextPage = current.next;;nextPage: $nextPage;"
        )
        if(nextPage == null) {
            emit(Resource.Success(false))
            return@flow
        }

        val newValue = try {
            val response = withContext(Dispatchers.IO) {
                api.searchRepos(query, nextPage)
            }
            Log.d(
                "RRI",
                "val response = withContext;;response: $response;"
            )

            when(val apiResponse = ApiResponse.create(response)) {

                is ApiSuccessResponse<RepoSearchResponse> -> {
                    Log.d(
                        "RRI",
                        "is ApiSuccessResponse<RepoSearchResponse>;;apiResponse: $apiResponse;"
                    )
                    val ids = arrayListOf<Int>().apply {
                        addAll(current.repoIds)
                        addAll(apiResponse.body.items.map { it.id })
                    }

                    val merged = RepoSearchResult(
                        query, ids,
                        apiResponse.body.total, apiResponse.nextPage
                    )
                    val repoEntities = apiResponse.body.items.map { it.toEntity() }

                    db.withTransaction {
                        dao.insert(merged.toEntity())
                        dao.insertRepos(repoEntities)
                    }

                    Resource.Success(apiResponse.nextPage != null)
                }

                is ApiEmptyResponse<RepoSearchResponse> -> Resource.Success(false)
                is ApiErrorResponse<RepoSearchResponse> -> Resource.Error(
                    apiResponse.errorMessage,
                    true
                )
            }
        } catch(e: IOException) {
            Resource.Error(e.message ?: "Unknown error", true)
        }
        emit(newValue)
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun search(query: String): Flow<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>() {
            override suspend fun saveCallResult(item: RepoSearchResponse) {
                val repoIds = item.items.map { it.id }
                val repoEntities = item.items.map { it.toEntity() }
                val repoSearchResult = RepoSearchResult(
                    query = query,
                    repoIds = repoIds,
                    totalCount = item.total,
                    next = item.nextPage
                )
                db.withTransaction {
                    dao.insertRepos(repoEntities)
                    dao.insert(repoSearchResult.toEntity())
                }
            }

            override fun shouldFetch(data: List<Repo>?) = data.isNullOrEmpty()
            override fun loadFromDb(): Flow<List<Repo>> {
                return dao.search(query).flatMapLatest { searchData ->
                    if(searchData == null) {
                        flowOf(emptyList())
                    } else {
                        dao.loadOrdered(searchData.repoIds).map { list ->
                            list.map { it.toDomain() }
                        }
                    }
                }
            }

            override suspend fun createCall() = api.searchRepos(query)
            override fun processResponse(response: ApiSuccessResponse<RepoSearchResponse>): RepoSearchResponse {
                val body = response.body
                val links = response.links
                body.nextPage = response.nextPage
                Log.d("RRI", "body.nextPage;;body.nextPage: ${body.nextPage};links: $links")
                return body
            }
        }.asFlow()
    }
}
