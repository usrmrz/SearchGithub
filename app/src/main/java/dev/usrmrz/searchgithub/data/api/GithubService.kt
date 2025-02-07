package dev.usrmrz.searchgithub.data.api

import dev.usrmrz.searchgithub.domain.model.Contributor
import dev.usrmrz.searchgithub.domain.model.RepoModel
import dev.usrmrz.searchgithub.domain.model.RepoSearchResponse
import dev.usrmrz.searchgithub.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {

    @GET("users/{login}")
    suspend fun getUser(@Path("login") login: String): ApiResponse<UserModel>

    @GET("users/{login}/repos")
    fun getRepos(@Path("login") login: String): Flow<ApiResponse<List<RepoModel>>>

    @GET("repos/{owner}/{name}")
    suspend fun getRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): ApiResponse<RepoModel>

    @GET("repos/{owner}/{name}/contributors")
    fun getContributors(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Flow<ApiResponse<List<Contributor>>>

    @GET("search/repositories")
    suspend fun searchReposApi(@Query("q") query: String): ApiResponse<RepoSearchResponse>

    @GET("search/repositories")
    fun searchReposApi(@Query("q") query: String, @Query("page") page: Int): ApiResponse<RepoSearchResponse>

//    @GET("search/repositories")
//    suspend fun searchReposApi(//GET https://api.github.com/search/repositories?q=best&sort=stars&order=desc
//        @Query("q") query: String = "language:kotlin",
//        @Query("sort") sort: String = "stars",
//        @Query("order") order: String = "desc"
//
//    ): RepoSearchResponse
}
