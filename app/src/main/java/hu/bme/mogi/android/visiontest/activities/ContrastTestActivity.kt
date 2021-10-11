package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.File
import hu.bme.mogi.android.visiontest.Noise
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_contrasttest.*
import kotlinx.android.synthetic.main.activity_contrasttest.downBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.leftBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.noiseView
import kotlinx.android.synthetic.main.activity_contrasttest.startButton
import kotlinx.android.synthetic.main.activity_contrasttest.rightBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.upBtnC
import kotlin.math.pow
import kotlin.math.roundToInt


class ContrastTestActivity: AppCompatActivity() {
//    var tryNumber: Int = 1
//    var results: FloatArray = floatArrayOf(0f, 0f, 0f)
    //TODO: apply aspect ratio to noise
    var prevDir = 5
    var level = 0
    var guesses = BooleanArray(6)
    var alphas = FloatArray(6)
    var frequencies = FloatArray(6)
    var alpha = 0.4f//0.15f
    var freq = 4f//7f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasttest)

        gaussView.alpha=0f

        startButton.setOnClickListener{
            prevDir = ViewMover.move(gaussView, noiseView, true)
            applyNoise()
            gaussView.alpha = alpha

            startButton.isEnabled=false
            startButton.setBackgroundColor(Color.TRANSPARENT)
        }


        if(resources.configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            upBtnC.isEnabled = true
            downBtnC.isEnabled = true
            leftBtnC.isEnabled = true
            rightBtnC.isEnabled = true
            //Toast.makeText(applicationContext,"Hello",Toast.LENGTH_SHORT).show()
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
        }

        setFrequency(freq)

        //set size
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val gaussParams = gaussView.layoutParams
        val gaussWidth = ViewMover.degreeToPixels(2.0, displayMetrics,getSharedPreferences("sp", Context.MODE_PRIVATE))
        //Toast.makeText(applicationContext,gaussWidth.toString(),Toast.LENGTH_SHORT).show()
        gaussParams.width = gaussWidth
        gaussParams.height = gaussWidth

    }

    //TODO: Tidy this
    private fun setFrequency(freq: Float){
        val source = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.black_square
        )
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        var hsv: FloatArray

        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                hsv= floatArrayOf(
                    0f,
                    0f,
                    Math.sin(Math.PI * 2 * x.toDouble() / (width / freq)).toFloat() / 2 + 0.5f
                )
                pixels[index] = Color.HSVToColor(hsv)
            }
        }

        val bmOut = Bitmap.createBitmap(width, height, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        gaussView.setImageBitmap(bmOut)
    }

    //TODO: lépcsős fentről-lentről megbecsülés
    private fun guess(dir: Int) {
        val correct = dir == prevDir
        guesses[level] = correct
        frequencies[level] = freq
        alphas[level] = alpha

        freq *= 1.3f
        alpha *= 0.87f
        setFrequency(freq)
        gaussView.alpha = alpha

        if(level == 5) {
            evaluate()
            return
        }

        prevDir = ViewMover.move(gaussView, noiseView, false)

        applyNoise()

        level++
    }

    private fun evaluate() {
        var fileText="\n\nCONTRAST SENSITIVITY:\n"
        var result = "High contrast sensitivity."
        var mistakeMade = false

        for (i in guesses.indices) {
            if(!guesses[i] && !mistakeMade){
                result = when(i) {
                    in 0..3 -> "Low contrast sensitivity."
                    else -> "Mostly adequate contrast sensitivity."
                }
                mistakeMade = true
            }
            val res = if(guesses[i]) "CORRECT"
            else "WRONG"
            val freqRound = round(frequencies[i],2)
            val alphaRound = round(alphas[i]*100,2)
            fileText+="\tFREQ: "+freqRound.toString()+"\tCONTR: "+alphaRound.toString()+"%\t"+res+"\n"
        }
        Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
        fileText+= "\tEVALUATION:\n\t$result\n"
        File.writeFileOnInternalStorage(fileText)
        finish()
    }

    private fun applyNoise() {
        noiseView.setImageBitmap(
            Noise.applyNoise(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.black_square
                ), 300, 600
            )
        )
    }

    private fun round(number: Float, decimalPlaces: Int): Float {
        val number3digits = (number * 10f.pow(decimalPlaces+2)).roundToInt() / 10f.pow(decimalPlaces+2)
        val number2digits = (number3digits * 10f.pow(decimalPlaces+1)).roundToInt() / 10f.pow(decimalPlaces+1)
        return (number2digits * 10f.pow(decimalPlaces)).roundToInt() / 10f.pow(decimalPlaces)
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
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

}