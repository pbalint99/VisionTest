package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.widget.FrameLayout
import android.widget.Toast
import java.util.*
import kotlin.math.abs
import kotlin.math.pow


object Noise {
    fun applyNoise(source: Bitmap, width: Int, height: Int, color: Float = 0f): Bitmap {
        // get source image size
        val pixels = IntArray(width * height)
        var hsv: FloatArray = floatArrayOf(0f, 0f, 0f)
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
                hsv= floatArrayOf(color, 1f, random.nextFloat())
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
        var hsv: FloatArray = floatArrayOf(0f, 0f, 0f)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, width)
        // create a random object
        val random = Random()
        var index = 0
        val innerD = 0.8*width //innerDiameter
        // iteration through pixels
        for (y in 0 until width) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                //TODO: optimise this
                if(x * x - x*width + y * y - y*width < -width*width/4){
                    if(random.nextInt()%2==0) hsv= floatArrayOf(bgColor, 1f, random.nextFloat())
                    else hsv= floatArrayOf(color, 1f, random.nextFloat())
                }
                else hsv= floatArrayOf(bgColor, 1f, random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
            }
        }
        // output bitmap
        val bmOut = Bitmap.createBitmap(width,width, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, width)
        return bmOut
    }


}