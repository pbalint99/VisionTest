package hu.bme.mogi.android.visiontest.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_color_calibration.*

class ColorCalibrationActivity : AppCompatActivity() {
    var color = Color.RED
    var hsv = FloatArray(3)
    enum class Type {
        HSV, RGB
    }
    var type: Type = Type.HSV

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_calibration)

        et1.setText("0")
        et2.setText("1")
        et3.setText("1")

        et1.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et1.text.toString() != "") {
                    if (type == Type.HSV) {
                        if (et1.text.toString().toFloat() > 360) et1.setText("360")
                        changeColor(type)
                    } else {
                        if (et1.text.toString().toFloat() > 255) et1.setText("255")
                        changeColor(type)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        et2.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et2.text.toString() != "") {
                    if (type == Type.HSV) {
                        if (et2.text.toString().toFloat() > 1) et2.setText("1")
                        changeColor(type)
                    } else {
                        if (et2.text.toString().toFloat() > 255) et2.setText("255")
                        changeColor(type)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        et3.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et3.text.toString() != "") {
                    if (type == Type.HSV) {
                        if (et3.text.toString().toFloat() > 1) et3.setText("1")
                        changeColor(type)
                    } else {
                        if (et3.text.toString().toFloat() > 255) et3.setText("255")
                        changeColor(type)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }


    fun changeColor(type: Type) {
        if(et1.text.toString() != "" && et2.text.toString() != "" && et3.text.toString() != "") {
            if(type == Type.HSV) {
                hsv = floatArrayOf(et1.text.toString().toFloat(),
                    et2.text.toString().toFloat(),et3.text.toString().toFloat())
                color = Color.HSVToColor(hsv)
            } else {
                color = Color.rgb(et1.text.toString().toFloat().toInt(),
                    et2.text.toString().toFloat().toInt(), et3.text.toString().toFloat().toInt())
            }
            colorView.setColorFilter(color)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.calibration_menu, menu)
        menu?.getItem(0)?.subMenu?.getItem(0)?.title = "Switch RGB/HSV"
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.color_calibration -> {
                if(type == Type.HSV) {
                    type = Type.RGB
                    textView1.text = getString(R.string.red)
                    textView2.text = getString(R.string.green)
                    textView3.text = getString(R.string.blue)
                    et1.setText("255")
                    et2.setText("255")
                    et3.setText("255")
                } else {
                    type = Type.HSV
                    textView1.text = getString(R.string.hue_0_360)
                    textView2.text = getString(R.string.saturation_0_1)
                    textView3.text = getString(R.string.value_0_1)
                    et1.setText("0")
                    et2.setText("1")
                    et3.setText("1")
                }
                changeColor(type)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}