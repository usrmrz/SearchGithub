package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    suspend fun searchRepos(query: String): RepoSearchResponse
    fun getRepos(): Flow<List<Repo>>
    suspend fun insertRepos(repos: List<Repo>)
}
