package hu.bme.mogi.android.visiontest.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.File
import hu.bme.mogi.android.visiontest.R
import kotlinx.android.synthetic.main.activity_calibration.*
import kotlinx.android.synthetic.main.activity_contrasttest_keyboard.*
import java.text.SimpleDateFormat
import java.util.*


class CalibrationActivity: AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private var yearSet = 2000; private var monthSet = 6; private var daySet = 15
    private var distance = 4f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)

        lateinit var mDateSetListener: OnDateSetListener
        sharedPref = applicationContext.getSharedPreferences(
            "sp",
            Context.MODE_PRIVATE
        )

        doneBtn.setOnClickListener{
            start()
            val intent = Intent(this, VATestActivity::class.java)
            startActivity(intent)
        }

        chooseSpecificTv.setOnClickListener{
            File.onlyOneTest = true
            start()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //farBtn.setOnClickListener{
        //    distance = 4f
        //}

        //nearBtn.setOnClickListener{
        //    distance = 0.4f
        //}

        yearSet = sharedPref.getInt("year", 2000)
        monthSet = sharedPref.getInt("month", 6)
        daySet = sharedPref.getInt("day", 15)
        var date = "$yearSet/${monthSet+1}/$daySet"
        etName.setText(sharedPref.getString("name", ""))
        //etDistance.setText(sharedPref.getFloat("distance", 2f).toString())
        mDisplayDate.text = date
        if(sharedPref.getFloat("distance",4f)==4f) {
            farBtn.isChecked = true
            nearBtn.isChecked = false
        }
        else {
            farBtn.isChecked = false
            nearBtn.isChecked = true
        }

        mDisplayDate.setOnClickListener {
            val year = yearSet
            val month = monthSet
            val day = daySet
            val dialog = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        mDateSetListener = OnDateSetListener { _, year, month, day ->
            yearSet = year; monthSet = month; daySet = day
            date = "$yearSet/${monthSet+1}/$daySet"
            mDisplayDate.text = date
        }

        try {
            if (checkSystemWritePermission()) {
                setBrightness(255)
            }
        } catch (e: Exception) {
            Log.i("settings", e.toString())
        }
        val size = Point()
        display?.getRealSize(size)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun start() {
        var name = etName.text.toString()
        with(sharedPref.edit()) {
            putFloat("distance", distance)
            putString("name", name)
            putInt("year", yearSet)
            putInt("month", monthSet)
            putInt("day", daySet)
            apply()
        }
        var sdf = SimpleDateFormat("yyyy.MM.dd, hh:mm")
        var currentDate = sdf.format(Date())
        File.fileText+="PATIENT: $name\nDate: $currentDate\n\n"
        name = name.replace(" ", "").replace("ő", "o").replace("ó", "o").replace("ö", "o").replace(
            "ű",
            "u"
        ).replace("ü", "u").replace("ú", "u").replace("á", "a").replace("é", "e").replace(
            "í",
            "i"
        )
        sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm")
        currentDate = sdf.format(Date())
        File.fileName = name+"_$currentDate"
        File.fileText = ""
    }

    @Suppress("NAME_SHADOWING")
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
            intent.data = Uri.parse("package:" + applicationContext.packageName)
            applicationContext.startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.calibration_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.color_calibration -> {
                val intent = Intent(this, ParametersActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.hungarian -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                start()
                val intent = Intent(this, VATestActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}