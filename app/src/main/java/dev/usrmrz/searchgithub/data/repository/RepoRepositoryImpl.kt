package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.db.RepoDao
import dev.usrmrz.searchgithub.data.entities.OwnerEntity
import dev.usrmrz.searchgithub.data.entities.RepoEntity
import dev.usrmrz.searchgithub.domain.model.Owner
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import dev.usrmrz.searchgithub.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepoRepositoryImpl(
    private val githubService: GithubService,
    private val repoDao: RepoDao,
) : RepoRepository {

    private fun RepoEntity.toDomain(): Repo {
        return Repo(
            id = this.id,
            name = this.name,
            fullName = this.fullName,
            description = this.description,
            owner = owner.toDomain(),
            stars = this.stars,
        )
    }

    fun OwnerEntity.toDomain(): Owner {
        return Owner(
            login = this.login,
            url = this.url
        )
    }

    private fun Repo.toEntity(): RepoEntity {
        return RepoEntity(
            id = this.id,
            name = this.name,
            fullName = this.fullName,
            description = this.description.toString(),
            owner = owner.toEntity(),
            stars = this.stars,
        )
    }

    fun Owner.toEntity(): OwnerEntity {
        return OwnerEntity(
            login = this.login,
            url = this.url
        )
    }

    override suspend fun searchReposApi(query: String): ApiResponse<RepoSearchResponse> {
        val response = githubService.searchReposApi(query)
        return response
    }

    override fun getReposFromDb(): Flow<List<Repo>> {
        return repoDao.getReposFromDb().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertReposDb(repos: List<Repo>) {
        val entities = repos.map { it.toEntity() }
        repoDao.insertReposDb(entities)
    }
}