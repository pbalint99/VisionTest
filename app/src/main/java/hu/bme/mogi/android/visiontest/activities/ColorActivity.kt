package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.Noise
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_color.*
import kotlinx.android.synthetic.main.activity_color.textView
import kotlinx.android.synthetic.main.activity_contrasttest.downBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.leftBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.noiseView
import kotlinx.android.synthetic.main.activity_contrasttest.startButton
import kotlinx.android.synthetic.main.activity_contrasttest.rightBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.upBtnC
import kotlinx.android.synthetic.main.activity_contrasttest_keyboard.*

class ColorActivity: AppCompatActivity() {
    var bgColor = floatArrayOf(57f,245f) //Ishihara: 57f
    var dotColor = floatArrayOf(31f,65f) //Ishihara:31f
    var prevDir = 5
    var level = 0
    var guesses = BooleanArray(5)
    //private var aspectRatio = 0f
    private var noiseDensity = 500
    private var index = 0
    //TODO: BUG
    var dotScreenRatio = 0.2f
    private lateinit var passButton : MenuItem
    private lateinit var menuText : MenuItem
    private lateinit var displayMetrics: DisplayMetrics
    var started = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(resources.configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            setContentView(R.layout.activity_color)
            startButton.setOnClickListener {
                start()
                upBtnC.isEnabled = true
                downBtnC.isEnabled = true
                leftBtnC.isEnabled = true
                rightBtnC.isEnabled = true
                passButton.isEnabled=true
                menuText.title = "Can't see:"
                startButton.isEnabled = false
                startButton.text = ""
                startButton.setBackgroundColor(Color.TRANSPARENT)
            }
            upBtnC.setOnClickListener{
                guess(0)
            }
            rightBtnC.setOnClickListener{
                guess(1)
            }
            downBtnC.setOnClickListener{
                guess(2)
            }
            leftBtnC.setOnClickListener{
                guess(3)
            }
            start()
        } else setContentView(R.layout.activity_color_keyboard)


    }

    private fun guess(dir: Int) {
        guesses[level] = dir == prevDir

        if(level == 4) {
            evaluate()
            return
        }
//        if(level == 4) {
//            index++
//        }

        prevDir = ViewMover.move(dotView, noiseView, false)

        applyNoises(index)

        level++
    }

    private fun evaluate() {
        var correct = 0
        for (element in guesses) {
            if (element) correct++
        }
        Toast.makeText(applicationContext,"Your result is: "+correct+"/"+guesses.size,Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun start()  {
        textView.text = ""

        prevDir = ViewMover.move(dotView, noiseView, false)

        menuText.isVisible=true
        displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dotParams = dotView.layoutParams
        val dotWidth = ViewMover.degreeToPixels(1.0,displayMetrics,getSharedPreferences("sp", Context.MODE_PRIVATE))
        dotParams.width = dotWidth
        dotParams.height = dotWidth
        dotScreenRatio = dotWidth.toFloat()/displayMetrics.widthPixels

        applyNoises(0)
    }

    private fun applyNoises(index: Int) {
        noiseView.setImageBitmap(
            Noise.applyNoise(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
                ),displayMetrics, 400
            )
        )
        dotView.setImageBitmap(
            Noise.applyNoiseAmorphous(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
                ),
                0f, displayMetrics,200, dotScreenRatio)
        )
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        if (menu != null) {
            passButton = menu.getItem(1)
            menuText = menu.getItem(0)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        guess(4)
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                guess(0)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                guess(1)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                guess(2)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                guess(3)
                true
            }
            KeyEvent.KEYCODE_ESCAPE -> {
                true
            }
            KeyEvent.KEYCODE_ENTER -> {
                true
            }
            KeyEvent.KEYCODE_SPACE -> {
                if (!started) {
                    start()
                    started = true
                    startTextView.setBackgroundColor(Color.TRANSPARENT)
                    startTextView.text = ""
                }
                true
            }
//            KeyEvent.KEYCODE_A -> {
//                Noise.circleSat += 0.05f
//                applyNoises(0)
//                true
//            }
//            KeyEvent.KEYCODE_S -> {
//                Noise.circleSat -= 0.05f
//                applyNoises(0)
//                true
//            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

}