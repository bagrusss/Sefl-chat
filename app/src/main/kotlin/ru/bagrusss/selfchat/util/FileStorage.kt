package ru.bagrusss.selfchat.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by bagrusss.
 */
object FileStorage {

    @JvmStatic
    fun getPathIMG(): File {
        val file = File(Environment.getExternalStorageDirectory().absolutePath, "selfchat")
        if (!file.exists())
            file.mkdir()
        return file
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
    fun saveBMPtoStorage(bmp: Bitmap): File? {
        if (isExternalStorageWritable() && isExternalStorageReadable()) {
            val file = File(getPathIMG(), "" + getTime().hashCode() + ".jpg")
            FileOutputStream(file).use {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            bmp.recycle()
            return file
        }
        return null
    }


    @JvmStatic
    @Throws(IOException::class)
    fun compressImg(path: String, maxSize: Int, k: Float): String {
        val oldFile = File(path)
        var oldImg: Bitmap? = null
        val oldExif = ExifInterface(path)
        var oldWidth = oldExif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 1)
        var oldLength = oldExif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 1)
        FileInputStream(oldFile).use {
            oldImg = BitmapFactory.decodeStream(it)
        }
        if (oldWidth == 0 || oldLength == 0) {
            oldWidth = oldImg!!.width
            oldLength = oldImg!!.height
        }
        val orientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)
        val newWidth = maxSize * k
        val newLength = newWidth / oldWidth * oldLength
        if (newLength >= oldLength) {
            oldImg!!.recycle()
            return path
        }
        val newImg = Bitmap.createScaledBitmap(oldImg, newWidth.toInt(), newLength.toInt(), false)
        val newPath = path + ".cache"
        FileOutputStream(File(newPath)).use {
            newImg.compress(Bitmap.CompressFormat.JPEG, 100, it)
            oldImg!!.recycle()
        }
        val newExif = ExifInterface(newPath)
        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation)
        newExif.saveAttributes()
        return newPath
    }

    fun getFileURI(context: Context, contentURI: Uri): Uri {
        var result: String? = null
        context.contentResolver.query(contentURI, null, null, null, null).use {
            if (it == null)
                result = contentURI.path
            else {
                it.moveToFirst()
                val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = it.getString(idx)
            }
        }
        return Uri.parse(result)
    }

    @Throws(Exception::class)
    fun createTemporaryFile(ext: String): File {
        return File(getPathIMG(), getTimestamp() + '.' + ext)
    }

}