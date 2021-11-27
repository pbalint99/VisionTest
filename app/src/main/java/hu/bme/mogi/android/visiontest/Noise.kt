package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.util.DisplayMetrics
import java.util.*

//TODO: do this on a side thread?
//Use other createbitmap method, which takes a color array
object Noise {
    //const val colorWiggleRoom = 100f
    //const val satWiggleRoom = 0.5f
    private const val circleColorDensity = 0.3f
    private const val circleSat = 0.3f
    private val radii = IntArray(101)
    private val xCoords = IntArray(101)
    private val yCoords = IntArray(101)
    private val opacity = FloatArray(101)
    private var widthCells = 100
    private const val densityMultiplier = 50
    private val random = Random()
    private lateinit var hsv: FloatArray
    var aspectRatio: Float = 0.2f
    var heightCells = 100
    var dotWidthCells = 100

    fun applyNoise(): Bitmap {
        var index = 0
        val pixels = IntArray(widthCells * heightCells) //also the number of cells

        for (y in 0 until heightCells) {
            for (x in 0 until widthCells) {
                hsv= floatArrayOf(57f,
                    0f, random.nextFloat())
                pixels[index] = Color.HSVToColor(hsv)
                index++
            }
        }

        return Bitmap.createBitmap(pixels, widthCells, heightCells, Bitmap.Config.RGB_565)
    }


    fun applyNoiseAmorphous(
        color: Float,
        dotScreenRatio: Float
    ): Bitmap {
        var index = 0
        val dotWidthCells = (widthCells*dotScreenRatio).toInt()
        val pixels = IntArray(dotWidthCells * dotWidthCells) { Color.TRANSPARENT } //Also cells
        val circles = 0..(6..12).random()

        for (i in circles) {
            radii[i] = (dotWidthCells / 10..dotWidthCells / 5).random()
            xCoords[i] = (radii[i]..(dotWidthCells - radii[i])).random()
            yCoords[i] = (radii[i]..(dotWidthCells - radii[i])).random()
        }

        for (y in 0 until dotWidthCells) {
            for (x in 0 until dotWidthCells) {
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
        //val bitmap = Bitmap.createBitmap(pixels, dotWidth, dotWidth, source.config)
        //File.saveImage(bitmap)
        return Bitmap.createBitmap(pixels, dotWidthCells, dotWidthCells, Bitmap.Config.ARGB_8888)
    }


    fun applyDecoys(dotScreenRatio: Float): Bitmap {
        var index = 0
        val pixels = IntArray(widthCells * heightCells) { Color.TRANSPARENT }
        val decoyXCoords = IntArray(12)
        val decoyYCoords = IntArray(12)
        var circleIndex = 0
        val dotWidthCells = (widthCells*dotScreenRatio).toInt()
        val horWiggleRoom = (0.1f * widthCells).toInt()
        val vertWiggleRoom = (0.1f * heightCells).toInt()

        for (i in decoyXCoords.indices) {
            decoyXCoords[i] = (dotWidthCells / 2..widthCells - dotWidthCells / 2).random()
            decoyYCoords[i] = (dotWidthCells / 2..heightCells - dotWidthCells / 2).random()
        }
        //Up
        decoyXCoords[0] = widthCells / 2 - horWiggleRoom / 2 + random.nextInt(horWiggleRoom)
        decoyYCoords[0] = 0 + random.nextInt(vertWiggleRoom)
        //Right
        decoyXCoords[1] = widthCells - dotWidthCells - random.nextInt(horWiggleRoom)
        decoyYCoords[1] = heightCells / 2 - vertWiggleRoom / 2 + random.nextInt(vertWiggleRoom)
        //Down
        decoyXCoords[2] = widthCells / 2 - horWiggleRoom / 2 + random.nextInt(horWiggleRoom)
        decoyYCoords[2] = heightCells - dotWidthCells - random.nextInt(vertWiggleRoom)
        //Left
        decoyXCoords[3] = 0 + random.nextInt(horWiggleRoom)
        decoyYCoords[3] = heightCells / 2 - vertWiggleRoom / 2 + random.nextInt(vertWiggleRoom)

        for (i in decoyXCoords.indices) {
            for (j in 0..(4..8).random()) {
                radii[circleIndex] = (dotWidthCells / 10..dotWidthCells / 5).random()
                xCoords[circleIndex] =
                    (decoyXCoords[i] + radii[circleIndex]..decoyXCoords[i] + dotWidthCells - radii[circleIndex]).random()
                yCoords[circleIndex] =
                    (decoyYCoords[i] + radii[circleIndex]..decoyYCoords[i] + dotWidthCells - radii[circleIndex]).random()
                opacity[circleIndex] = (random.nextFloat() * 0.2f + 0.6f) * 100
                circleIndex++
            }
        }
        //TODO:Delete
        decoyXCoords[4] = 0
        decoyYCoords[4] = 0
        opacity[4] = 100f
        //TODO: optimise this
        for (y in 0 until heightCells) {
            for (x in 0 until widthCells) {
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
        return Bitmap.createBitmap(pixels, widthCells, heightCells, Bitmap.Config.ARGB_8888)
    }

    fun setup(displayMetrics: DisplayMetrics) {
        aspectRatio = displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels
        heightCells = (displayMetrics.density*densityMultiplier/aspectRatio).toInt()
        widthCells = (displayMetrics.density*densityMultiplier).toInt()
    }

}