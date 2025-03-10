package dev.usrmrz.searchgithub.data.repository

import androidx.paging.PagingSource
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.api.toApiResponse
import dev.usrmrz.searchgithub.data.api.toSuccess
import dev.usrmrz.searchgithub.data.db.entity.mapper.toDomain
import dev.usrmrz.searchgithub.domain.model.Repo

abstract class SearchPagingSource(
    private val api: GithubService,
    private val query: String
) : PagingSource<Int, Repo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val page = params.key ?: 1
        return try {
            val response = api.searchRepos(query, page)
            LoadResult.Page(
                data = response.items,
                prevKey = if(page > 1) page - 1 else null,
                nextKey = response.nextPage
            )
        } catch(e: Exception) {
            LoadResult.Error(e)
        }
    }
}