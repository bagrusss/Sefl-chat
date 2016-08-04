package ru.bagrusss.selfchat.network

import com.google.gson.annotations.SerializedName

/**
 * Created by bagrusss.
 */

class Msg {

    @SerializedName("data")
    var data: String = ""

    @SerializedName("date")
    var date: String = ""

    @SerializedName("time")
    var time: String = ""

    @SerializedName("timestamp")
    var timestamp: Long = 0

    @SerializedName("type")
    var type: Int = 1

    override fun toString(): String{
        return "Msg(data='$data', date='$date', time='$time', timestamp=$timestamp, type=$type)"
    }

}