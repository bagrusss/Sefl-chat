package ru.bagrusss.selfchat.services

import android.app.IntentService
import android.content.Intent
import org.greenrobot.eventbus.EventBus
import ru.bagrusss.selfchat.data.HelperDB
import ru.bagrusss.selfchat.eventbus.Message

class ProcessorIntentService : IntentService("ProcessorIntentService") {

    companion object {

        val ACTION_ADD_MESSAGE = "ru.bagrusss.selfchat.services.action.ACTION_ADD_MESSAGE"

        val PARAM_MSG = "message"
        val PARAM_TYPE = "type"
        val PARAM_TIME = "time"
        val PARAM_REQ_CODE = "rq_code"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            val reqCode = intent.getIntExtra(PARAM_REQ_CODE, 10)
            when (action) {
                ACTION_ADD_MESSAGE -> {
                    val msg = intent.getStringExtra(PARAM_MSG)
                    val type = intent.getIntExtra(PARAM_TYPE, 1)
                    val time = intent.getStringExtra(PARAM_TIME)
                    HelperDB.getInstance(this@ProcessorIntentService)
                            .insertData(msg, time, type)
                    EventBus.getDefault().post(Message(reqCode, Message.OK))
                }
            }
        }
    }

}
