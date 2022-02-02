package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_parameters.*
import kotlin.math.floor

class ParametersActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)

        sharedPref = applicationContext.getSharedPreferences(
            "sp",
            Context.MODE_PRIVATE
        )
        minimumAcuityET.setText(sharedPref.getInt("minimumAcuity",12).toString())
        contrastNumberOfTrialsET.setText(sharedPref.getInt("contrastTrials",6).toString())
        minFreqET.setText(sharedPref.getFloat("minFreq",1.5f).toString())
        maxFreqET.setText(sharedPref.getFloat("maxFreq",4f).toString())
        minContrastET.setText(sharedPref.getFloat("minContrast",4f).toString())
        maxContrastET.setText(sharedPref.getFloat("maxContrast",10f).toString())
        vaCheckbox.isChecked = sharedPref.getBoolean("useVA",true)
        trialsPerColorET.setText(sharedPref.getInt("colorTrials",2).toString())
        fillET.setText(sharedPref.getFloat("fillPercent",10f).toString())
        saturationET.setText(sharedPref.getFloat("saturation",10f).toString())
    }

    override fun onPause() {
        super.onPause()
        with(sharedPref.edit()) {
            putInt("minimumAcuity", floor(minimumAcuityET.text.toString().toFloat()).toInt())
            putBoolean("useVA", vaCheckbox.isChecked)
            putInt("contrastTrials",floor(contrastNumberOfTrialsET.text.toString().toFloat()).toInt())
            putFloat("minFreq",minFreqET.text.toString().toFloat())
            putFloat("maxFreq",maxFreqET.text.toString().toFloat())
            putFloat("minContrast",minContrastET.text.toString().toFloat())
            putFloat("maxContrast",maxContrastET.text.toString().toFloat())
            putInt("colorTrials",floor(trialsPerColorET.text.toString().toFloat()).toInt())
            putFloat("fillPercent",fillET.text.toString().toFloat())
            putFloat("saturation",saturationET.text.toString().toFloat())
            apply()
        }
    }
}