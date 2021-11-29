package hu.bme.mogi.android.visiontest.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import hu.bme.mogi.android.visiontest.File
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_results.*
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

class ResultsActivity : AppCompatActivity() {
//    enum class Type {VA,CONTRAST,COLOR}
//    var type = Type.VA
    var type = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        type = intent.getIntExtra("type",0)
        val result = when(type) {
            0 -> {
                when(intent.getIntExtra("result",0)) {
                    0 -> "HIGH"
                    1 -> "OKAY"
                    else -> "LOW"
                }
            }
            1 -> {
                when(intent.getIntExtra("result",0)) {
                    0 -> "HIGH"
                    else -> "LOW"
                }
            }
            else -> {
                when(intent.getIntExtra("result",0)) {
                    0 -> "GOOD"
                    else -> "AFFECTED"
                }
            }
        }

        when (type) {
            0 -> {
                topTV.text = "Visual acuity: $result"
                middleTV.text = "Next:\nContrast sensitivity"
            }
            1 -> {
                topTV.text = "Contrast Sensitivity: $result"
                middleTV.text = "Next:\nColor perception"
            }
            else -> {
                topTV.text = "Color perception: $result"
                bottomTV.text = "PRESS SPACE TO SAVE"
                middleTV.text = "TEST DONE!"
            }
        }

        bottomTV.setOnClickListener{
            next()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_BUTTON_X,  KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_BUTTON_START, KeyEvent.KEYCODE_BUTTON_SELECT -> {
                next()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    fun next() {
        if(type!=2) {
            val intent = if(type == 0) Intent(this, ContrastTestActivity::class.java)
            else Intent(this, ColorTestActivity::class.java)
            startActivity(intent)
        } else {
            createFile()
//            val intent = Intent(this,CalibrationActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, File.fileName)
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            when (resultCode) {
                RESULT_OK -> if (data != null
                    && data.data != null
                ) {
                    writeInFile(data.data!!, File.fileText)
                }
                RESULT_CANCELED -> {
                }
            }
        }
    }

    private fun writeInFile(uri: Uri, text: String) {
        val outputStream: OutputStream?
        try {
            outputStream = contentResolver.openOutputStream(uri)
            val bw = BufferedWriter(OutputStreamWriter(outputStream))
            bw.write(text)
            bw.flush()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}