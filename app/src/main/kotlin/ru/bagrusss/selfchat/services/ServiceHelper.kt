package ru.bagrusss.selfchat.services

import android.content.Context
import android.content.Intent

/**
 * Created by bagrusss.
 */

object ServiceHelper {

    @JvmStatic
    fun addData(context: Context, msg: String, type: Int, time: String, reqCode: Int) {
        val intent = Intent(context, ProcessorIntentService::class.java)
        with(intent) {
            action = ProcessorIntentService.ACTION_ADD_MESSAGE
            putExtra(ProcessorIntentService.PARAM_TIME, time)
            putExtra(ProcessorIntentService.PARAM_TYPE, type)
            putExtra(ProcessorIntentService.PARAM_MSG, msg)
            putExtra(ProcessorIntentService.PARAM_REQ_CODE, reqCode)
            context.startService(intent)
        }
    }

}
