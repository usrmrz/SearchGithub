package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.RepoDao
import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.RepoModel
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import dev.usrmrz.searchgithub.util.RateLimiter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Suppress("unused")
class RepoRepositoryImpl(
    private val db: GithubDb,
    private val dao: RepoDao,
    private val api: GithubService
) : RepoRepository {

    private val repoListRateLimit = RateLimiter(10)

    override fun loadRepos(owner: String): Flow<Resource<List<RepoModel>>> = flow {
        emit(Resource.Loading())
    }

    override fun loadRepo(owner: String, name: String): Flow<Resource<RepoModel>> {
        TODO("Not yet implemented")
    }

    override fun loadContributors(owner: String, name: String): Flow<Resource<List<Contributor>>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchNextPage(query: String): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun search(query: String): Flow<Resource<List<RepoModel>>> {
        TODO("Not yet implemented")
    }
}