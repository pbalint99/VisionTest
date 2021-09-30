package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.Noise
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_contrasttest.*
import kotlinx.android.synthetic.main.activity_contrasttest.downBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.leftBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.noiseView
import kotlinx.android.synthetic.main.activity_contrasttest.plusButton
import kotlinx.android.synthetic.main.activity_contrasttest.rightBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.upBtnC


class ContrastTestActivity: AppCompatActivity() {
//    var tryNumber: Int = 1
//    var results: FloatArray = floatArrayOf(0f, 0f, 0f)
    //TODO: apply aspect ratio to noise
    private var firstpressed: Boolean = false
    var prevDir = 5
    var level = 0
    var results = BooleanArray(6)
    var alphas = FloatArray(6)
    var freq = 5f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasttest)

        gaussView.alpha=0f
        upBtnC.isEnabled = false
        downBtnC.isEnabled = false
        leftBtnC.isEnabled = false
        rightBtnC.isEnabled = false

        plusButton.setOnClickListener{
            if(!firstpressed) {
                plusButton.text="+"
                prevDir = ViewMover.move(gaussView, noiseView, true)
                applyNoise()
                firstpressed=true

                upBtnC.isEnabled = true
                downBtnC.isEnabled = true
                leftBtnC.isEnabled = true
                rightBtnC.isEnabled = true
            }
            gaussView.alpha+=0.01f
        }

        //Button Listeners:
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

    //TODO: lépcsős fentről-lenntről megbecsülés
    private fun guess(dir: Int) {
        val correct = dir == prevDir
        results[level] = correct
        alphas[level] = gaussView.alpha

        if(level%2==1) {
            freq += 3
            setFrequency(freq)
        }

        if(level == 5) {
            evaluate()
            return
        }

        prevDir = ViewMover.move(gaussView, noiseView, false)

        applyNoise()

        gaussView.alpha=0f

        level++
    }

    private fun evaluate() {
        var correct = 0
        for (element in results) {
            if (element) correct++
        }
        Toast.makeText(applicationContext, String.format("%.2f", alphas.average())+" on avarage", Toast.LENGTH_SHORT).show()
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

}