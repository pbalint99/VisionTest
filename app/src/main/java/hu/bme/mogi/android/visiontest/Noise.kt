package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import java.util.*
import kotlin.math.abs
import kotlin.math.pow


object Noise {
    const val colorWiggleRoom = 70f
    const val satWiggleRoom = 0.5f
    const val circleColorDensity = 0.4f
    fun applyNoise(source: Bitmap, width: Int, height: Int, color: Float = -1f): Bitmap {
        // get source image size
        val pixels = IntArray(width * height)
        var hsv: FloatArray = floatArrayOf(0f, 0f, 0f)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)
        //if color is net set, set saturation to 0
        val sat = if(color == -1f) 0f
        else 1f
        // create a random object
        val random = Random()
        var index = 0
        // iteration through pixels
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                hsv= floatArrayOf(color-colorWiggleRoom/2+colorWiggleRoom*random.nextFloat(), sat - satWiggleRoom*random.nextFloat(), random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
            }
        }
        // output bitmap
        val bmOut = Bitmap.createBitmap(width, height, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    fun applyNoiseCircle(source: Bitmap, width: Int, color: Float, bgColor: Float = 0f): Bitmap {
        // get source image size
        val pixels = IntArray(width * width)
        var hsv: FloatArray
        var finalColor: Int
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, width)
        // create a random object
        val random = Random()
        var index = 0
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