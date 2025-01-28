package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.GithubApi
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import dev.usrmrz.searchgithub.domain.repository.RepoRepository

class RepoRepositoryImpl(
    private val githubApi: GithubApi,
) : RepoRepository {

    override suspend fun searchRepos(query: String): RepoSearchResponse {
        val response = githubApi.searchRepos(query)
        return response
    }
}