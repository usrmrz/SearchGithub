package dev.usrmrz.searchgithub.data.repository

import android.R.id.input
import android.util.Log
import androidx.annotation.OpenForTesting
import androidx.room.withTransaction
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.api.RepoSearchResponse
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.RepoDao
import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.data.db.entity.mapper.toDomain
import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import dev.usrmrz.searchgithub.util.RateLimiter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
            override fun loadFromDb() = dao.loadRepositories(owner)
//            override fun onFetchFailed() = repoListRateLimit.reset(owner)
        }.asFlow()
    }

    override fun loadRepo(owner: String, name: String): Flow<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, RepoEntity>() {
            override suspend fun saveCallResult(item: RepoEntity) = dao.insert(item)
            override fun shouldFetch(data: Repo?) = data == null
            override suspend fun createCall() = api.getRepo(owner, name).toEntity()

            override fun loadFromDb() = dao.load(
                ownerLogin = owner,
                name = name
            ).map { it.toDomain() }
        }.asFlow()
    }

    override fun loadContributors(owner: String, name: String): Flow<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<ContributorEntity>>() {
            override suspend fun saveCallResult(item: List<ContributorEntity>) {
                item.forEach {
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.withTransaction {
                    dao.createRepoIfNotExists(
                        RepoEntity(
                            id = Repo.UNKNOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            owner = Repo.Owner(owner, null),
                            description = "",
                            watchers = 0,
                            issues = 0,
                            stars = 0,
                            forks = 0,
                        )
                    )
                    dao.insertContributors(item)
                }
            }

            override fun shouldFetch(data: List<Contributor>?): Boolean = data.isNullOrEmpty()
            override fun loadFromDb() = dao.loadContributors(owner, name)
            override suspend fun createCall() = api.getContributors(owner, name)
        }.asFlow()
    }

    override fun searchNextPage(query: String): Flow<Resource<Boolean>?> {
        Log.d("RepoRp", "override fun searchNextPage(query: String): Flow<Resource<Boolean>?> {;;query: $query")
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            api = api,
            db = db,
        )
        Log.d("RepoRp", "val fetchNextSearchPageTask = FetchNextSearchPageTask(;;query: $query; fetchNextSearchPageTask.flowNext: ${fetchNextSearchPageTask.flowNext}")
        return fetchNextSearchPageTask.flowNext
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun search(query: String): Flow<Resource<List<RepoEntity>>> {
        Log.d("RepoRp", "override fun search(query: String): Flow<Resource<List<Repo>>> {;;query: $query")
        return object : NetworkBoundResource<List<RepoEntity>, RepoSearchResponse>() {
            override suspend fun saveCallResult(item: RepoSearchResponse) {
                Log.d("RepoRp", "override suspend fun saveCallResult(item: RepoSearchResponse) {;;input: $input; item: $item")
                val repoIds = item.items.map { it.id }
                Log.d("RepoRp", "val repoIds = item.items.map { it.id };;repoIds: $repoIds; item: $item")
                val repoSearchResult = RepoSearchResult(
                    query = query,
                    repoIds = repoIds,
                    totalCount = item.total,
                    next = item.nextPage
                )
                Log.d(
                    "RepoRp",
                    "val repoSearchResult = RepoSearchResult(;;query: $query, repoIds: $repoIds, totalCount: ${item.total}, next: ${item.nextPage}"
                )
                db.withTransaction {
                    dao.insertRepos(item.items)
                    dao.insert(repoSearchResult)
                }
            }

            override fun shouldFetch(data: List<Repo>?) = data.isNullOrEmpty()
            override fun loadFromDb(): Flow<List<Repo>> {
                Log.d("RepoRp", "override fun loadFromDb(): Flow<List<Repo>> {;;query: $query")
                return dao.search(query).flatMapLatest { searchData ->
                    (if(searchData == null) {
                        Log.d("searchData == null;;RepoRp", "searchData: $searchData")
                        //before: emptyList<Repo>()
                        flowOf(emptyList())//gpt listing
                    } else {
                        Log.d("RepoRp", "} else {;;query: $query")
                        dao.loadOrdered(searchData.repoIds)
                    })
                }
            }

            override suspend fun createCall() =
                api.searchRepos(query).toDomain()

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
