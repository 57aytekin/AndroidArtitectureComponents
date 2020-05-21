package com.example.yeniappwkotlin.data.network

import com.example.yeniappwkotlin.util.ApiException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.lang.StringBuilder

abstract class SafeApiRequest {

    suspend fun <T: Any> apiRequest(call : suspend() -> Response<T>) : T {
        val response = call.invoke()
        val message = StringBuilder()

        if (response.isSuccessful){
            return response.body()!!
        }else{
            try {
                val error = response.errorBody()?.string()

                error?.let {
                    try {
                        message.append(JSONObject(it).getString("message"))
                    }catch (e : JSONException){ }
                    message.append("\n")
                }
                message.append("Error Code: ${response.code()}")
            }catch ( e : ApiException){
                e.printStackTrace()
            }
            throw ApiException(message.toString())
        }
    }
}