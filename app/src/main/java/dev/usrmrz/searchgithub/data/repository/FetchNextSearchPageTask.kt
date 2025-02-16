package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.GithubService
import dev.usrmrz.searchgithub.data.db.GithubDb
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FetchNextSearchPageTask(
//    private val query: String,
//    private val api: GithubService,
//    private val db: GithubDb,
) {
    private val _flowNext = MutableStateFlow<Resource<Boolean>?>(null)
    val flowNext: StateFlow<Resource<Boolean>?> = _flowNext.asStateFlow()

//    fun fetchNextPage() {
//        coroutineScope().launch {
//            val current = db.repoDao().findSearchResult(query)
//            if (current == null) {
//                _flowNext.emit(null)
//                return@launch
//            }
//            val nextPage = current.next
//            if (nextPage == null) {
//                _flowNext.emit(Resource.success(false))
//                return@launch
//            }
//            val newValue = try {
//                val response = api.searchRepos(query, nextPage)
//                val apiResponse = ApiResponse.create(response)
//                when (apiResponse) {
//                    is ApiSuccessResponse<*> -> {
//                        val ids = ArrayList(current.repoIds)
//                        ids.addAll(apiResponse.body.items.map { it.id })
//                        val merged = RepoSearchResult(
//                            query, ids,
//                            apiResponse.body.total, apiResponse.nextPage
//                        )
//                        db.withTransaction {
//                            db.repoDao().insert(merged)
//                            db.repoDao().insertRepos(apiResponse.body.items)
//                        }
//                        Resource.success(apiResponse.nextPage != null)
//                    }
//                    is ApiEmptyResponse<*> -> {
//                        Resource.success(false)
//                    }
//                    is ApiErrorResponse<*> -> {
//                        Resource.error(apiResponse.errorMessage, true)
//                    }
//                }
//            } catch (e: IOException) {
//                Resource.error(e.message ?: "Unknown error", true)
//            }
//            _flowNext.emit(newValue)
//        }
//    }
}