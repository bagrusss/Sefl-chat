package ru.bagrusss.selfchat.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.apache.commons.io.FilenameUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.bagrusss.selfchat.network.json.Msg
import ru.bagrusss.selfchat.network.json.Response
import ru.bagrusss.selfchat.util.FileStorage
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by bagrusss.
 */

object Net {

    @JvmStatic val PARAM_DATA = "data"
    @JvmStatic val PARAM_TYPE = "type"

    @JvmStatic val CONTENT_JSON = "application/json"
    @JvmStatic val UPLOAD_URL = "/upload"

    @JvmStatic private var mApi: API? = null

    @JvmStatic private var BASE_URL: String? = null
    @JvmStatic private var GSON = GsonBuilder().setLenient().create()

    @JvmStatic
    private fun createRetrofit(url: String): API {
        BASE_URL = url
        val okHttpClient = OkHttpClient
                .Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GSON))
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
    fun getMessages(): ArrayList<Msg> {
        val res = Net.getAPI().messages().execute().body()
        return res.data!!
    }

    //Загружать и выгружить можно и через Retrofit, но это не эффективно

    data class Result5(val status: Boolean, val id: Long?, val url: String?, val date: String?, val time: String?)

    @JvmStatic
    fun uploadFile(file: String): Result5 {
        val url = URL(BASE_URL + UPLOAD_URL)
        val connection = url.openConnection() as HttpURLConnection
        var time: String? = null
        var date: String? = null
        var data: String? = null
        var status = false
        var id: Long? = null
        try {
            with(connection) {
                useCaches = false
                doOutput = true
                requestMethod = "POST"
                setRequestProperty("Content-type", "image/" + FilenameUtils.getExtension(file))
                connect()
            }
            val lineEnd = "\r\n"
            val twoHyphens = "--"
            val boundary = "*****"
            val dos = DataOutputStream(connection.outputStream)
            dos.use {
                FileInputStream(File(file)).use {
                    var bytesAvailable = it.available()
                    var bufferSize = Math.min(bytesAvailable, 2048)
                    val buffer = ByteArray(bufferSize)
                    var bytesRead = it.read(buffer, 0, bufferSize)
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize)
                        bytesAvailable = it.available()
                        bufferSize = Math.min(bytesAvailable, 2048)
                        bytesRead = it.read(buffer, 0, bufferSize)
                    }
                    dos.writeBytes(lineEnd)
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
                    dos.flush()
                }
            }
            var resp: Response<Msg>? = null
            BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use {
                val type = object : TypeToken<Response<Msg>>() {}.type
                resp = GSON.fromJson(it, type)
            }
            val msg = resp!!.data!!
            id = msg.timestamp
            date = msg.date
            data = msg.data
            time = msg.time
            status = true
        } catch (e: IOException) {
            Log.e("upload", e.message)
        } finally {
            connection.disconnect()
        }
        return Result5(status, id, data, date, time)
    }

    @JvmStatic
    fun downloadFile(path: String): String? {
        val url = URL(BASE_URL + path)
        var filePath: String? = null
        val connection = url.openConnection() as HttpURLConnection
        try {
            with(connection) {
                useCaches = false
                requestMethod = "GET"
                connect()
            }
            val ext = connection.getHeaderField("Content-Type").replace("image/", "")
            val data = ByteArray(2048)
            val file = FileStorage.createTemporaryFile(ext)
            val fos = FileOutputStream(file)
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                fos.use {
                    connection.inputStream.use {
                        var count = it.read(data)
                        while (count != -1) {
                            fos.write(data, 0, count)
                            count = it.read(data)
                        }
                    }
                }
                filePath = file.absolutePath
            }
        } catch (e: IOException) {
            Log.e("upload", e.message)
            filePath = null
        } finally {
            connection.disconnect()
        }
        return filePath
    }

}