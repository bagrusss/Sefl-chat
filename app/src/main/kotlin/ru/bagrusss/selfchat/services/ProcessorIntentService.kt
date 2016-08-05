package ru.bagrusss.selfchat.services

import android.app.IntentService
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.windowManager
import ru.bagrusss.selfchat.R
import ru.bagrusss.selfchat.data.HelperDB
import ru.bagrusss.selfchat.eventbus.Message
import ru.bagrusss.selfchat.network.Net
import ru.bagrusss.selfchat.util.FileStorage
import java.net.SocketTimeoutException

class ProcessorIntentService : IntentService("ProcessorIntentService") {

    companion object {

        val ACTION_ADD_MESSAGE = "ru.bagrusss.selfchat.services.action.ACTION_ADD_MESSAGE"
        val ACTION_SAVE_BMP = "ru.bagrusss.selfchat.services.action.ACTION_SAVE_BMP"
        val ACTION_INIT_RETROFIT = "ru.bagrusss.selfchat.services.action.INIT_RETROFIT"
        val ACTION_UPDATE_MESSAGES = "ru.bagrusss.selfchat.services.action.ACTION_UPDATE_MESSAGES"

        val PARAM_DATA = "data"
        val PARAM_TYPE = "type"
        val PARAM_TIME = "time"
        val PARAM_REQ_CODE = "rq_code"

        val PARAM_BMP = "bmp"
        val PARAM_HTTP_URL = "http_url"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            val reqCode = intent.getIntExtra(PARAM_REQ_CODE, 10)
            val message = Message(reqCode, Message.OK)

            when (action) {
                ACTION_ADD_MESSAGE -> {
                    val msg = intent.getStringExtra(PARAM_DATA)
                    val type = intent.getIntExtra(PARAM_TYPE, 1)
                    try {
                        val (status, id, date, time) = Net.sendMessageAndParse(type, msg)
                        if (status) {
                            HelperDB.getInstance(this).insertData(id!!, msg, date!!, time!!, type)
                        } else message.status = Message.ERROR
                    } catch (e: SocketTimeoutException) {
                        message.status = Message.ERROR
                        message.errorText = getString(R.string.timeout)
                    } catch (e: Exception) {
                        message.status = Message.ERROR
                        message.errorText = getString(R.string.something_wrong)
                    }

                }
                ACTION_SAVE_BMP -> {
                    val bmpUri = intent.extras.get(PARAM_BMP) as Uri
                    try {
                        val (status, id, url, date, time) = Net.uploadFile(bmpUri.path)
                        if (status) {
                            saveCompessed(bmpUri.path, id!!, date!!, time!!)
                        } else {
                            message.status = Message.ERROR
                            message.errorText = getString(R.string.cant_connect)
                        }
                    } catch (e: SocketTimeoutException) {
                        message.status = Message.ERROR
                        message.errorText = getString(R.string.timeout)
                    } catch (e: Exception) {
                        message.status = Message.ERROR
                        message.errorText = getString(R.string.something_wrong)
                    }
                }
                ACTION_INIT_RETROFIT -> {
                    Net.initAPI(intent.getStringExtra(PARAM_HTTP_URL))
                    message.status = Message.RETROFIT_READY
                }
                ACTION_UPDATE_MESSAGES -> {
                    try {
                        val msgs = Net.getMessages()
                        val lastMsg = HelperDB.getInstance(this).getLastMessageId()
                        var current = 0
                        for (msg in msgs) {
                            if (msg.timestamp > lastMsg)
                                break
                            ++current
                        }
                        //только новые
                        msgs.subList(0, current).clear()
                        var fileImg: String?
                        for (msg in msgs) {
                            if (msg.type == HelperDB.TYPE_IMAGE) {
                                fileImg = Net.downloadFile(msg.data)
                                if (fileImg != null)
                                    saveCompessed(fileImg, msg.timestamp, msg.date, msg.time)
                            } else {
                                HelperDB.getInstance(this)
                                        .insertData(msg.timestamp, msg.data, msg.date, msg.time, HelperDB.TYPE_TEXT)
                            }
                        }

                    } catch (e: SocketTimeoutException) {
                        message.status = Message.ERROR
                        message.errorText = getString(R.string.timeout)
                    } catch (e: Exception) {
                        Log.e("exc ", e.message)
                        message.status = Message.ERROR
                        message.errorText = getString(R.string.something_wrong)
                    }
                }

            }
            EventBus.getDefault().post(message)
        }
    }

    private fun saveCompessed(path: String, id: Long, date: String, time: String) {
        val p = getDisplayResolution()
        val compressed = FileStorage.compressImg(path, Math.max(p.x, p.y), 0.65f)
        HelperDB.getInstance(this).insertData(id, compressed, date, time, HelperDB.TYPE_IMAGE)
    }

    private fun getDisplayResolution(): Point {
        val display = windowManager.defaultDisplay
        val p = Point()
        display.getSize(p)
        return p
    }

}
