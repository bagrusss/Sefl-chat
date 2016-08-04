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
import ru.bagrusss.selfchat.util.getDate
import java.net.SocketTimeoutException

class ProcessorIntentService : IntentService("ProcessorIntentService") {

    companion object {

        val ACTION_ADD_MESSAGE = "ru.bagrusss.selfchat.services.action.ACTION_ADD_MESSAGE"
        val ACTION_SAVE_BMP = "ru.bagrusss.selfchat.services.action.ACTION_SAVE_BMP"
        val ACTION_SAVE_BMP_COMPRESSED = "ru.bagrusss.selfchat.services.action.ACTION_SAVE_BMP_COMPRESSED"
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

            val msg = intent.getStringExtra(PARAM_DATA)
            val type = intent.getIntExtra(PARAM_TYPE, 1)
            when (action) {
                ACTION_ADD_MESSAGE -> {
                    try {
                        val (status, id, date, time) = Net.sendMessageAndParse(type, msg)
                        if (status) {
                            HelperDB.getInstance(this).insertData(id!!, msg, date!!, time!!, type)
                        }
                    } catch (e: SocketTimeoutException) {
                        message.status = Message.ERROR
                        message.errorText = getString(R.string.timeout)
                    } catch (e: Exception) {

                    }

                }
                ACTION_SAVE_BMP -> {
                    val bmpPath = intent.extras.get(PARAM_BMP) as Uri
                    val (status, id, url, date, time) = Net.uploadFile(bmpPath.path)
                    if (status) {
                        val display = windowManager.defaultDisplay
                        val p = Point()
                        display.getSize(p)
                        val compressed = FileStorage.compressImg(bmpPath.path, Math.min(p.x, p.y), 0.7f)
                        //TODO добавть оригинал в базу и переделать адаптер
                        HelperDB.getInstance(this).insertData(id!!, compressed, date!!, time!!, HelperDB.TYPE_IMAGE)
                    }
                }
                ACTION_SAVE_BMP_COMPRESSED -> {
                    val display = windowManager.defaultDisplay
                    val p = Point()
                    display.getSize(p)
                    var file = intent.getStringExtra(PARAM_DATA)
                    file = FileStorage.compressBMP(this, p.x, p.y, file)
                    intent.putExtra(PARAM_DATA, file)
                    insertToDB(intent)
                }
                ACTION_INIT_RETROFIT -> {
                    Net.initAPI(intent.getStringExtra(PARAM_HTTP_URL))
                    message.status = Message.RETROFIT_READY
                }
                ACTION_UPDATE_MESSAGES -> {
                    try {
                        val msgs = Net.getMessages(this)
                        HelperDB.getInstance(this).insertMessages(msgs)
                    } catch (e: Exception) {
                        Log.e("exc ", e.message)
                        message.status = Message.ERROR
                    }
                }

            }
            EventBus.getDefault().post(message)
        }
    }


    fun insertToDB(intent: Intent) {
        val msg = intent.getStringExtra(PARAM_DATA)
        val type = intent.getIntExtra(PARAM_TYPE, 1)
        val time = intent.getStringExtra(PARAM_TIME)
        HelperDB.getInstance(this)
                .insertData(0, msg, getDate(), time, type)
    }

}
