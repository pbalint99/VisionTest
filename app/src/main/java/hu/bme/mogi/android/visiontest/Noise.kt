package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.util.DisplayMetrics
import java.util.*

//TODO: do this on a side thread?
//Use other createbitmap method, which takes a color array
object Noise {
    const val colorWiggleRoom = 30f
    const val satWiggleRoom = 0.5f
    const val circleColorDensity = 0.3f

    //TODO: noise density absed on distance?
    fun applyNoise(source: Bitmap, color: Float = -1f, displayMetrics: DisplayMetrics, density: Int): Bitmap {
        var hsv: FloatArray
        //if color is not set, set saturation to 0
        val sat = if(color == -1f) 0f
        else 1f
        val random = Random()
        var index = 0
        val aspectRatio = displayMetrics.widthPixels.toFloat()/displayMetrics.heightPixels
        val width= (displayMetrics.density*density*aspectRatio).toInt()
        val height = (displayMetrics.density*density).toInt()
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                hsv= floatArrayOf(color-colorWiggleRoom/2+colorWiggleRoom*random.nextFloat(),
                    sat - satWiggleRoom*random.nextFloat(), random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
                index++
            }
        }
        return Bitmap.createBitmap(pixels, width, height, source.config)
    }

    //dp, density could be stored in a local var
    fun applyNoiseCircle(source: Bitmap, color: Float, displayMetrics: DisplayMetrics, density: Int, dotScreenRatio: Float = 0.2f): Bitmap {
        var hsv: FloatArray
        var finalColor: Int
        // create a random object
        val random = Random()
        var index : Int
        val width= (displayMetrics.density*density*dotScreenRatio).toInt()
        val pixels = IntArray(width * width)

        // iteration through pixels
        for (y in 0 until width) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                if(x * x - x * width + y * y - y*width < - width*width/4) {
                    if(random.nextFloat()> circleColorDensity) finalColor =  Color.TRANSPARENT
                    else {
                        hsv= floatArrayOf(color - colorWiggleRoom/2 + colorWiggleRoom*random.nextFloat(), 1f - satWiggleRoom*random.nextFloat(), random.nextFloat())
                        finalColor =  Color.HSVToColor(hsv)
                    }
                } else {
                    finalColor = Color.TRANSPARENT
                }
                pixels[index] = finalColor
            }
        }
        // output bitmap
        val bmOut = Bitmap.createBitmap(width, width, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, width)
        return bmOut
    }


}