package dev.usrmrz.searchgithub.data.api

import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.Repo
import dev.usrmrz.searchgithub.domain.model.User
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//@Suppress("unused")
interface GithubService {

    @GET("users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ): User

    @GET("users/{login}/repos")
    suspend fun getRepos(
        @Path("login") login: String
    ): List<Repo>

    @GET("repos/{owner}/{name}")
    suspend fun getRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Repo

    @GET("repos/{owner}/{name}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): List<Contributor>

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String
    ): RepoSearchResponse

    @GET("search/repositories")
    fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int
    ): RepoSearchResponse
}
