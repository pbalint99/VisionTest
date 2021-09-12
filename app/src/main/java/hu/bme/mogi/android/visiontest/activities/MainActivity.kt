package hu.bme.mogi.android.visiontest.activities

import android.content.ClipData.newUri
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        try {
            if (checkSystemWritePermission()) {
                setBrightness(255)
            } else {
                Toast.makeText(applicationContext, "Please allow modifying system settings.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.i("settings", e.toString())
            Toast.makeText(applicationContext, "Please allow modifying system settings.", Toast.LENGTH_SHORT).show()
        }

    }

    fun setBrightness(brightness: Int) {

        //constrain the value of brightness
        var brightness = brightness
        if (brightness < 0) brightness = 0 else if (brightness > 255) brightness = 255
        val cResolver = this.applicationContext.contentResolver
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
    }

    private fun checkSystemWritePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(applicationContext)) return true else openAndroidPermissionsMenu()
        }
        return false
    }

    private fun openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + applicationContext.getPackageName())
            applicationContext.startActivity(intent)
        }
    }


}
