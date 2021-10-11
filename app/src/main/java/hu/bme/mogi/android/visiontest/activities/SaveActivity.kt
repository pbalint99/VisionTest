package hu.bme.mogi.android.visiontest.activities

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_save.*
import java.io.File
import java.io.FileWriter
import java.util.*

class SaveActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        val vaTestActivity = VATestActivity()
        val guesses = vaTestActivity.guesses

        save.setOnClickListener{
            val date = Date()
            var text = ""
            for (i in guesses.indices) {
                text += guesses[i].toString()
            }
            writeFileOnInternalStorage(name.text.toString()+date.month.toString()+
                    date.day.toString()+date.time.toString()+".txt",text)
        }
    }

    fun writeFileOnInternalStorage(sFileName: String, sBody: String?) {
        val dir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val gpxfile = File(dir, sFileName)
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}