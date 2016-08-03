package ru.bagrusss.selfchat.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.bagrusss.selfchat.data.HelperDB
import java.util.concurrent.TimeUnit

/**
 * Created by bagrusss.
 */

object Net {

    @JvmStatic val PARAM_DATA = "data"
    @JvmStatic val PARAM_TYPE = "type"

    @JvmStatic val CONTENT_JSON = "application/json"

    @JvmStatic private var mApi: API? = null

    @JvmStatic
    private fun createRetrofit(url: String): API {
        val okHttpClient = OkHttpClient
                .Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
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

    data class Result4(val status: Boolean, val id: Long?, val date: String?, val time: String?)

    @JvmStatic
    fun sendMessageAndParse(type: Int, text: String): Result4 {
        val jo = JsonObject()
        jo.addProperty(PARAM_TYPE, type)
        jo.addProperty(PARAM_DATA, text)
        val resp = Net.getAPI()
                .newMessage(RequestBody.create(MediaType.parse(CONTENT_JSON),
                        jo.toString()))
                .execute()
                .body()
        val status = resp.status == 0
        var time: String? = null
        var date: String? = null
        var id: Long? = null
        if (status) {
            time = resp.data?.time
            date = resp.data?.date
            id = resp.data?.timestamp
        }
        return Result4(status, id, date, time)
    }

    @JvmStatic
    fun getMessages(c: Context): List<Msg> {
        val res = Net.getAPI().messages().execute().body()
        val msgs = res.data
        val lastMsg = HelperDB.getInstance(c).getLastMessageId()
        var current = 0
        for (msg in msgs!!) {
            if (msg.timestamp > lastMsg) {
                break
            }
            ++current
        }
        msgs.subList(0, current).clear()
        return msgs
    }

    @JvmStatic
    fun uploadFile(file: String, url: String) {

    }

    fun downloadFile(url: String) {

    }

}