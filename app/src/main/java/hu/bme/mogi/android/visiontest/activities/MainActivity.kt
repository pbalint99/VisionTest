package hu.bme.mogi.android.visiontest.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_main.*

//TODO: touch and keyboard should mutually work
//TODO: feedback when button pressed (so low VA can see something happened)
//Back button rewrite

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Gombok beállítása:
        va_start.setOnClickListener{
            val intent = Intent(this, VATestActivity::class.java)
            startActivity(intent)
        }

        contrast_start.setOnClickListener{
            val intent = Intent(this, ContrastTestActivity::class.java)
            startActivity(intent)
        }

        calibration_start.setOnClickListener{
            val intent = Intent(this, CalibrationActivity::class.java)
            startActivity(intent)
        }

        color_start.setOnClickListener{
            val intent = Intent(this, ColorActivity::class.java)
            startActivity(intent)
        }
    }

}
