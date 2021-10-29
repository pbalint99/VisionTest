package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.util.DisplayMetrics
import java.util.*

//TODO: do this on a side thread?
//Use other createbitmap method, which takes a color array
object Noise {
    const val colorWiggleRoom = 100f
    const val satWiggleRoom = 0.5f
    const val circleColorDensity = 0.2f
    var circleSat = 0.2f

    fun applyNoise(source: Bitmap, displayMetrics: DisplayMetrics, density: Int): Bitmap {
        var hsv: FloatArray
        val random = Random()
        var index = 0
        val aspectRatio = displayMetrics.widthPixels.toFloat()/displayMetrics.heightPixels
        val width = (displayMetrics.density*density*aspectRatio).toInt()
        val height = (displayMetrics.density*density).toInt()
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                hsv= floatArrayOf(0f,
                    0f, random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
                index++
            }
        }
        return Bitmap.createBitmap(pixels, width, height, source.config)
    }

    fun applyNoiseColor(source: Bitmap, color: Float = -1f, displayMetrics: DisplayMetrics, density: Int): Bitmap {
        var hsv: FloatArray
        //if color is not set, set saturation to 0
        val sat = if(color == -1f) 0f
        else 1f
        val random = Random()
        var index = 0
        val aspectRatio = displayMetrics.widthPixels.toFloat()/displayMetrics.heightPixels
        val width = (displayMetrics.density*density*aspectRatio).toInt()
        val height = (displayMetrics.density*density).toInt()
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                hsv= floatArrayOf(color - colorWiggleRoom / 2 + colorWiggleRoom*random.nextFloat(),
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
        var index = 0
        val width= (displayMetrics.density*density*dotScreenRatio).toInt()
        val pixels = IntArray(width * width)

        // iteration through pixels
        for (y in 0 until width) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                if(x * x - x * width + y * y - y*width < - width*width/4) {
                    if(random.nextFloat()> circleColorDensity) finalColor =  Color.TRANSPARENT
                    else {
                        hsv= floatArrayOf(color - colorWiggleRoom/2 + colorWiggleRoom*random.nextFloat(), circleSat, random.nextFloat())
                        finalColor =  Color.HSVToColor(hsv)
                    }
                } else {
                    finalColor = Color.TRANSPARENT
                }
                pixels[index] = finalColor
                index++
            }
        }
        // output bitmap
//        val bmOut = Bitmap.createBitmap(width, width, source.config)
//        bmOut.setPixels(pixels, 0, width, 0, 0, width, width)
        return Bitmap.createBitmap(pixels, width, width, source.config)
    }

    fun applyNoiseAmorphous(source: Bitmap, color: Float, displayMetrics: DisplayMetrics, density: Int, dotScreenRatio: Float = 0.2f, blackAndWhite: Boolean = false): Bitmap {
        var hsv: FloatArray
        var finalColor: Int
        // create a random object
        val random = Random()
        val radii = IntArray(9)
        val xCoords = IntArray(9)
        val yCoords = IntArray(9)
        var index = 0
        val width= (displayMetrics.density*density*dotScreenRatio).toInt()
        val pixels = IntArray(width * width) {Color.TRANSPARENT}
        val circles = 0..(4..8).random()

        for(i in circles) {
            radii[i] = (width/10..width/5).random()
            xCoords[i] = (radii[i]..width-radii[i]).random()
            yCoords[i] = (radii[i]..width-radii[i]).random()
        }
        // iteration through pixels
        for (y in 0 until width) {
            for (x in 0 until width) {
                //Select between 2-4 points
                for(i in circles) {
                    if(x>xCoords[i]-radii[i] && x<xCoords[i]+radii[i]
                        && y>yCoords[i]-radii[i] && y<yCoords[i]+radii[i] && random.nextFloat()< circleColorDensity) {
                            hsv = floatArrayOf(color - colorWiggleRoom/2 + colorWiggleRoom*random.nextFloat(), circleSat, random.nextFloat())
                            pixels[index] = Color.HSVToColor(hsv)
                    }
                }
                index++
            }
        }
        val bitmap = Bitmap.createBitmap(pixels, width, width, source.config)
        File.saveImage(bitmap)
        return Bitmap.createBitmap(pixels, width, width, source.config)
    }


}