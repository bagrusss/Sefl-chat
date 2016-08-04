package ru.bagrusss.selfchat.services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri


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
            putExtra(ProcessorIntentService.PARAM_DATA, msg)
            putExtra(ProcessorIntentService.PARAM_REQ_CODE, reqCode)
        }
        return intent
    }

    @JvmStatic
    fun addData(context: Context, msg: String, type: Int, time: String, reqCode: Int) {
        with(prepareIntent(context, type, msg, reqCode, time)) {
            action = ProcessorIntentService.ACTION_ADD_MESSAGE
            context.startService(this)
        }
    }

    fun saveBMP(context: Context, uri: Uri, type: Int, time: String, reqCode: Int) {
        with(prepareIntent(context, type, null, reqCode, time)) {
            putExtra(ProcessorIntentService.PARAM_BMP, uri)
            action = ProcessorIntentService.ACTION_SAVE_BMP
            context.startService(this)
        }
    }

    fun saveBMPCompressed(context: Context, url: String, type: Int, time: String, reqCode: Int) {
        val intent = prepareIntent(context, type, null, reqCode, time)
        with(intent) {
            action = ProcessorIntentService.ACTION_SAVE_BMP_COMPRESSED
            putExtra(ProcessorIntentService.PARAM_DATA, url)
            context.startService(this)
        }
    }

    @JvmStatic
    fun initRetrofit(context: Context, server: String) {
        with(Intent(context, ProcessorIntentService::class.java)) {
            this.putExtra(ProcessorIntentService.PARAM_HTTP_URL, server)
            this.action = ProcessorIntentService.ACTION_INIT_RETROFIT
            context.startService(this)
        }

    }

    @JvmStatic
    fun updateMessages(context: Context, reqCode: Int) {
        val intent = Intent(context, ProcessorIntentService::class.java)
        with(intent) {
            action = ProcessorIntentService.ACTION_UPDATE_MESSAGES
            putExtra(ProcessorIntentService.PARAM_REQ_CODE, reqCode)
            context.startService(this)
        }
    }

}
