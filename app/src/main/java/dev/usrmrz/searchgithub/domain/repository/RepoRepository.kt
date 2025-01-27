package dev.usrmrz.searchgithub.domain.repository

import dev.usrmrz.searchgithub.domain.model.Repo

interface RepoRepository {
    suspend fun searchRepos(query: String): List<Repo>
    suspend fun count(query: String): Int
}
