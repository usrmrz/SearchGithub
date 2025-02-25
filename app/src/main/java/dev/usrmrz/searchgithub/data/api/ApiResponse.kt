package dev.usrmrz.searchgithub.data.api

import android.util.Log
import retrofit2.Response


/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
//@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val errorMessage: String) : ApiResponse<Nothing>()
    object Empty : ApiResponse<Nothing>()

    companion object {
        fun <T> create(error: Throwable): ApiResponse<T> {
            return Error(error.message ?: "unknown error")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if(response.isSuccessful) {
                val body = response.body()
                val json = response.body()?.toString()
                Log.d("ApiRsp_create_r0", "JSON Response: $json")
                Log.d("ApiRsp_create_r1", "body: $body, response: $response")
                if(body == null || response.code() == 204) {
                    Log.d("ApiRsp_create_r2", "body: $body, response.cd: ${response.code()}")
                    Empty
                } else {
                    Success(
                        data = body
//                        body = body,
//                        linkHeader = response.headers()["link"]
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if(msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                Error(errorMsg ?: "unknown error")
            }
        }
    }
}


suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
    return try {
        val response = apiCall()
        if(response != null) {
            ApiResponse.Success(response)
        } else {
            ApiResponse.Empty
        }
    } catch(e: Exception) {
        ApiResponse.Error(e.message ?: "An unknown error occurred")
    }
}


/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
//class ApiEmptyResponse<T> : ApiResponse<T>()
//data class ApiSuccessResponse<T>(
//    val body: T,
//    val links: Map<String, String>
//) : ApiResponse<T>() {
//    constructor(body: T, linkHeader: String?) : this(
//        body = body,
//        links = linkHeader?.extractLinks() ?: emptyMap()
//    )
//    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
//        Log.d("ApiRsp_1", "nextPage: $nextPage body: $body links: $links")
//        links[NEXT_LINK]?.let { next ->
//            val matcher = PAGE_PATTERN.matcher(next)
//            if(!matcher.find() || matcher.groupCount() != 1) {
//                null
//            } else {
//                try {
//                    Integer.parseInt(matcher.group(1)!!)
//                } catch(ex: NumberFormatException) {
//                    Log.d("cannot parse next page from %s because $ex", next)
//                    null
//                }
//            }
//        }
//    }
//    companion object {
//        private val LINK_PATTERN =
//            Pattern.compile("<([^>]*)>[\\\\s]*;[\\\\s]*rel=\"([a-zA-Z0-9]+)\"")
//        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
//        private const val NEXT_LINK = "next"
//        private fun String.extractLinks(): Map<String, String> {
//            val links = mutableMapOf<String, String>()
//            val matcher = LINK_PATTERN.matcher(this)
//            Log.d("ApiRsp_2", "links: $links, matcher: $matcher")
//            while(matcher.find()) {
//                val count = matcher.groupCount()
//                if(count == 2) {
//                    links[matcher.group(2)!!] = matcher.group(1)!!
//                }
//            }
//            Log.d("ApiRsp_3", "links: $links, count: ${count()}")
//            return links
//        }
//    }
//}
//data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()


