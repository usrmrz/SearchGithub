package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    suspend fun searchReposApi(query: String): ApiResponse<RepoSearchResponse>
    fun getReposFromDb(): Flow<List<Repo>>
    suspend fun insertReposDb(repos: List<Repo>)
}
