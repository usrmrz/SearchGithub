package dev.usrmrz.searchgithub.data.api

import retrofit2.Response

sealed class ApiResponse<out T> {
    data class ApiSuccessResponse<out T>(val data: T) : ApiResponse<T>()
    data class ApiErrorResponse(val errorMessage: String) : ApiResponse<Nothing>()
    class ApiEmptyResponse<T> : ApiResponse<T>()

    companion object {
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(response.body()!!)
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error")
            }

//            return if (response.isSuccessful && response.body() != null) {
//                ApiSuccessResponse(response.body()!!)
//            } else {
//                ApiErrorResponse(response.errorBody()?.string() ?: "Unknown error")
//            }
        }

        fun <T> create(error: Throwable): ApiResponse<T> {
            return ApiErrorResponse(error.localizedMessage ?: "Unknown error")
        }
    }
}
