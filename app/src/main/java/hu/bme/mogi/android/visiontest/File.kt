package hu.bme.mogi.android.visiontest

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.util.*

object File {

    fun writeFileOnInternalStorage(sBody: String?) {
        val date = Date()
        val dir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val gpxfile = File(dir, date.month.toString()+date.day.toString()+".txt")
            val writer = FileWriter(gpxfile,true)
            writer.append(sBody)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}