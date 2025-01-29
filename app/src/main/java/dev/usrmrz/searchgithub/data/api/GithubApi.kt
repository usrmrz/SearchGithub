package dev.usrmrz.searchgithub.data.api

import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    suspend fun searchReposApi(@Query("q") query: String): RepoSearchResponse
}
