package dev.usrmrz.searchgithub.data.repository

//import androidx.room.withTransaction
//import dev.usrmrz.searchgithub.data.api.ApiEmptyResponse
//import dev.usrmrz.searchgithub.data.api.ApiErrorResponse
//import dev.usrmrz.searchgithub.data.api.ApiResponse
//import dev.usrmrz.searchgithub.data.api.ApiSuccessResponse
//import dev.usrmrz.searchgithub.data.api.GithubService
//import dev.usrmrz.searchgithub.data.db.GithubDb
//import dev.usrmrz.searchgithub.data.db.entity.mapper.toEntity
//import dev.usrmrz.searchgithub.domain.model.RepoSearchResult
//import dev.usrmrz.searchgithub.domain.model.Resource
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import java.io.IOException
//import kotlin.math.exp
//
//class FetchNextSearchPageTask(
//    private val query: String,
//    private val api: GithubService,
//    private val db: GithubDb,
//    private val scope: CoroutineScope
//) {
//    private val _flowNext = MutableStateFlow<Resource<Boolean>?>(null)
//    val flowNext: StateFlow<Resource<Boolean>?> = _flowNext.asStateFlow()
//    private val scope: CoroutineScope
//        get() = scope
//    fun fetchNextPage(): Flow<Resource<Boolean>?> {
//        scope.launch {
//            val current = db.repoDao().findSearchResult(query)
//            if (current == null) {
//                _flowNext.emit(null)
//                return@launch
//            }
//            val nextPage = current.next
//            if (nextPage == null) {
//                _flowNext.emit(Resource.Success(false))
//                return@launch
//            }
//            val newValue = try {
//                val response = api.searchRepos(query, nextPage)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        val ids = ArrayList(current.repoIds)
//                        ids.addAll(apiResponse.body.items.map { it.id })
//                        val merged = RepoSearchResult(
//                            query, ids,
//                            apiResponse.body.total, apiResponse.nextPage
//                        )
//                        val repoEntities = apiResponse.body.items.map { it.toEntity() }
//                        db.withTransaction {
//                            db.repoDao().insert(merged.toEntity())
//                            db.repoDao().insertRepos(repoEntities)
//                        }
//                        Resource.Success(apiResponse.nextPage != null)
//                    }
//                    is ApiEmptyResponse -> {
//                        Resource.Success(false)
//                    }
//                    is ApiErrorResponse -> {
//                        Resource.Error(apiResponse.errorMessage, true)
//                    }
//                }
//            } catch (e: IOException) {
//                Resource.Error(e.message ?: "Unknown error", true)
//            }
//            _flowNext.emit(newValue)
//        }
//        return
//    }
//}


//class FetchNextSearchPageTask(
//    private val query: String,
//    private val api: GithubService,
//    private val db: GithubDb,
//) {
//    private val _flowNext = MutableStateFlow<Resource<Boolean>?>(null)
//    val flowNext: StateFlow<Resource<Boolean>?> = _flowNext.asStateFlow()
//
//    suspend fun fetchNextPage() {
//        coroutineScope {
//            val current = db.repoDao().findSearchResult(query)
//            if (current == null) {
//                _flowNext.emit(null)
//                return@coroutineScope
//            }
//            val nextPage = current.next
//            if (nextPage == null) {
//                _flowNext.emit(Resource.Success(false))
//                return@coroutineScope
//            }
//            val newValue = try {
//                val response = api.searchRepos(query, nextPage)
//                val apiResponse = ApiResponse.create(response)
//                when (apiResponse) {
//                    is ApiSuccessResponse -> {
//                        val ids = ArrayList(current.repoIds)
//                        ids.addAll(apiResponse.body.items.map { it.id })
//                        val merged = RepoSearchResult(
//                            query, ids,
//                            apiResponse.body.total, apiResponse.nextPage
//                        )
//                        val repoEntities = apiResponse.body.items.map { it.toEntity() }
//                        db.withTransaction {
//                            db.repoDao().insert(merged.toEntity())
//                            db.repoDao().insertRepos(repoEntities)
//                        }
//                        Resource.Success(apiResponse.nextPage != null)
//                    }
//                    is ApiEmptyResponse -> {
//                        Resource.Success(false)
//                    }
//                    is ApiErrorResponse -> {
//                        Resource.Error(apiResponse.errorMessage, true)
//                    }
//                }
//            } catch (e: IOException) {
//                Resource.Error(e.message ?: "Unknown error", true)
//            }
//            _flowNext.emit(newValue)
//        }
//    }
//}


