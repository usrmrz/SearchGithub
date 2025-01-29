package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.GithubApi
import dev.usrmrz.searchgithub.data.database.RepoDao
import dev.usrmrz.searchgithub.data.entities.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepoRepositoryImpl(
    private val githubApi: GithubApi,
    private val repoDao: RepoDao,
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
            description = this.description.toString(),
            stars = this.stars,
        )
    }

    override suspend fun searchReposApi(query: String): RepoSearchResponse {
        val response = githubApi.searchReposApi(query)
        return response
    }

    override fun getReposFromDb(): Flow<List<Repo>> {
        return repoDao.getReposFromDb().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertReposDb(repos: List<Repo>) {
        val entities = repos.map { it.toEntity() }
        repoDao.insertReposDb(entities)
    }
}