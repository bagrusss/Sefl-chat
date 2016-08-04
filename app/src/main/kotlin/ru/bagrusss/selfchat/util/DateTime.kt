package ru.bagrusss.selfchat.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by bagrusss.
 */

val timeFormat = SimpleDateFormat("HH:mm.ss", Locale("ru"))
val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("ru"))

fun getTime(): String {
    return timeFormat.format(Date())
}

fun getDate(): String {
    return dateFormat.format(Date())
}

fun getTimestamp(): String {
    return timestamp.format(Date())
}