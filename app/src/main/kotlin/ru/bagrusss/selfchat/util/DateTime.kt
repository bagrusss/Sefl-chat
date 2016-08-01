package ru.bagrusss.selfchat.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by bagrusss.
 */

val cal = Calendar.getInstance()!!
val timeFormat = SimpleDateFormat("HH:mm:ss", Locale("ru"))
val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))

fun getTime(): String {
    return timeFormat.format(cal.time)
}

fun getDate(): String {
    return dateFormat.format(cal.time)
}