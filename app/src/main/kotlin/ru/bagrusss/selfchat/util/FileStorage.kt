package ru.bagrusss.selfchat.util

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by bagrusss.
 */
object FileStorage {

    @JvmStatic
    fun getPathIMG(): String {
        val file = File(Environment.getExternalStorageDirectory().path + "/selfchat/")
        if (!file.exists())
            file.mkdir()
        return file.path
    }

    @JvmStatic
    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(state)
    }

    @JvmStatic
    fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun saveBMPtoStorage(bmp: Bitmap): String? {
        if (isExternalStorageWritable() && isExternalStorageReadable()) {
            val path = getPathIMG() + bmp.hashCode() + ".jpg"
            val file = File(path)
            FileOutputStream(file).use {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            return Uri.fromFile(file).toString()
        }
        return null
    }


}