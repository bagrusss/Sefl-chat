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
    fun saveBMPtoStorage(bmp: Bitmap): String? {
        if (isExternalStorageWritable() && isExternalStorageReadable()) {
            val file = File(getPathIMG(), "" + getTime().hashCode() + ".jpg")
            FileOutputStream(file).use {
                bmp.compress(Bitmap.CompressFormat.JPEG, 85, it)
            }
            bmp.recycle()
            return Uri.fromFile(file).toString()
        }
        return null
    }

    @JvmStatic
    @Throws(IOException::class)
    fun compressBMP(cont: Context, x: Int, y: Int, uri: String): String? {
        val oldPath = getRealPathFromURI(cont, Uri.parse(uri))
        val oldFile = File(oldPath)
        var bmp: Bitmap? = null
        FileInputStream(oldFile).use {
            bmp = decodeBMPtoScreenSize(x, y, BitmapFactory.decodeStream(it))
        }
        val oldExif = ExifInterface(oldPath)
        val exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)

        val newFile = File(getPathIMG(), "" + getTime().hashCode() + ".cache")
        val newPath = newFile.path
        newFile.createNewFile()
        FileOutputStream(newFile).use {
            bmp?.compress(Bitmap.CompressFormat.JPEG, 85, it)
        }
        bmp?.recycle()
        if (exifOrientation != null) {
            val newExif = ExifInterface(newPath)
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
            newExif.saveAttributes()
        }
        return Uri.fromFile(newFile).toString()
    }

    @JvmStatic
    fun compressImg(path: String, maxSize: Int, k: Float): String {
        val oldFile = File(path)
        var oldImg: Bitmap? = null
        val oldExif = ExifInterface(path)
        val oldWidth = oldExif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 1)
        val oldLength = oldExif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 1)
        val orientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)
        val newWidth = maxSize * k
        val newLength = newWidth / oldWidth * oldLength
        if (newLength >= oldLength) {
            return path
        }
        FileInputStream(oldFile).use {
            oldImg = BitmapFactory.decodeStream(it)
        }
        val newImg = Bitmap.createScaledBitmap(oldImg, newWidth.toInt(), newLength.toInt(), false)
        val newPath = path + ".cache"
        FileOutputStream(File(newPath)).use {
            newImg.compress(Bitmap.CompressFormat.JPEG, 85, it)
            oldImg!!.recycle()
        }
        val newExif = ExifInterface(newPath)
        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation)
        newExif.saveAttributes()
        return newPath
    }

    @JvmStatic
    fun decodeBMPtoScreenSize(x: Int, y: Int, source: Bitmap): Bitmap {
        val size = Math.max(x, y) //screen
        var currentX = source.width
        var currentY = source.height
        var currentMaxSize = Math.max(currentX, currentY)
        while (currentMaxSize > size) {
            currentX /= 2
            currentY /= 2
            currentMaxSize = Math.max(currentX, currentY)
        }
        val res = Bitmap.createScaledBitmap(source, currentX, currentY, false)
        if (res != source) {
            source.recycle()
        }
        return res
    }


    private fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
        val uri = contentURI.toString()
        if (uri.indexOf("file://", 0, true) != -1)
            return contentURI.path

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
        return result
    }

    @Throws(Exception::class)
    fun createTemporaryFile(part: String, ext: String): File {
        return File(getPathIMG(), part + '.' + ext)
    }

}