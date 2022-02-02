package hu.bme.mogi.android.visiontest

import android.graphics.*
import android.util.DisplayMetrics
import java.util.*
import kotlin.math.max

//TODO: do this on a side thread?
//Use other createbitmap method, which takes a color array
object Noise {
    //const val colorWiggleRoom = 100f
    //const val satWiggleRoom = 0.5f
    var circleColorDensity = 0.5f
    var circleSat = 0.2f
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
    val blueCorrectionFactor = 1.2f

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

    fun setup(displayMetrics: DisplayMetrics) {
        aspectRatio = displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels
        heightCells = (displayMetrics.density*densityMultiplier/aspectRatio).toInt()
        widthCells = (displayMetrics.density*densityMultiplier).toInt()
    }

    fun applyNoiseAmorphous(
        color: Float, //negative means opacity
        dotScreenRatio: Float
    ): Bitmap {
        var index = 0
        val dotWidthCells = (widthCells*dotScreenRatio).toInt()
        val pixels = IntArray(dotWidthCells * dotWidthCells) { Color.TRANSPARENT } //Also cells
        val circles = 0..(6..12).random()

        if(color==240f) circleSat *= blueCorrectionFactor
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
                        hsv = if(color>=0) floatArrayOf(color, circleSat, random.nextFloat())
                        else floatArrayOf(color, 0f, -random.nextFloat()*color)
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
}