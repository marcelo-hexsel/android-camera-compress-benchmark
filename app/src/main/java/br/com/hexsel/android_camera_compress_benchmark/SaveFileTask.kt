package br.com.hexsel.android_camera_compress_benchmark

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.*


class SaveFileTask : AsyncTask<ByteArray, String, String>() {

    override fun doInBackground(vararg jpeg: ByteArray): String? {
        val photo = File(Environment.getExternalStorageDirectory(), Date().toString() + ".jpg")

        if (photo.exists()) {
            photo.delete()
        }

        try {
            val fos = FileOutputStream(photo.getPath())

            fos.write(jpeg[0])
            fos.close()
        } catch (e: java.io.IOException) {
            Log.e("PictureDemo", "Exception in photoCallback", e)
        }

        return null
    }
}