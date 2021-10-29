package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.File
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_contrasttest.*
import kotlinx.android.synthetic.main.activity_contrasttest.demoView
import kotlinx.android.synthetic.main.activity_contrasttest.gaussView
import kotlinx.android.synthetic.main.activity_contrasttest.noiseView
import kotlinx.android.synthetic.main.activity_contrasttest.textView
import kotlinx.android.synthetic.main.activity_contrasttest_keyboard.*
import java.io.FileOutputStream
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin


class ContrastTestActivity: AppCompatActivity() {
//    var tryNumber: Int = 1
//    var results: FloatArray = floatArrayOf(0f, 0f, 0f)
    //TODO: apply aspect ratio to noise
    var prevDir = 5
    var level = 0
    var guesses = BooleanArray(6)
    var alphas = FloatArray(6)
    var frequencies = FloatArray(6)
    var alpha = 0.06f
    var cpd = 1.5f
    var started = false
    var displayMetrics = DisplayMetrics()
    var noiseNumber = 0
    var keyboardConnected = true
    lateinit var passButton : MenuItem
    lateinit var menuText : MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(resources.configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            setContentView(R.layout.activity_contrasttest)
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
            startButton.setOnClickListener{
                start()
                startButton.isEnabled=false
                startButton.text=""
                startButton.setBackgroundColor(Color.TRANSPARENT)
                upBtnC.setBackgroundColor(Color.BLACK)
                rightBtnC.setBackgroundColor(Color.BLACK)
                downBtnC.setBackgroundColor(Color.BLACK)
                leftBtnC.setBackgroundColor(Color.BLACK)
                menuText.title = "Can't see:"
                passButton.isVisible=true
            }
            keyboardConnected = false
        } else setContentView(R.layout.activity_contrasttest_keyboard)

        setFrequency(cpd)
        gaussView.alpha=0f

        //set size
        displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val gaussParams = gaussView.layoutParams
        val gaussWidth = ViewMover.degreeToPixels(
            2.0, displayMetrics, getSharedPreferences(
                "sp",
                Context.MODE_PRIVATE
            )
        )
        //Toast.makeText(applicationContext,gaussWidth.toString(),Toast.LENGTH_SHORT).show()
        gaussParams.width = gaussWidth
        gaussParams.height = gaussWidth

    }

    private fun setFrequency(cpd: Float) {
        val source = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.black_square
        )
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        var hsv: FloatArray
        val freq = (cpd*4).roundToInt()/2
        //val svd = File.smallestVisibleInDegrees
        Toast.makeText(applicationContext,(cpd*2).toString()+" "+freq.toString(),Toast.LENGTH_SHORT).show()

        //if(svd/2<cpd)

        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                hsv= floatArrayOf(
                    0f,
                    0f,
                    sin(2f/freq + (6.28f * x.toFloat() * freq) / width ) / 2 + 0.5f
                )
                pixels[index] = Color.HSVToColor(hsv)
                index++
            }
        }

        val bmOut = Bitmap.createBitmap(width, height, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        gaussView.setImageBitmap(bmOut)
        File.saveImage(bmOut)
    }

    //TODO: lépcsős fentről-lentről megbecsülés
    private fun guess(dir: Int) {
        val correct = dir == prevDir
        guesses[level] = correct
        frequencies[level] = cpd
        alphas[level] = alpha

        //Values by Mirella:
        cpd *= 1.3195f
        alpha *= 0.8705f
        gaussView.alpha = alpha

        if(level == 5) {
            evaluate()
            return
        }

        prevDir = ViewMover.move(gaussView, noiseView, true)

        applyNoise()
        setFrequency(cpd)

        level++
    }

    private fun evaluate() {
        var fileText="\n\nCONTRAST SENSITIVITY:\n"
        var result = "High contrast sensitivity."
        var mistakeMade = false

        for (i in guesses.indices) {
            if(!guesses[i] && !mistakeMade){
                result = when(i) {
                    in 0..4 -> "Low contrast sensitivity."
                    else -> "Mostly adequate contrast sensitivity."
                }
                mistakeMade = true
            }
            val res = if(guesses[i]) "CORRECT"
            else "WRONG"
            val freqRound = round(frequencies[i], 2)
            val alphaRound = round(alphas[i] * 100, 2)
            fileText+="\tCPD: "+freqRound.toString()+"\tCONTR: "+alphaRound.toString()+"%\t"+res+"\n"
        }
        Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
        fileText+= "\tEVALUATION:\n\t$result\n"
        File.writeFileOnInternalStorage(fileText)
        val intent = Intent(this, ColorActivity::class.java)
        startActivity(intent)
    }

    private fun applyNoise() {
        when(noiseNumber) {
            0 -> noiseView.setImageResource(R.drawable.noise1)
            1 -> noiseView.setImageResource(R.drawable.noise2)
            2 -> noiseView.setImageResource(R.drawable.noise3)
            else -> noiseView.setImageResource(R.drawable.noise4)
        }
        noiseNumber++
        if(noiseNumber==4) noiseNumber=0
    }

    private fun round(number: Float, decimalPlaces: Int): Float {
        val number3digits = (number * 10f.pow(decimalPlaces + 2)).roundToInt() / 10f.pow(
            decimalPlaces + 2
        )
        val number2digits = (number3digits * 10f.pow(decimalPlaces + 1)).roundToInt() / 10f.pow(
            decimalPlaces + 1
        )
        return (number2digits * 10f.pow(decimalPlaces)).roundToInt() / 10f.pow(decimalPlaces)
    }

    fun start() {
        prevDir = ViewMover.move(gaussView, noiseView, true)
        applyNoise()
        gaussView.alpha = alpha

        demoView.alpha=0f
        textView.text=""
        menuText.isVisible=true
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
            KeyEvent.KEYCODE_SPACE -> {
                if (!started) {
                    start()
                    started = true
                    startTextView.setBackgroundColor(Color.TRANSPARENT)
                    startTextView.text = ""
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

}