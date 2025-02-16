package dev.usrmrz.searchgithub.domain.model

//@Suppress("unused")
/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<T>(val data: T? = null, val message: String? = null) {
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(data)
        }

        fun <T> error(data: T?, msg: String): Resource<T> {
            return Resource(data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(data)
        }
    }
}


//version with sealed class
//sealed class Resource<T>(
//    val data: T? = null,
//    val error: Throwable? = null
//) {
//    class Success<T>(data: T) : Resource<T>(data)
//    class Loading<T>(data: T? = null) : Resource<T>(data)
//    class Error<T>(error: Throwable, data: T? = null) : Resource<T>(data, error)
//}

