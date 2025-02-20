package dev.usrmrz.searchgithub.data.repository

import android.util.Log
import dev.usrmrz.searchgithub.data.api.ApiEmptyResponse
import dev.usrmrz.searchgithub.data.api.ApiErrorResponse
import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.ApiSuccessResponse
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<ResultType, RequestType> {

    fun asFlow() = flow<Resource<ResultType>> {
        emit(Resource.loading(null))
        val dbDate = loadFromDb().firstOrNull()
        if(shouldFetch(dbDate)) {
            emit(Resource.loading(dbDate))
            createCall().collect { apiResponse ->
                when(apiResponse) {
                    is ApiSuccessResponse<RequestType> -> {
                        saveCallResult(processResponse(apiResponse))
                        Log.d("N_B_R_1","apiResponse: $apiResponse")
                        emit(Resource.success(loadFromDb().first()))
                    }

                    is ApiEmptyResponse<RequestType> -> {
                        Log.d("N_B_R_2","dbDate: $dbDate")
                        emit(Resource.loading(dbDate))
                    }

                    is ApiErrorResponse<RequestType> -> {
                        Log.d("N_B_R_3","apiResponse: $apiResponse dbDate: $dbDate")
                        emit(Resource.error(null, "ApiErrorResponse<RequestType> is launching"))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    protected open fun processResponse(response: ApiSuccessResponse<RequestType>): RequestType =
        response.body

    protected abstract suspend fun saveCallResult(item: RequestType)

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun loadFromDb(): Flow<ResultType>

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>
}
