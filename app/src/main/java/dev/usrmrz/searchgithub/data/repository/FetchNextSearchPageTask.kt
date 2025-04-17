package dev.usrmrz.searchgithub.data.repository

import android.util.Log
import androidx.room.withTransaction
import dev.usrmrz.searchgithub.data.api.ApiEmptyResponse
import dev.usrmrz.searchgithub.data.api.ApiErrorResponse
import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.ApiSuccessResponse
import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.api.RepoSearchResponse
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.data.db.entity.mapper.toEntity
import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
import dev.usrmrz.searchgithub.domain.model.Resource
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
class FetchNextSearchPageTask(
    private val query: String,
    private val api: GithubService,
    private val db: GithubDb,
) {
    fun asFlow(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        val current = db.repoDao().findSearchResult(query)

        Log.d(
            "FNP",
            "val current = db.repoDao().findSearchResult(query);;query: $query; next: ${current?.next};"
        )

        if(current == null || current.next == null) {
            emit(Resource.Success(false))
            return@flow
        }

        val newValue = try {
            val response = withContext(Dispatchers.IO) {
                api.searchRepos(query, current.next)
            }

            Log.d(
                "FNP",
                "val response = withContext;;current.next: ${current.next}; response: $response;"
            )

            when(val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse<RepoSearchResponse> -> {

                    Log.d(
                        "FNP",
                        "is ApiSuccessResponse;;apiResponse: $apiResponse;"
                    )

                    // we merge all repo ids into 1 list so that it is easier to fetch the
                    // result list.
                    val ids = ArrayList(current.repoIds).apply {
                        addAll(apiResponse.body.items.map { it.id })
                    }

                    val merged = RepoSearchResult(
                        query = query,
                        repoIds = ids,
                        totalCount = apiResponse.body.total,
                        next = apiResponse.nextPage
                    )

                    val repoEntities = apiResponse.body.items.map { it.toEntity() }

                    db.withTransaction {
                        db.repoDao().insert(merged.toEntity())
                        db.repoDao().insertRepos(repoEntities)
                    }

                    Resource.Success(apiResponse.nextPage != null)
                }

                is ApiEmptyResponse<RepoSearchResponse> -> Resource.Success(false)
                is ApiErrorResponse<RepoSearchResponse> -> Resource.Error(
                    apiResponse.errorMessage,
                    true
                )

            }

        } catch(e: IOException) {
            Resource.Error(e.message ?: "Unknown error", true)
        }

        emit(newValue)
    }.flowOn(Dispatchers.IO)
}
