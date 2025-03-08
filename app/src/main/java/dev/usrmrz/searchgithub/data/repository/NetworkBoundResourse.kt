package dev.usrmrz.searchgithub.data.repository

import android.util.Log
import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.ApiResponse.ApiSuccessResponse
import dev.usrmrz.searchgithub.data.api.safeApiCall
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

abstract class NetworkBoundResource<ResultType, RequestType> {

//    fun asFlow(): Flow<Resource<ResultType>> = flow {
    fun asFlow() = flow {
//        emit(Resource.Loading())
        val dbValue = loadFromDb().first()
        Log.d("NBR", "val dbValue = loadFromDb().first();;dbValue: $dbValue")
        if(shouldFetch(dbValue)) {
            emit(Resource.Loading(dbValue))
            Log.d("NBR", "if(shouldFetch(dbValue)) { emit(Resource.Loading(dbValue));;dbValue: $dbValue")
            when(val apiResponse = safeApiCall { createCall() }) {
                is ApiSuccessResponse -> {
                    Log.d("NBR", "when(val apiResponse = safeApiCall { createCall() }) { is ApiResponse.Success -> {;;apiResponse: $apiResponse")
                    saveCallResult(apiResponse.data)
                    Log.d("NBR", "saveCallResult(apiResponse.data);;apiResponse: $apiResponse; apiResponse.d: ${apiResponse.body}")
                    val emittedFetchedDb = loadFromDb().map { Success(it) }
                    emitAll(emittedFetchedDb)
                    Log.d("NBR", "val emitted = loadFromDb().map { Resource.Success(it) } emitAll(emitted);;it: $emittedFetchedDb")
                }

                is ApiResponse.Empty -> {
                    val emittedEmpty = loadFromDb().map { Success(it) }
                    emitAll(emittedEmpty)
                    Log.d("NBR", "is ApiResponse.Empty -> { val eEmp = loadFromDb().map { Resource.Success(it) } emitAll(eEmp);;it: $emittedEmpty")
                }

                is ApiResponse.Error -> {
                    Log.d("NBR", "is ApiResponse.Error -> {")
                    onFetchFailed()
                    Log.d("NBR", "is ApiResponse.Error -> { onFetchFailed();;onFF: ${onFetchFailed()}")
                    val emittedError = loadFromDb().map { Error(apiResponse.errorMessage, it) }
                    emitAll(emittedError)
                    Log.d("NBR", "val eErr = loadFromDb().map { Resource.Error(apiResponse.errorMessage, it) } emitAll(eErr);;it: $emittedError")
                }

                ApiResponse.ApiEmptyResponse -> TODO()
                is ApiResponse.ApiErrorResponse -> TODO()
            }
        } else {
            val emittedShouldntFetch = loadFromDb().map { Resource.Success(it) }
            emitAll(emittedShouldntFetch)
            Log.d("NBR", "val eNF = loadFromDb().map { Resource.Success(it) } emitAll(eNF);;it: $emittedShouldntFetch")
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
