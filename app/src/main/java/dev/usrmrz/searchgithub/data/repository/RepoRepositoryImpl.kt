package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.GithubApi
import dev.usrmrz.searchgithub.data.database.RepoDao
import dev.usrmrz.searchgithub.data.database.entity.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.Resource
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RepoRepositoryImpl(
    private val githubApi: GithubApi,
    private val repoDao: RepoDao
) : RepoRepository {

    private fun RepoEntity.toDomainModel(): Repo {
        return Repo(
            id = this.id,
            name = this.name,
            description = this.description,
            stars = this.stars,
        )
    }

    private fun Repo.toEntity(): RepoEntity {
        return RepoEntity(
            id = this.id,
            name = this.name,
            description = this.description,
            stars = this.stars,
        )
    }

    override fun searchRepositories(query: String): Flow<Resource<List<Repo>>> = flow {
        emit(Resource.Loading())

        try {
            val response = githubApi.searchRepositories(query)
            repoDao.insertRepos(response.items)
            emitAll(repoDao.getAllRepos().map { Resource.Success(it) })
        } catch(e: Exception) {
            emit(Resource.Error("Download Error: ${e.message}"))
        }
    }

}