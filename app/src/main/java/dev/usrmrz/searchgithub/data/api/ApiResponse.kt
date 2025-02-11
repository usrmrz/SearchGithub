package dev.usrmrz.searchgithub.data.api

import retrofit2.Response

sealed class ApiResponse<T> {
    companion object {
        //Обработка исключений
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        //Обработка ответа Retrofit
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return try {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null || response.code() == 204) {
                        ApiEmptyResponse()
                    } else {
                        ApiSuccessResponse(
                            body = body,
                            linkHeader = response.headers()["link"]
                        )
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    ApiErrorResponse(errorMsg ?: "unknown error")
                }
            } catch (e: Exception) {
                ApiErrorResponse(e.message ?: "unknown error")
            }
        }
    }
}

class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy {
        links[NEXT_LINK]?.let { next ->
            PAGE_PATTERN.find(next)?.groupValues?.get(1)?.toIntOrNull()
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("""<([^>]*)>;\s*rel="([^"]+)"""")
        private val PAGE_PATTERN = Regex("""page=(\d+)""")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            return LINK_PATTERN.matcher(this)
                .useResults { matcher ->
                    matcher.asSequence()
                        .associate { it.group(2)!! to it.group(1)!! }
                }
        }
    }
}

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()
