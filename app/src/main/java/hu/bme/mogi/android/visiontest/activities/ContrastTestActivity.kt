package hu.bme.mogi.android.visiontest.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.Noise
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_contrasttest.*


class ContrastTestActivity: AppCompatActivity() {
//    var tryNumber: Int = 1
//    var results: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var firstpressed: Boolean = false
    private var widthMiddle = 0; private var heightMiddle = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasttest)

        gaussView.alpha=0f

        plusButton.setOnClickListener{
            if(!firstpressed) {
                plusButton.text="+"
                ViewMover.move(gaussView,noiseView,true)
                noiseView.setImageBitmap(
                    Noise.applyNoise(
                        BitmapFactory.decodeResource(
                            applicationContext.resources,
                            R.drawable.black_square
                        ),300,600
                    )
                )
                firstpressed=true
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

        setFrequency(5f)

    }

    private fun setFrequency(freq: Float){
        val source = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.black_square)
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        var hsv: FloatArray
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                hsv= floatArrayOf(0f, 0f, Math.sin(Math.PI*2*x.toDouble()/(width/freq)).toFloat()/2+0.5f)
                val randColor: Int = Color.HSVToColor(hsv)
                // "OR" the two variables
                pixels[index] = pixels[index] or randColor
                //TODO: or nem kell
            }
        }

        val bmOut = Bitmap.createBitmap(width, height, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        gaussView.setImageBitmap(bmOut)
    }

    //TODO: lépcsős fentről-lenntről megbecsülés
    private fun guess(dir: Int) {
//        var min: Int = 100;
//        Toast.makeText(applicationContext,"x: "+(gaussView.pivotX/noiseView.width).toString()+", y: "+(gaussView.pivotY/noiseView.height).toString(),Toast.LENGTH_SHORT).show();
    }

}