package dev.usrmrz.searchgithub.data.repository

import androidx.annotation.OpenForTesting
import androidx.room.withTransaction
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.api.RepoSearchResponse
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.RepoDao
import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoSearchEntity
import dev.usrmrz.searchgithub.data.db.entity.mapper.toDomain
import dev.usrmrz.searchgithub.data.db.entity.mapper.toEntity
import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import dev.usrmrz.searchgithub.util.RateLimiter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

//@Suppress("unused")
@Singleton
@OpenForTesting
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
//                item.forEach {
//                    it.repoName = name
//                    it.repoOwner = owner
//                }
                db.withTransaction {
                    dao.createRepoIfNotExists(
                        RepoEntity(
                            id = Repo.UNKNOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            owner = RepoEntity.OwnerEntity(owner, null),
                            description = "",
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

    override fun searchNextPage(query: String): StateFlow<Resource<Boolean>?> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            api = api,
            db = db,
        )
        return fetchNextSearchPageTask.flowNext
    }

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

//            override fun processResponse(response: ApiResponse.Success<RepoSearchResponse>): RepoSearchResponse {
//                Log.d("RRImpl_procResp", "response_body: ${response.data}")
//                val body = response.data
//                body.nextPage = response.data.nextPage
//                Log.d(
//                    "RRImpl_procResp_vals",
//                    "body: $body; response.nextPage: ${response.data.nextPage}"
//                )
//                return body
//            }
        }.asFlow()
    }
}
