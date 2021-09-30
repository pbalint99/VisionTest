package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_vatest.*
import kotlin.math.roundToInt


class VATestActivity  : AppCompatActivity() {
    private var level: Int = 0
    private var width: Int = 0
    private var dpWidth: Float = 0f
    private var distance: Float = 6f
    //Amerikai szabvány alapján:
    private val sixMSizes: FloatArray = floatArrayOf(
        87.266f,
        52.360f,
        34.907f,
        26.180f,
        17.453f,
        13.090f,
        8.727f,
        7.272f,
        5.818f
    )
    private val distances: IntArray = intArrayOf(
        60,
        36,
        24,
        18,
        12,
        9,
        6,
        5,
        4
    )

    private var direction: Int = 0
    val displayMetrics = DisplayMetrics()

    //private var results: BooleanArray = booleanArrayOf(false)
    private var lastGood: Int = distances.size-1
    private var mistakeMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vatest)

        val sharedPref = getSharedPreferences("sp", Context.MODE_PRIVATE) ?: return
        distance = sharedPref.getFloat("distance", 6f)

        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        changeImage()

        //Button Listeners:
        upBtn.setOnClickListener{
            guess(0)
        }
        rightBtn.setOnClickListener{
            guess(1)
        }
        downBtn.setOnClickListener{
            guess(2)
        }
        leftBtn.setOnClickListener{
            guess(3)
        }

    }

    private fun changeImage() = if(level>sixMSizes.size-2) {
        Toast.makeText(applicationContext,"Your result: 6/"+distances[lastGood].toString(),Toast.LENGTH_LONG).show()
        finish()
    }
    else {
        //Size
        val mmSize = sixMSizes[level] * distance / 6
        ivSnellen.requestLayout()
        ivSnellen.layoutParams.width = ViewMover.mmToPixels(mmSize, displayMetrics)
        //TODO: Nem sin, tg!!!

        //Direction
        direction=(0..3).random()
        when (direction) {
            0 -> ivSnellen.rotation=270f //Up
            1 -> ivSnellen.rotation=0f //Right
            2 -> ivSnellen.rotation=90f //Down
            else -> ivSnellen.rotation=180f //Left
        }
    }

    private fun guess(dir: Int) {
        if(dir!=direction && !mistakeMade) {
            lastGood = if(level>0) level - 1
            else 0
            mistakeMade=true
        }
        level++
        changeImage()
    }

}