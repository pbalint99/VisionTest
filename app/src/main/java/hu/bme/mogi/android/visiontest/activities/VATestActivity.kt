package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_vatest.*
import kotlin.math.roundToInt


class VATestActivity  : AppCompatActivity() {
    private var level: Int = 0
    private var width: Int = 0
    private var dpWidth: Float = 0f
    private var distance: Float = 0f
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
    private var direction: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vatest)

        val sharedPref = getSharedPreferences("sp", Context.MODE_PRIVATE) ?: return
        width = sharedPref.getInt("width", 80)
        dpWidth = sharedPref.getFloat("dpWidth", 400f)
        distance = sharedPref.getFloat("distance", 6f)

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

    private fun changeImage() = if(level>sixMSizes.size-1) finish()
    else {
        //Size
        val mmSize = sixMSizes[level] * distance / 6
        val pixelSize = (dpWidth * mmSize / width)
        ivSnellen.requestLayout()
        ivSnellen.layoutParams.width = dpToPx(pixelSize, applicationContext)

        //Direction
        direction=(0..3).random()
        when (direction) {
            0 -> ivSnellen.rotation=270f //Up
            1 -> ivSnellen.rotation=0f //Right
            2 -> ivSnellen.rotation=90f //Down
            else -> ivSnellen.rotation=180f //Left
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).roundToInt()
    }

    private fun guess(dir: Int) {
        if(dir==direction) Toast.makeText(applicationContext,"Correct",Toast.LENGTH_SHORT).show()
        else Toast.makeText(applicationContext,"Wrong",Toast.LENGTH_SHORT).show()
        level++
        changeImage()
    }

}