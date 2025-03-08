package dev.usrmrz.searchgithub.domain.model

//@Suppress("unused")
/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
//version with sealed class
sealed class Resource<T>(
    val status: Status,
    val data: T?,
    val message: String? = null
) {
    class Success<T>(
        data: T
    ) : Resource<T>(Status.SUCCESS, data)

    class Loading<T>(
        data: T? = null
    ) : Resource<T>(Status.LOADING, data)

    class Error<T>(
        message: String,
        data: T? = null
    ) : Resource<T>(Status.ERROR, data, message)
}

//data class Resource<T>(val status: Status, val data: T? = null, val message: String? = null) {
//    companion object {
//        fun <T> success(data: T): Resource<T> {
//            return Resource(Status.SUCCESS, data)
//        }
//        fun <T> error(data: T?, msg: String): Resource<T> {
//            return Resource(Status.ERROR, data, msg)
//        }
//        fun <T> loading(data: T? = null): Resource<T> {
//            return Resource(Status.LOADING, data)
//        }
//    }
//}
