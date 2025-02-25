package dev.usrmrz.searchgithub.util

import android.util.Log
import com.google.gson.internal.`$Gson$Types`.getRawType
import dev.usrmrz.searchgithub.data.api.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

//class FlowCallAdapterFactory : CallAdapter.Factory() {

//    override fun get(
//        returnType: Type,
//        annotations: Array<out Annotation?>,
//        retrofit: Retrofit
//    ): CallAdapter<*, *>? {
//        Log.d("FCAF_get", "returnType: $returnType, annotations: $annotations, retrofit: $retrofit")
//        if(getRawType(returnType) != Flow::class.java) {
//            Log.d("FCAF_returnType", "getRawType: ${getRawType(returnType)}")
//            return null
//        }

//        val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
//        Log.d("FCAF_responseType", "responseType: $responseType")
//        return object : CallAdapter<Any, Flow<ApiResponse<Any>>> {
//            override fun responseType() = responseType
//            override fun adapt(call: Call<Any>): Flow<ApiResponse<Any>> = callbackFlow {
//                call.enqueue(object : Callback<Any> {
//                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
//                        Log.d("FCAF_onResponse", "response: $response")
//                        trySend(ApiResponse.create(response)).isSuccess
//                    }
//                    override fun onFailure(call: Call<Any>, throwable: Throwable) {
//                        Log.d("FCAF_onFailure", "call: $call, throwable: $throwable")
//                        trySend(ApiResponse.create(throwable)).isSuccess
//                    }
//                })
//                Log.d("FCAF_awaitClose", "call: $call")
//                awaitClose { call.cancel() }
//            }.flowOn(Dispatchers.IO)
//                try {
//                    emit(call.execute().body()!!)
//                } catch(e: Exception) {
//                    emit(ApiResponse.create<Any>(e))
//                }
//            }.flowOn(Dispatchers.IO)
//        }
//    }
//}
