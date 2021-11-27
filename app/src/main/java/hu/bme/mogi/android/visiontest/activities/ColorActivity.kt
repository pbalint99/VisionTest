package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.File
import hu.bme.mogi.android.visiontest.Noise
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_color.*
import kotlinx.android.synthetic.main.activity_color.textView
import kotlinx.android.synthetic.main.activity_color_keyboard.*
import kotlinx.android.synthetic.main.activity_contrasttest.downBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.leftBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.noiseView
import kotlinx.android.synthetic.main.activity_contrasttest.startButton
import kotlinx.android.synthetic.main.activity_contrasttest.rightBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.upBtnC
import kotlinx.android.synthetic.main.activity_contrasttest_keyboard.*
import kotlinx.android.synthetic.main.activity_contrasttest_keyboard.startTextView
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.math.round

class ColorActivity: AppCompatActivity() {
    var bgColor = floatArrayOf(57f,245f) //Ishihara: 57f
    var dotColor = floatArrayOf(31f,65f) //Ishihara:31f
    var prevDir = 5
    var level = 0
    var guesses = BooleanArray(5)
    private var index = 0
    var dotScreenRatio = 0.2f
    private lateinit var passButton : MenuItem
    private lateinit var menuText : MenuItem
    private var started = false
    private var dotViews = arrayOfNulls<ImageView>(4)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameControllers = File.getGameControllerIds()

        dotViews[0] = dotViewUp
        dotViews[1] = dotViewRight
        dotViews[2] = dotViewDown
        dotViews[3] = dotViewLeft

        if(resources.configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES &&
            gameControllers.isEmpty()) {
            setContentView(R.layout.activity_color)
            startButton.setOnClickListener {
                start()
                upBtnC.isEnabled = true
                downBtnC.isEnabled = true
                leftBtnC.isEnabled = true
                rightBtnC.isEnabled = true
                passButton.isEnabled=true
                menuText.title = "Can't see:"
                startButton.isEnabled = false
                startButton.text = ""
                startButton.setBackgroundColor(Color.TRANSPARENT)
            }
            upBtnC.setOnClickListener{
                guess(0)
            }
            rightBtnC.setOnClickListener{
                guess(1)
            }
            downBtnC.setOnClickListener{
                guess(2)
            }
            leftBtnC.setOnClickListener{
                guess(3)
            }
        } else setContentView(R.layout.activity_color_keyboard)

    }

    private fun guess(dir: Int) {
        if(!started) return
        guesses[level] = dir == prevDir

        if(level == 4) {
            evaluate()
            return
        }
//        if(level == 4) {
//            index++
//        }
        //TODO:
        //prevDir = ViewMover.move(dotView, noiseView, false)

        applyNoises(index)

        level++
    }

    private fun evaluate() {
        var correct = 0
        for (element in guesses) {
            if (element) correct++
        }

        var fileText="\n\nCOLOR PERCEPTION:\n"
        var result = "Trichromat."

        for (i in guesses.indices) {
            if(!guesses[i]){
                result = "Not trichromat"
            }
            val res = if(guesses[i]) "CORRECT"
            else "WRONG"
            fileText+="\t"+res+"\n"
        }
        fileText+= "\tEVALUATION:\n\t$result\n"
        File.fileText+=fileText
        createFile()
        //finish()
    }

    private fun start()  {
        textView.text = ""

        //TODO:
        //prevDir = ViewMover.move(dotView, noiseView, false)

        menuText.isVisible=true

        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        Noise.setup(displayMetrics)

        val dotWidth = ViewMover.degreeToPixels(1.0,displayMetrics,getSharedPreferences("sp", Context.MODE_PRIVATE))
        dotScreenRatio = dotWidth.toFloat()/displayMetrics.widthPixels
        for (i in dotViews.indices) {
            val dotParams = dotViews[i]?.layoutParams
            dotParams?.width = dotWidth
            dotParams?.height = dotWidth
        }

        applyNoises(0)

        started = true
    }

    //TODO: also for button control
    private fun applyNoises(index: Int) {
        noiseView.setImageBitmap(
            Noise.applyNoise()
        )
        dotViewLeft.setImageBitmap(
            Noise.applyNoiseAmorphous(0f, dotScreenRatio)
        )
        dotViewRight.setImageBitmap(
            Noise.applyNoiseAmorphous(0f, dotScreenRatio)
        )
        dotViewUp.setImageBitmap(
                Noise.applyNoiseAmorphous(0f, dotScreenRatio)
                )
        dotViewDown.setImageBitmap(
            Noise.applyNoiseAmorphous(0f, dotScreenRatio)
        )
//        decoyView.setImageBitmap(
//            Noise.applyDecoys(dotScreenRatio)
//        )
//        decoyView.scaleType = ImageView.ScaleType.FIT_XY
//        decoyView.setImageBitmap(
//            Noise.applyDots(dotScreenRatio)
//        )
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        if (menu != null) {
            passButton = menu.getItem(1)
            menuText = menu.getItem(0)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        guess(4)
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                guess(0)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                guess(1)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                guess(2)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                guess(3)
                true
            }
            KeyEvent.KEYCODE_ESCAPE -> {
                true
            }
            KeyEvent.KEYCODE_ENTER -> {
                true
            }
            KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_BUTTON_X,  KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_BUTTON_START, KeyEvent.KEYCODE_BUTTON_SELECT -> {
                if (!started) {
                    start()
                    startTextView.setBackgroundColor(Color.TRANSPARENT)
                    startTextView.text = ""
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
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