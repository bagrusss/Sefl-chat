package ru.bagrusss.selfchat.network

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
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

/*    @GET("messages")
    fun messages(): Call<JsonObject>*/

    @Multipart
    @POST("upload")
    fun upload(@Header("Content-type") contentType: String,
               @Part body: RequestBody): Call<JsonObject>

}