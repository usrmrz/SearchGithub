package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse

interface RepoRepository {
    suspend fun searchRepos(query: String): RepoSearchResponse
}
