package dev.usrmrz.searchgithub.data.repository

import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.safeApiCall
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

abstract class NetworkBoundResource<ResultType, RequestType> {

    fun asFlow() = flow<Resource<ResultType>> {
        emit(Resource.Loading())
        val dbValue = loadFromDb().first()
        if (shouldFetch(dbValue)) {
            emit(Resource.Loading(dbValue))
            when (val apiResponse = safeApiCall { createCall() }) {
                is ApiResponse.Success -> {
                    saveCallResult(apiResponse.data)
                    emitAll(loadFromDb().map { Resource.Success(it) })
                }
                is ApiResponse.Empty -> {
                    emitAll(loadFromDb().map { Resource.Success(it) })
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    emitAll(loadFromDb().map { Resource.Error(apiResponse.errorMessage, it) })
                }
            }
        } else {
            emitAll(loadFromDb().map { Resource.Success(it) })
        }
    }
    protected open fun onFetchFailed() {}
    protected abstract suspend fun saveCallResult(item: RequestType)
    protected abstract fun shouldFetch(data: ResultType?): Boolean
    protected abstract suspend fun createCall(): RequestType
    protected abstract fun loadFromDb(): Flow<ResultType>
}

//abstract class NetworkBoundResource<ResultType, RequestType> {
//    private var result: Flow<Resource<ResultType>> = flow {
//        emit(Resource.loading())
//        val dbDate = loadFromDb().first()
//        Log.d("NBR_loadFromDb", "dbDate: $dbDate")
//        if(shouldFetch(dbDate)) {
//            Log.d("NBR_shouldFetch", "shouldFetch: ${shouldFetch(dbDate)}, dbDate: $dbDate")
//            emit(Resource.loading(dbDate))
//            createCall().collect { apiResponse ->
//                Log.d("NBR_collect", "apiResponse: $apiResponse")
//                when(val apiResponse = createCall().first()) {
//                    is ApiResponse.Success<RequestType> -> {
//                        Log.d("NBR_1_Success", "ApiResponse.Success: $apiResponse")
//                        saveCallResult(processResponse(apiResponse))
//                        Log.d("NBR_2_Success", "apiResponse: $apiResponse")
//                        emit(Resource.success(loadFromDb().first()))
//                    }
//                    is ApiEmptyResponse<RequestType> -> {
//                        Log.d("NBR_3_Empty", "ApiEmptyResponse: $dbDate")
//                        emit(Resource.loading(dbDate))
//                    }
//                    is ApiErrorResponse<RequestType> -> {
//                        Log.d("NBR_4_Error", "apiResponse: $apiResponse dbDate: $dbDate")
//                        emit(Resource.error(null, "ApiErrorResponse<RequestType> is launching"))
//                    }
//                }
//            }
//        } else {
//            val newDate = loadFromDb().first()
//            emit(Resource.success(newDate))
//        }
//    }
//    protected open fun onFetchFailed() {}
//    protected open fun processResponse(response: ApiSuccessResponse<RequestType>): RequestType =
//        response.body
//    protected abstract suspend fun saveCallResult(item: RequestType)
//    protected abstract fun shouldFetch(data: ResultType?): Boolean
//    protected abstract fun loadFromDb(): Flow<ResultType>
//    protected abstract suspend fun createCall(): RequestType
//    fun asFlow(): Flow<Resource<ResultType>> = result
//}
