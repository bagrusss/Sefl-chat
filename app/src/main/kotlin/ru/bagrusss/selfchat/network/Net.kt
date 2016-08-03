package ru.bagrusss.selfchat.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.bagrusss.selfchat.services.ProcessorIntentService
import java.util.concurrent.TimeUnit

/**
 * Created by bagrusss.
 */

object Net {

    @JvmStatic
    private var mApi: API? = null

    @JvmStatic
    private fun createRetrofit(url: String): API {
        val okHttpClient = OkHttpClient
                .Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .build()
        return retrofit.create(API::class.java)
    }

    @JvmStatic
    fun initAPI(url: String): API {
        if (mApi == null)
            mApi = createRetrofit(url)
        return mApi!!
    }

    @JvmStatic
    fun getAPI(): API {
        return mApi!!
    }

    data class Result3(val status: String, val date: String?, val time: String?)

    @JvmStatic
    fun sendMessageAndParse(type: Int, text: String): Result3 {
        var jo = JsonObject()
        jo.addProperty(ProcessorIntentService.PARAM_TYPE, type)
        jo.addProperty(ProcessorIntentService.PARAM_DATA, text)
        jo = Net.getAPI()
                .newMessage(RequestBody.create(MediaType.parse("application/json"),
                        jo.toString()))
                .execute()
                .body()
        val status = jo.get("status").asString
        if ("ok".equals(status)) {
            val time = jo.get("time").asString
            val date = jo.get("date").asString
            return Result3(status, date, time)
        } else {
            return Result3(status, null, null)
        }
    }

}