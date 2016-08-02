package ru.bagrusss.selfchat.services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap

/**
 * Created by bagrusss.
 */

object ServiceHelper {

    @JvmStatic
    private fun prepareIntent(context: Context, type: Int, msg: String?, reqCode: Int, time: String): Intent {
        val intent = Intent(context, ProcessorIntentService::class.java)
        with(intent) {
            putExtra(ProcessorIntentService.PARAM_TIME, time)
            putExtra(ProcessorIntentService.PARAM_TYPE, type)
            putExtra(ProcessorIntentService.PARAM_MSG, msg)
            putExtra(ProcessorIntentService.PARAM_REQ_CODE, reqCode)
        }
        return intent
    }

    @JvmStatic
    fun addData(context: Context, msg: String, type: Int, time: String, reqCode: Int) {
        val intent = prepareIntent(context, type, msg, reqCode, time)
        with(intent) {
            action = ProcessorIntentService.ACTION_ADD_MESSAGE
            context.startService(intent)
        }
    }

    fun saveBMP(context: Context, bmp: Bitmap, type: Int, time: String, reqCode: Int) {
        val intent = prepareIntent(context, type, null, reqCode, time)
        with(intent) {
            putExtra(ProcessorIntentService.PARAM_BMP, bmp)
            action = ProcessorIntentService.ACTION_SAVE_BMP
            context.startService(intent)
        }

    }

}
