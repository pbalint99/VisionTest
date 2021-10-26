package hu.bme.mogi.android.visiontest

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.*

object File {
    var smallestVisibleInDegrees = 0f

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

    fun saveImage(finalBitmap: Bitmap) {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val fname = "Image.jpg"
        val file = File(dir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}