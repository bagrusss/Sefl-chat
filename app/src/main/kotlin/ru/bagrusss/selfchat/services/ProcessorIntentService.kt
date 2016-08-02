package ru.bagrusss.selfchat.services

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import org.greenrobot.eventbus.EventBus
import ru.bagrusss.selfchat.data.HelperDB
import ru.bagrusss.selfchat.eventbus.Message
import ru.bagrusss.selfchat.util.FileStorage

class ProcessorIntentService : IntentService("ProcessorIntentService") {

    companion object {

        val ACTION_ADD_MESSAGE = "ru.bagrusss.selfchat.services.action.ACTION_ADD_MESSAGE"
        val ACTION_SAVE_BMP = "ru.bagrusss.selfchat.services.action.ACTION_SAVE_BMP"
        val ACTION_SAVE_BMP_COMPRESSED = "ru.bagrusss.selfchat.services.action.ACTION_SAVE_BMP_COMPRESSED"

        val PARAM_DATA = "message"
        val PARAM_TYPE = "type"
        val PARAM_TIME = "time"
        val PARAM_REQ_CODE = "rq_code"

        val PARAM_BMP = "bmp"
        val PARAM_X = "x"
        val PARAM_Y = "y"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            val reqCode = intent.getIntExtra(PARAM_REQ_CODE, 10)
            when (action) {
                ACTION_ADD_MESSAGE -> {
                    insertToDB(intent)
                }
                ACTION_SAVE_BMP -> {
                    val bmp = intent.extras.get(PARAM_BMP) as Bitmap
                    val file = FileStorage.saveBMPtoStorage(bmp)
                    intent.putExtra(PARAM_DATA, file)
                    insertToDB(intent)
                }
                ACTION_SAVE_BMP_COMPRESSED -> {
                    val x = intent.getIntExtra(PARAM_X, 1)
                    val y = intent.getIntExtra(PARAM_Y, 1)
                    var file = intent.getStringExtra(PARAM_DATA)
                    file = FileStorage.saveCompressed(this, x, y, file)
                    intent.putExtra(PARAM_DATA, file)
                    insertToDB(intent)
                }

            }
            EventBus.getDefault().post(Message(reqCode, Message.OK))
        }
    }


    fun insertToDB(intent: Intent) {
        val msg = intent.getStringExtra(PARAM_DATA)
        val type = intent.getIntExtra(PARAM_TYPE, 1)
        val time = intent.getStringExtra(PARAM_TIME)
        HelperDB.getInstance(this@ProcessorIntentService)
                .insertData(msg, time, type)
    }

}
