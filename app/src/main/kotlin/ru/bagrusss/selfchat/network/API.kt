package ru.bagrusss.selfchat.network

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by bagrusss.
 */
interface API {

    @POST("new")
    fun newMessage(@Body body: RequestBody): Call<JsonObject>

    @GET("messages")
    fun messages(): Call<JsonObject>

    @Multipart
    @POST("upload{file}")
    fun upload(@Path("file") filename: String,
               @Header("Content-type") header: String,
               @Part body: RequestBody): Call<JsonObject>

}