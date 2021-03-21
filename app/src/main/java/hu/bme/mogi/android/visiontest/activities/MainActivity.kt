package hu.bme.mogi.android.visiontest.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Gombok beállítása:
        va_start.setOnClickListener{
            val intent = Intent(this, VATestActivity::class.java)
            startActivity(intent)
        }

        calibration_start.setOnClickListener{
            val intent = Intent(this, CalibrationActivity::class.java)
            startActivity(intent)
        }
    }


}
