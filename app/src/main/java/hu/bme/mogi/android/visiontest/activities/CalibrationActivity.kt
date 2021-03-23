package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_calibration.*


class CalibrationActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)

        doneBtn.setOnClickListener{
            val displayMetrics: DisplayMetrics = applicationContext.resources.displayMetrics
            val dpWidth: Float = displayMetrics.widthPixels / displayMetrics.density

            val sharedPref = applicationContext.getSharedPreferences(
                "sp",
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
                putInt("width", etWidth.text.toString().toInt())
                putFloat("dpWidth",dpWidth)
                putFloat("distance",etDistance.text.toString().toFloat())
                apply()
            }

            Toast.makeText(this@CalibrationActivity, "All set.", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }
}