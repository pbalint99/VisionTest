package hu.bme.mogi.android.visiontest

import android.content.Context
import android.content.SharedPreferences
import android.util.DisplayMetrics
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_contrasttest.*
import java.util.*
import kotlin.math.sin
import kotlin.math.tan

object ViewMover {
    var density: Float = 0f
    var distance: Float = 0f
    var prevDir = intArrayOf(4,4)

    fun move(viewToMove: View, bigView: View, rotate: Boolean): Int {
        val params = viewToMove.layoutParams as ConstraintLayout.LayoutParams
        val random = Random()
        val loc =  IntArray(2)
        val widthMiddle = (bigView.width - viewToMove.width)/2
        val heightMiddle = (bigView.height - viewToMove.height)/2
        val width = bigView.width
        val height = bigView.height
        val horWiggleRoom = (0.1f*width).toInt()
        val vertWiggleRoom = (0.1f*height).toInt()

        var dir = (0..3).random()
        while(dir==prevDir[0] && dir==prevDir[1]) {
            dir = (0..3).random()
        }
        when(dir) {
            0 -> {
                loc[0] = widthMiddle - horWiggleRoom/2 + random.nextInt(horWiggleRoom)
                loc[1] = 0 + random.nextInt(vertWiggleRoom)
            } //Up
            1 -> {
                loc[0] = bigView.width - viewToMove.width - random.nextInt(horWiggleRoom)
                loc[1] = heightMiddle - vertWiggleRoom/2 + random.nextInt(vertWiggleRoom)
            } //Right
            2 -> {
                loc[0] = widthMiddle - horWiggleRoom/2 + random.nextInt(horWiggleRoom)
                loc[1] = bigView.height - viewToMove.height - random.nextInt(vertWiggleRoom)
            } //Down
            else -> {
                loc[0] = 0 + random.nextInt(horWiggleRoom)
                loc[1] = heightMiddle - vertWiggleRoom/2 + random.nextInt(vertWiggleRoom)
            } //Left
        }
        prevDir[1] = prevDir[0]; prevDir[0] = dir
        params.setMargins(
            loc[0],
            loc[1],
            0,
            0
        )
        viewToMove.layoutParams = params
        if(rotate) viewToMove.rotation=random.nextInt(360).toFloat()

        return dir
    }

    fun degreeToPixels(deg: Double = 2.0, dm: DisplayMetrics, sp: SharedPreferences) : Int {
        distance = sp.getFloat("distance", 1f)
        val mmSize = tan(Math.toRadians(deg)) * distance * 1000
        //val mmScreenWidth = 25.4f*1.05f*dm.widthPixels/dm.xdpi
        //return intArrayOf(((mmSize*dm.xdpi)/(25.4*1.05f)).toInt(),mmSize.toInt())
        return ((mmSize*dm.xdpi)/(25.4*1.05f)).toInt()
    }

    fun mmToPixels(mmSize: Float = 10f, dm: DisplayMetrics) : Int {
        return ((mmSize*dm.xdpi)/(25.4*1.05f)).toInt()
    }
}