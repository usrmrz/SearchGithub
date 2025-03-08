package dev.usrmrz.searchgithub.data.api

import dev.usrmrz.searchgithub.data.db.entity.ContributorEntity
import dev.usrmrz.searchgithub.data.db.entity.RepoEntity
import dev.usrmrz.searchgithub.data.db.entity.UserEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//@Suppress("unused")
interface GithubService {

    @GET("users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ): UserEntity

    @GET("users/{login}/repos")
    suspend fun getRepos(
        @Path("login") login: String
    ): List<RepoEntity>

    @GET("repos/{owner}/{name}")
    suspend fun getRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): RepoEntity

    @GET("repos/{owner}/{name}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): List<ContributorEntity>

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String
    ): RepoSearchResponse

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int
    ): RepoSearchResponse
}
