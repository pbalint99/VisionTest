package hu.bme.mogi.android.visiontest.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_contrasttest.*
import kotlinx.android.synthetic.main.activity_vatest.*
import java.util.*


class ContrastTestActivity: AppCompatActivity() {
    var tryNumber: Int = 1
    var results: FloatArray = floatArrayOf(0f, 0f, 0f)
    var firstpressed: Boolean = false
    var widthMiddle = 0; var heightMiddle = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasttest)

        gaussView.setImageResource(R.drawable.sin_feathered)
        gaussView.alpha=0f

        plusButton.setOnClickListener{
            if(!firstpressed) {
                plusButton.text="+"
                moveGauss()
                noiseView.setImageBitmap(
                    applyFleaEffect(
                        BitmapFactory.decodeResource(
                            applicationContext.resources,
                            R.drawable.black_square
                        )
                    )
                )
                firstpressed=true
            }
            gaussView.alpha+=0.01f
        }

        //Button Listeners:
        upBtnC.setOnClickListener{
            guess(0)
            moveGauss() //TODO: ne hagyd benne
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

//        nextButton.setOnClickListener{
//            if(tryNumber<3) {
//                noiseView.setImageBitmap(applyFleaEffect(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.black_square)))
//                moveGauss()
//                results[tryNumber-1]=gaussView.alpha
//                gaussView.alpha = 0.02f
//                tryNumber++
//            } else {
//                Toast.makeText(applicationContext,"Your result: "+(results.average()*100).roundToInt().toString()+"%",Toast.LENGTH_LONG).show()
//                finish()
//            }
//        }

//        gaussView.pivotX = gaussView.width.toFloat() / 2;
//        gaussView.pivotY = gaussView.height.toFloat()/ 2;
//        gaussView.setImageBitmap(
//            setFrequency(
//                BitmapFactory.decodeResource(
//                    applicationContext.resources,
//                    R.drawable.black_square
//                )
//            )
//        )

    }

    private fun setFrequency(source: Bitmap): Bitmap? {
        // get source image size
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        var hsv: FloatArray
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)


        val bmOut = Bitmap.createBitmap(width, height, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }


    private fun applyFleaEffect(source: Bitmap): Bitmap? {
        // get source image size
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        var hsv: FloatArray
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)
        // create a random object
        val random = Random()
        var index = 0
        // iteration through pixels
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get random color
//                val randColor: Int = Color.rgb(
//                    random.nextInt(255),
//                    random.nextInt(255), random.nextInt(255)
//                )
                hsv= floatArrayOf(0f, 0f, random.nextFloat())
                val randColor: Int = Color.HSVToColor(hsv)
                // OR
                pixels[index] = pixels[index] or randColor
            }
        }
        // output bitmap
        val bmOut = Bitmap.createBitmap(width, height, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    private fun moveGauss() {
        val params = gaussView.layoutParams as ConstraintLayout.LayoutParams
        val random = Random()
        val loc =  IntArray(2)
        widthMiddle = (noiseView.width - gaussView.width)/2
        heightMiddle = (noiseView.height - gaussView.height)/2

        when((0..3).random()) {
            0 -> {
                loc[0] = widthMiddle
                loc[1] = 0
            } //Up
            1 -> {
                loc[0] = noiseView.width - gaussView.width
                loc[1] = heightMiddle
            } //Right
            2 -> {
                loc[0] = widthMiddle
                loc[1] = noiseView.height - gaussView.height
            } //Down
            else -> {
                loc[0] = 0
                loc[1] = heightMiddle
            } //Left
        }
        params.setMargins(
//            random.nextInt(noiseView.width) - gaussView.width,
//            random.nextInt(noiseView.height) - gaussView.height,
            loc[0],
            loc[1],
            0,
            0
        )
        gaussView.layoutParams = params
        gaussView.rotation=random.nextInt(360).toFloat()
    }

    //TODO: lépcsős fentről-lenntről megbecsülés
    private fun guess(dir: Int) {
//        var min: Int = 100;
//        Toast.makeText(applicationContext,"x: "+(gaussView.pivotX/noiseView.width).toString()+", y: "+(gaussView.pivotY/noiseView.height).toString(),Toast.LENGTH_SHORT).show();
    }

}