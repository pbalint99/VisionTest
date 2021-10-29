package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
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
            val pWidth = displayMetrics.widthPixels

            val sharedPref = applicationContext.getSharedPreferences(
                "sp",
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
//                putInt("width", etWidth.text.toString().toInt())
//                putInt("pixelWidth",pWidth)
                putFloat("distance",etDistance.text.toString().toFloat())
                putString("name",etName.text.toString())
                apply()
            }

            Toast.makeText(this@CalibrationActivity, "All set.", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }
}