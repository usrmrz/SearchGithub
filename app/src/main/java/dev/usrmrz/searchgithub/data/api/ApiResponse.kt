package dev.usrmrz.searchgithub.data.api

import android.util.Log
import retrofit2.Response
import java.util.regex.Pattern

//@Suppress("unused")
// T is used in extending classes
sealed class ApiResponse<out T> {
    companion object {
        fun <T> create(error: Throwable): ApiResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if(response.isSuccessful) {
                val body = response.body()
                val linkHeader = response.headers()["link"]
                Log.d(
                    "ApiR",
                    "fun <T> create(response: Response<T>): ApiResponse<T>;;linkHeader: $linkHeader"
                )
                Log.d("ApiR", "fun <T> create(response;;body: $body, response: $response")
                if(body == null || response.code() == 204) {
                    Log.d(
                        "ApiR",
                        "fun <T> create(response; body == null || response.code() == 204;;body: $body, response.cd: ${response.code()}"
                    )
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(
                        body = body,
//                        linkHeader = response.headers()["link"],
                        linkHeader = linkHeader,
                    )

                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if(msg.isNullOrEmpty()) {
                    response.message()
                    Log.d(
                        "ApiR",
                        "val errorMsg = if(msg.isNullOrEmpty()) { response.message();;response: $response, response.mes: ${response.message()}"
                    )
                } else {
                    Log.d("ApiR", "if(msg.isNullOrEmpty()) else;;msg: $msg")
                    msg
                }
                ApiErrorResponse(errorMsg.toString())
            }
            Log.d("ApiR", "End response: $response")
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */


class ApiEmptyResponse<T> : ApiResponse<T>()


data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    ) {
        Log.d("ApiR", "links: $links")
        Log.d("ApiR", "linkHeader: $linkHeader")
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>\\s*;\\s*rel=\"([a-z]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)
            Log.d("ApiR", "links: $links, matcher: $matcher")
            while(matcher.find()) {
                val count = matcher.groupCount()
                if(count == 2) {
                    links[matcher.group(2)!!] = matcher.group(1)!!
                }
            }
            Log.d("ApiR", "links: $links, count: ${matcher.groupCount()}")
            return links
        }
    }

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        val nextUrl = links[NEXT_LINK]
        Log.d("ApiR", "nextUrl: $nextUrl; NEXT_LINK: $NEXT_LINK")
        nextUrl?.let {
            val matcher = PAGE_PATTERN.matcher(it)
            if(matcher.find() && matcher.groupCount() == 1) {
                try {
                    val page = Integer.parseInt(matcher.group(1)!!)
                    Log.d("ApiR", "Page: $page")
                    page
                } catch(ex: NumberFormatException) {
                    Log.d("ApiR", "Cannot parse next page from %s because $ex")
                    null
                }
            } else {
                Log.d("ApiR", "No match found parse next page from $it")
                null
            }
        }
    }
}

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResponse<T> {
    return try {
        val response = apiCall()
        if(response.isSuccessful) {
            Log.d("AR", "response.isSuccessful;;response: $response")
            val body = response.body()
            val linkHeader = response.headers()["link"]
            if(body == null || response.code() == 204) {
                Log.d("AR", "ApiEmptyResponse;;linkHeader: $linkHeader; body: $body")
                ApiEmptyResponse()
            } else {
                Log.d("AR", "ApiSuccessResponse;;linkHeader: $linkHeader; body: $body")
                ApiSuccessResponse(body, linkHeader)
            }
        } else {
            val msg = response.errorBody()?.string()
            val errorMsg = if(msg.isNullOrEmpty())
                response.message() else msg
            Log.d("AR", "ApiErrorResponse;;errorMsg: $errorMsg")
            ApiErrorResponse(errorMsg)
        }
    } catch(e: Exception) {
        ApiErrorResponse(e.message ?: "An unknown error occurred")
    }
}

//suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
//    return try {
//        val response = apiCall()
//        if(response != null) {
//            Log.d("AR", "if(links != null) {;;response: $response")
//            ApiSuccessResponse(response, "2")
//        } else {
//            ApiEmptyResponse()
//        }
//    } catch(e: Exception) {
//        ApiErrorResponse(e.message ?: "An unknown error occurred")
//    }
//}



