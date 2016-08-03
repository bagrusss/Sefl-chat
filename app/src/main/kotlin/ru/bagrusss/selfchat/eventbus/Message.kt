package ru.bagrusss.selfchat.eventbus

/**
 * Created by bagrusss.
 */
open class Message(rc: Int, stat: Int) {
    companion object {
        val OK = 0
        val ERROR = 1
        val RETROFIT_READY = 2
    }

    var status = stat
    var reqCode = rc
    var errorText = ""

}
