package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.util.DisplayMetrics
import java.util.*

//TODO: do this on a side thread?
//Use other createbitmap method, which takes a color array
object Noise {
    const val colorWiggleRoom = 100f
    const val satWiggleRoom = 0.5f
    var circleColorDensity = 0.3f
    var circleSat = 0.3f
    val radii = IntArray(101)
    val xCoords = IntArray(101)
    val yCoords = IntArray(101)
    val opacity = FloatArray(101)
    var dotWidth = 100
    var circles2 = 0

    fun applyNoise(source: Bitmap, displayMetrics: DisplayMetrics, densityMultiplier: Int): Bitmap {
        var hsv: FloatArray
        val random = Random()
        var index = 0
        val aspectRatio = displayMetrics.widthPixels.toFloat()/displayMetrics.heightPixels
        val height = (displayMetrics.density*densityMultiplier).toInt()
        val width = (displayMetrics.density*densityMultiplier*aspectRatio).toInt()
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                hsv= floatArrayOf(57f,
                    0f, random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
                index++
            }
        }

        return Bitmap.createBitmap(pixels, width, height, source.config)
    }

    fun applyDecoys(source: Bitmap, displayMetrics: DisplayMetrics, density: Int, dotScreenRatio: Float): Bitmap {
        //Van ami cellaszámban van és van ami pixelben...
        var hsv : FloatArray
        val random = Random()
        var index = 0
        val aspectRatio = displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels
        val height = (displayMetrics.density * density).toInt()
        val width = (displayMetrics.density * density * aspectRatio).toInt()
        val pixels = IntArray(width * height) { Color.TRANSPARENT }
        val decoyXCoords = IntArray(12)
        val decoyYCoords = IntArray(12)
        var circleIndex = 0
        val horWiggleRoom = (0.1f * width).toInt()
        dotWidth = (width*dotScreenRatio).toInt()
        val vertWiggleRoom = (0.1f * height).toInt()

        for (i in decoyXCoords.indices) {
            decoyXCoords[i] = (dotWidth / 2..width - dotWidth / 2).random()
            decoyYCoords[i] = (dotWidth / 2..height - dotWidth / 2).random()
        }
        //Up
        decoyXCoords[0] = width / 2 - horWiggleRoom / 2 + random.nextInt(horWiggleRoom)
        decoyYCoords[0] = 0 + random.nextInt(vertWiggleRoom)
        //Right
        decoyXCoords[1] = width - dotWidth - random.nextInt(horWiggleRoom)
        decoyYCoords[1] = height / 2 - vertWiggleRoom / 2 + random.nextInt(vertWiggleRoom)
        //Down
        decoyXCoords[2] = width / 2 - horWiggleRoom / 2 + random.nextInt(horWiggleRoom)
        decoyYCoords[2] = height - dotWidth - random.nextInt(vertWiggleRoom)
        //Left
        decoyXCoords[3] = 0 + random.nextInt(horWiggleRoom)
        decoyYCoords[3] = height / 2 - vertWiggleRoom / 2 + random.nextInt(vertWiggleRoom)

        for (i in decoyXCoords.indices) {
            for (j in 0..(4..8).random()) {
                radii[circleIndex] = (dotWidth / 10..dotWidth / 5).random()
                xCoords[circleIndex] =
                    (decoyXCoords[i] + radii[circleIndex]..decoyXCoords[i] + dotWidth - radii[circleIndex]).random()
                yCoords[circleIndex] =
                    (decoyYCoords[i] + radii[circleIndex]..decoyYCoords[i] + dotWidth - radii[circleIndex]).random()
                opacity[circleIndex] = (random.nextFloat() * 0.2f + 0.6f) * 100
                circleIndex++
            }
        }
        //TODO:Delete
        decoyXCoords[4] = 0
        decoyYCoords[4] = 0
        opacity[4] = 100f
        //TODO: optimise this
        for (y in 0 until height) {
            for (x in 0 until width) {
                //Select between 2-4 points
                for(i in radii.indices) {
                    if(x>xCoords[i]-radii[i] && x<xCoords[i]+radii[i]
                        && y>yCoords[i]-radii[i] && y<yCoords[i]+radii[i] && random.nextFloat()< circleColorDensity) {
                        hsv = floatArrayOf(57f, 0f, random.nextFloat()*opacity[i]/100)
                        pixels[index] = Color.HSVToColor(hsv)
                    }
                }
                for (i in radii.indices) {
                    if(x>xCoords[i]-radii[i] && x<xCoords[i]+radii[i]
                        && y>yCoords[i]-radii[i] && y<yCoords[i]+radii[i] && random.nextFloat()< circleColorDensity) {
                        hsv = floatArrayOf(57f, 0f, random.nextFloat()*opacity[i]/100)
                        pixels[index] = Color.HSVToColor(hsv)
                    }
                }
                    index++
                }
            }
        return Bitmap.createBitmap(pixels, width, height, source.config)
        }


        fun applyNoiseAmorphous(
            source: Bitmap,
            color: Float,
            displayMetrics: DisplayMetrics,
            density: Int,
            dotScreenRatio: Float
        ): Bitmap {
            var hsv: FloatArray
            val random = Random()
            var index = 0
            dotWidth = (displayMetrics.density * density * dotScreenRatio).toInt()
            val pixels = IntArray(dotWidth * dotWidth) { Color.TRANSPARENT }
            val circles = 0..(6..12).random()

            for (i in circles) {
                radii[i] = (dotWidth / 10..dotWidth / 5).random()
                xCoords[i] = (radii[i]..dotWidth - radii[i]).random()
                yCoords[i] = (radii[i]..dotWidth - radii[i]).random()
            }

            for (y in 0 until dotWidth) {
                for (x in 0 until dotWidth) {
                    for (i in circles) {
                        if (x > xCoords[i] - radii[i] && x < xCoords[i] + radii[i]
                            && y > yCoords[i] - radii[i] && y < yCoords[i] + radii[i] && random.nextFloat() < circleColorDensity
                        ) {
                            hsv = floatArrayOf(color, circleSat, random.nextFloat())
                            pixels[index] = Color.HSVToColor(hsv)
                        }
                    }
                    index++
                }
            }
            val bitmap = Bitmap.createBitmap(pixels, dotWidth, dotWidth, source.config)
            //File.saveImage(bitmap)
            return Bitmap.createBitmap(pixels, dotWidth, dotWidth, source.config)
        }

//        fun applyNoiseAmorphousDemo(
//            source: Bitmap,
//            color: Float,
//            displayMetrics: DisplayMetrics,
//            density: Int,
//            dotScreenRatio: Float = 0.2f
//        ): Bitmap {
//            var hsv: FloatArray
//            val random = Random()
//            var index = 0
//            dotWidth = (displayMetrics.density * density * dotScreenRatio).toInt()
//            val pixels = IntArray(dotWidth * dotWidth) { Color.TRANSPARENT }
//            if (circles2 != 0) {
//                circleColorDensity = 1f
//                circleSat = 0.12f
//            }
//            if (circles2 == 0) {
//                circles2 = (6..12).random()
//                for (i in 0..circles2) {
//                    radii[i] = (dotWidth / 10..dotWidth / 5).random()
//                    xCoords[i] = (radii[i]..dotWidth - radii[i]).random()
//                    yCoords[i] = (radii[i]..dotWidth - radii[i]).random()
//                }
//            }
//
//
//            // iteration through
//            // - colorWiggleRoom/2 + colorWiggleRoom*random.nextFloat()
//            for (y in 0 until dotWidth) {
//                for (x in 0 until dotWidth) {
//                    //Select between 2-4 points
//                    for (i in 0..circles2) {
//                        if (x > xCoords[i] - radii[i] && x < xCoords[i] + radii[i]
//                            && y > yCoords[i] - radii[i] && y < yCoords[i] + radii[i] && random.nextFloat() < circleColorDensity
//                        ) {
//                            hsv = floatArrayOf(color, circleSat, random.nextFloat())
//                            pixels[index] = Color.HSVToColor(hsv)
//                        }
//                    }
//                    index++
//                }
//            }
//            val bitmap = Bitmap.createBitmap(pixels, dotWidth, dotWidth, source.config)
//            File.saveImage(bitmap)
//            return Bitmap.createBitmap(pixels, dotWidth, dotWidth, source.config)
//        }

//    fun applyNoiseColor(source: Bitmap, color: Float = -1f, displayMetrics: DisplayMetrics, density: Int): Bitmap {
//        var hsv: FloatArray
//        //if color is not set, set saturation to 0
//        val sat = if(color == -1f) 0f
//        else 1f
//        val random = Random()
//        var index = 0
//        val aspectRatio = displayMetrics.widthPixels.toFloat()/displayMetrics.heightPixels
//        val width = (displayMetrics.density*density*aspectRatio).toInt()
//        val height = (displayMetrics.density*density).toInt()
//        val pixels = IntArray(width * height)
//
//        for (y in 0 until height) {
//            for (x in 0 until width) {
//                hsv= floatArrayOf(color - colorWiggleRoom / 2 + colorWiggleRoom*random.nextFloat(),
//                    sat - satWiggleRoom*random.nextFloat(), random.nextFloat())
//                pixels[index] = Color.HSVToColor(hsv)
//                index++
//            }
//        }
//        return Bitmap.createBitmap(pixels, width, height, source.config)
//    }

        //dp, density could be stored in a local var
//    fun applyNoiseCircle(source: Bitmap, color: Float, displayMetrics: DisplayMetrics, density: Int, dotScreenRatio: Float = 0.2f): Bitmap {
//        var hsv: FloatArray
//        var finalColor: Int
//        // create a random object
//        val random = Random()
//        var index = 0
//        val width= (displayMetrics.density*density*dotScreenRatio).toInt()
//        val pixels = IntArray(width * width)
//
//        // iteration through pixels
//        for (y in 0 until width) {
//            for (x in 0 until width) {
//                // get current index in 2D-matrix
//                if(x * x - x * width + y * y - y*width < - width*width/4) {
//                    if(random.nextFloat()> circleColorDensity) finalColor =  Color.TRANSPARENT
//                    else {
//                        hsv= floatArrayOf(color - colorWiggleRoom/2 + colorWiggleRoom*random.nextFloat(), circleSat, random.nextFloat())
//                        finalColor =  Color.HSVToColor(hsv)
//                    }
//                } else {
//                    finalColor = Color.TRANSPARENT
//                }
//                pixels[index] = finalColor
//                index++
//            }
//        }
//        // output bitmap
////        val bmOut = Bitmap.createBitmap(width, width, source.config)
////        bmOut.setPixels(pixels, 0, width, 0, 0, width, width)
//        return Bitmap.createBitmap(pixels, width, width, source.config)
//    }

}