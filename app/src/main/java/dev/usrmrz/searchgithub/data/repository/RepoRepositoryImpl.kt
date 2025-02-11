package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.RepoDao
import dev.usrmrz.searchgithub.data.entities.RepoEntity
import dev.usrmrz.searchgithub.data.mapper.RepoMapper
import dev.usrmrz.searchgithub.domain.model.RepoModel
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.util.RateLimiter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

//@Suppress("unused")
//@Singleton
//@OpenForTesting
class RepoRepositoryImpl @Inject constructor(
    private val db: GithubDb,
    private val dao: RepoDao,
    private val api: GithubService,
    private val repoMapper: RepoMapper,
) {

    private val repoListRateLimit = RateLimiter(10 * 60 * 1000)

    //Загрузка репозиториев для пользователя
    fun loadRepos(owner: String): Flow<Resource<List<RepoModel>>> {

        return object : NetworkBoundResource<List<RepoModel>, List<RepoEntity>>() {
            override suspend fun saveCallResult(item: List<RepoEntity>) {
//                val entities = item.map(repoMapper::mapToEntity)
                dao.insertRepos(item)
            }

            override suspend fun shouldFetch(data: List<RepoModel>?): Boolean {
                TODO("Not yet implemented")
            }

            override fun loadFromDb(): Flow<List<RepoModel>> {
                return dao.loadRepositories(owner)
                    .map { entities ->
                        entities.map(repoMapper::mapToModel)
                    }
            }

            override suspend fun createCall(): Flow<ApiResponse<List<RepoEntity>>> {
                return flow {
                    emit(ApiResponse.create(api.getRepos(owner)))
                }
            }
        }.asFlow()
    }

}


// Загрузка конкретного репозитория
//    fun loadRepo(owner: String, name: String): Flow<Resource<RepoEntity>> {
//    }


// Загрузка контрибьюторов
//    fun loadContributors(owner: String, name: String): Flow<Resource<List<Contributor>>> {
//    }


// Поиск репозиториев
//    @OptIn(ExperimentalCoroutinesApi::class)
fun search(query: String = ""): String {//Flow<Resource<List<RepoModel>>> {
    return println(query).toString()
}


// Поиск следующей страницы
fun searchNextPage(query: String = "") {//: Flow<Resource<Boolean>> {
    return println(query)
}
}