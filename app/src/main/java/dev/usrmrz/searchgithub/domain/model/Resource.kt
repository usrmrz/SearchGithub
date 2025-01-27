package dev.usrmrz.searchgithub.domain.model

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> loading(data: T? = null): Resource<T> = Loading(data)
        fun <T> error(message: String, data: T? = null): Resource<T> = Error(message, data)
    }
}
