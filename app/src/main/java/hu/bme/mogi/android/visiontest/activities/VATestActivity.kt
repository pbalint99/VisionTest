package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_vatest.*
import kotlin.math.roundToInt

class VATestActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vatest)

        val sharedPref = getSharedPreferences("sp", Context.MODE_PRIVATE) ?: return
        val width = sharedPref.getInt("width", 80)
        val dpWidth = sharedPref.getFloat("dpWidth", 400f)

        val mmSize = 72.7
        val pixelSize : Float = (dpWidth*mmSize/width).toFloat()
        ivSnellen.layoutParams.width = dpToPx(pixelSize,applicationContext);

        //Toast.makeText(this,dpWidth.toString(),Toast.LENGTH_LONG).show()
    }

    fun dpToPx(dp: Float, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).roundToInt()
    }
}