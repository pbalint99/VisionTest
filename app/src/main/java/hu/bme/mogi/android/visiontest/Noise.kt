package hu.bme.mogi.android.visiontest

import android.graphics.*
import java.util.*

//TODO: do this on a side thread?
//Use other createbitmap method, which takes a color array
object Noise {
    const val colorWiggleRoom = 70f
    const val satWiggleRoom = 0.5f
    const val circleColorDensity = 0.2f

    fun applyNoise(source: Bitmap, width: Int, height: Int, color: Float = -1f): Bitmap {
        val pixels = IntArray(width * height)
        var hsv: FloatArray
        //if color is net set, set saturation to 0
        val sat = if(color == -1f) 0f
        else 1f
        val random = Random()
        var index = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                hsv= floatArrayOf(color-colorWiggleRoom/2+colorWiggleRoom*random.nextFloat(), sat - satWiggleRoom*random.nextFloat(), random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
                index++
            }
        }
        return Bitmap.createBitmap(pixels, width, height, source.config)
    }

    fun applyNoiseCircle(source: Bitmap, width: Int, color: Float): Bitmap {
        // get source image size
        val pixels = IntArray(width * width)
        var hsv: FloatArray
        var finalColor: Int
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, width)
        // create a random object
        val random = Random()
        var index : Int
        // iteration through pixels
        for (y in 0 until width) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                //TODO: optimise this
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
        val bmOut = Bitmap.createBitmap(width,width, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, width)
        return bmOut
    }


}