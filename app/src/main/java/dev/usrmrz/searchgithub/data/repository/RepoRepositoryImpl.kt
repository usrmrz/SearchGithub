package dev.usrmrz.searchgithub.data.repository

import android.R.id.input
import android.util.Log
import androidx.annotation.OpenForTesting
import androidx.room.withTransaction
import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.api.RepoSearchResponse
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.RepoDao
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
import retrofit2.mock.Calls.response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
@Singleton
@OpenForTesting
class RepoRepositoryImpl @Inject constructor(
    private val api: GithubService,
    private val dao: RepoDao,
    private val db: GithubDb,
) : RepoRepository {

//    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    override fun loadRepos(owner: String): Flow<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<Repo>>() {
            override suspend fun saveCallResult(item: List<Repo>) = dao.insertRepos(item)
            override fun shouldFetch(data: List<Repo>?) = data.isNullOrEmpty() //|| repoListRateLimit.shouldFetch(owner)
            override suspend fun createCall() = api.getRepos(owner)
            override fun loadFromDb() = dao.loadRepositories(owner)
//            override fun onFetchFailed() = repoListRateLimit.reset(owner)
        }.asFlow()
    }

    override fun loadRepo(owner: String, name: String): Flow<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, Repo>() {
            override suspend fun saveCallResult(item: Repo) = dao.insert(item)
            override fun shouldFetch(data: Repo?) = data == null
            override suspend fun createCall() = api.getRepo(
                owner = owner,
                name = name
            )
            override fun loadFromDb() = dao.load(
                ownerLogin = owner,
                name = name
            )
        }.asFlow()
    }

    override fun loadContributors(owner: String, name: String): Flow<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<Contributor>>() {
            override suspend fun saveCallResult(item: List<Contributor>) {
                item.forEach {
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.withTransaction {
                    dao.createRepoIfNotExists(
                        Repo(
                            id = Repo.UNKNOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            description = "",
                            owner = Repo.Owner(owner, null),
                            stars = 0
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
        Log.d(
            "RpRp_srchNP",
            "query: $query"
        )
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            api = api,
            db = db,
        )
        return fetchNextSearchPageTask.flowNext
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun search(query: String): Flow<Resource<List<Repo>>> {
        Log.d("RepoRp_search", "query: $query")
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>() {
            override suspend fun saveCallResult(item: RepoSearchResponse) {
                Log.d("RepoRp_ssp_svCallRslt", "input: $input; item: $item")
                val repoIds = item.items.map { it.id }
                Log.d("RepoRp_repoIds", "repoIds: $repoIds; item: $item")
                val repoSearchResult = RepoSearchResult(
                    query = query,
                    repoIds = repoIds,
                    totalCount = item.total,
                    next = item.nextPage
                )
                Log.d(
                    "RepoRp_repoSrchRslt",
                    "query: $query, repoIds: $repoIds, totalCount: ${item.total}, next: ${item.nextPage}"
                )
                db.withTransaction {
                    dao.insertRepos(item.items)
                    dao.insert(repoSearchResult)
                }
            }

            override fun shouldFetch(data: List<Repo>?) = data.isNullOrEmpty()
            override fun loadFromDb(): Flow<List<Repo>> {
                Log.d("RepoRp_lFDb", "query: $query")
                return dao.search(query).flatMapLatest { searchData ->
                    (if(searchData == null) {
                        Log.d("RepoRp_ldFDb_sDt", "searchData: $searchData")
                        //before: emptyList<Repo>()
                        flowOf(emptyList())//gpt listing
                    } else {
                        Log.d("RepoRp_loadFrmDb_else", "query: $query")
                        dao.loadOrdered(searchData.repoIds)
                    })
                }
            }

            override suspend fun createCall() =
                api.searchRepos(query)

//            override fun processResponse(response: ApiResponse.Success<RepoSearchResponse>): RepoSearchResponse {
//                Log.d("RRImpl_procResp", "response_body: ${response.body}")
//                val body = response.body
//                body.nextPage = response.nextPage
//                Log.d("RRImpl_procResp_vals", "body: $body response.nextPage: ${response.nextPage}")
//                return body
//            }
//            gpt begin
//            fun processApiResponse(response: ApiResponse<RepoSearchResponse>): ApiResponse<RepoSearchResponse> {
//                return when (response) {
//                    is ApiSuccessResponse -> response
//                    is ApiEmptyResponse -> {
//                        Log.w("RepoRepositoryImpl", "Empty API response received")
//                        ApiErrorResponse("Empty response from server") // Или другая логика
//                    }
//                    is ApiErrorResponse -> response
//                }
//            }
//            gpt end
        }.asFlow()
    }
}
