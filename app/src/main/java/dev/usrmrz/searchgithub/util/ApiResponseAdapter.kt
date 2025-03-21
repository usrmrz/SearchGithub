package dev.usrmrz.searchgithub.util

//import android.util.Log
//import com.google.gson.JsonDeserializationContext
//import com.google.gson.JsonDeserializer
//import com.google.gson.JsonElement
//import dev.usrmrz.searchgithub.data.api.ApiEmptyResponse
//import dev.usrmrz.searchgithub.data.api.ApiErrorResponse
//import dev.usrmrz.searchgithub.data.api.ApiResponse
//import dev.usrmrz.searchgithub.data.api.ApiSuccessResponse
//import java.lang.reflect.Type

//class ApiResponseAdapter<T>(private val type: Type) : JsonDeserializer<ApiResponse<T>> {

//    override fun deserialize(
//        json: JsonElement,
//        typeOfT: Type,
//        context: JsonDeserializationContext,
//    ): ApiResponse<T> {
//        if(json.isJsonNull || (json.isJsonObject && json.asJsonObject.entrySet().isEmpty())) {
//            Log.d(
//                "ApiRsAdp",
//                "Received empty JSON -> returning ApiEmptyResponse - json: $json"
//            )
//            return ApiEmptyResponse()
//        }
//        val jsonObject = json.asJsonObject
//        Log.d("ApiRsAdp", "obj Deserializing JSON: $jsonObject")
//        return when {
//            jsonObject.has("body") -> {
//                val body = context.deserialize<T>(jsonObject.get("body"), type)
//                val links = jsonObject.get("link")?.asJsonObject?.entrySet()
//                    ?.associate { it.key to it.value.asString } ?: emptyMap()
//                Log.d("ApiRsAdp", "errorMessage: ${jsonObject.has("message")}")
//                ApiSuccessResponse(body, links)
//            }
//            jsonObject.has("errorMessage") -> {
//                ApiErrorResponse(jsonObject.get("errorMessage").asString)
//            }
//            else -> {
//                ApiEmptyResponse()
//            }
//        }
//    }
//}


//class ApiResponseAdapter<T>(private val type: Type) : JsonDeserializer<ApiResponse<T>> {

//    override fun deserialize(
//        json: JsonElement,
//        typeOfT: Type,
//        context: JsonDeserializationContext,
//    ): ApiResponse<T> {
//        if(json.isJsonNull || (json.isJsonObject && json.asJsonObject.entrySet().isEmpty())) {
//            Log.d(
//                "ApiRsAdp_isEmpty",
//                "Received empty JSON -> returning ApiEmptyResponse - json: $json"
//            )
//            return ApiResponse.Empty
//        }
//        val obj = json.asJsonObject
//        Log.d("ApiRsAdp_deserialize", "obj Deserializing JSON: $obj")
//        return when {
//            obj.has("message") -> {
//                Log.d("ApiRsAdp_errorMessage", "errorMessage: ${obj.has("message")}")
//                ApiResponse.Error(obj["message"].asString)
//            }

//            else -> {
//                val body: T = context.deserialize(json, typeOfT)
//                Log.d("ApiRsAdp_else_Success", "type: $type, Parsed body: $body")
//                ApiResponse.Success(body)
//            }
//        }

//            obj.has("body") -> {
//                Log.d("ApiRsAdp_b", "obj = $obj")
//                val body: T = context.deserialize(obj["body"], type)
//                Log.d("ApiRsAdp_b1", "body = $body, obj: $obj, typeOfT: $type")
//
//                ApiSuccessResponse(body, obj["links"]?.asString)
//            }
//            else -> ApiEmptyResponse()
//        }
//    }
//}