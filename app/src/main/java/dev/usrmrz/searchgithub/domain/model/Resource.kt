package dev.usrmrz.searchgithub.domain.model

import android.R.attr.data


//@Suppress("unused")
sealed class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(SUCCESS)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
