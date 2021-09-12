package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.widget.Toast
import java.util.*
import kotlin.math.abs


object Noise {
    fun applyNoise(source: Bitmap, width: Int, height: Int): Bitmap {
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
                hsv= floatArrayOf(0f, 0f, random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
            }
        }
        // output bitmap
        val bmOut = Bitmap.createBitmap(width, height, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    fun applyNoiseCircle(source: Bitmap, width: Int): Bitmap {
        // get source image size
        val pixels = IntArray(width * width)
        var hsv: FloatArray = floatArrayOf(0f, 0f, 0f)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, width)
        // create a random object
        val random = Random()
        var index = 0
        val center = width/2
        // iteration through pixels
        for (y in 0 until width) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                val sat = (0.8f-abs(x-center).toFloat()/width-abs(y-center).toFloat()/width)
                hsv= floatArrayOf(178f, sat, random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
            }
        }
        // output bitmap
        val bmOut = Bitmap.createBitmap(width,width, source.config)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, width)
        return bmOut
    }


}