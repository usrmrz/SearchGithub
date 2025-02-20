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
    fun getUser(
        @Path("login") login: String
    ): Flow<ApiResponse<User>>

    @GET("users/{login}/repos")
    fun getRepos(
        @Path("login") login: String
    ): Flow<ApiResponse<List<Repo>>>

    @GET("repos/{owner}/{name}")
    fun getRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Flow<ApiResponse<Repo>>

    @GET("repos/{owner}/{name}/contributors")
    fun getContributors(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Flow<ApiResponse<List<Contributor>>>

    @GET("search/repositories")
    fun searchRepos(
        @Query("q") query: String
    ): Flow<ApiResponse<RepoSearchResponse>>

    @GET("search/repositories")
    fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int
    ): Call<RepoSearchResponse>
}
