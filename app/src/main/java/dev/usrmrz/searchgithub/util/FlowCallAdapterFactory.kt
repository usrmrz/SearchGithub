package dev.usrmrz.searchgithub.util

import dev.usrmrz.searchgithub.data.api.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation?>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if(getRawType(returnType) != Flow::class.java) {
            return null
        }

        val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
        return object : CallAdapter<Any, Flow<Any>> {
            override fun responseType(): Type = responseType

            override fun adapt(call: Call<Any>): Flow<Any> = flow {
                try {
                    emit(call.execute().body()!!)
                } catch(e: Exception) {
                    emit(ApiResponse.create<Any>(e))
                }
            }.flowOn(Dispatchers.IO)
        }
    }
}