package ru.bagrusss.selfchat.network

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.*

/**
 * Created by bagrusss.
 */
interface API {

    @POST("new")
    fun newMessage(@Body body: RequestBody): Call<Response<Msg>>

    @SerializedName("data")
    @GET("messages")
    fun messages(): Call<Response<ArrayList<Msg>>>

}