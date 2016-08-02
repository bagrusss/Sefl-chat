package ru.bagrusss.selfchat.eventbus

/**
 * Created by bagrusss.
 */
class Message(rc: Int, stat: Int) {
    companion object {
        val OK = 0
        val ERROR = 1
    }

    var status = stat
    var reqCode = rc


}