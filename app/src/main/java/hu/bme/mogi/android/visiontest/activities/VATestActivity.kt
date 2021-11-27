package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.File
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_vatest.*
import kotlinx.android.synthetic.main.activity_vatest.downArrow
import kotlinx.android.synthetic.main.activity_vatest.ivSnellen
import kotlinx.android.synthetic.main.activity_vatest.leftArrow
import kotlinx.android.synthetic.main.activity_vatest.rightArrow
import kotlinx.android.synthetic.main.activity_vatest.upArrow
import kotlinx.android.synthetic.main.activity_vatest_keyboard.*
import kotlin.math.atan


class VATestActivity  : AppCompatActivity() {
    private var level: Int = 0
    private var doneOnce = 0
    private var firstRoundGuesses = 9
    private var allGuesses = 0
    private var distance: Float = 6f
    private var prevDir = IntArray(2)
    private var firstGuess = true
    private var keyboardConnected = true
    private lateinit var passButton: MenuItem
    private lateinit var menuText: MenuItem

    //Amerikai szabvány alapján:
    private val sixMSizes: FloatArray = floatArrayOf(
        87.266f,
        52.360f,
        34.907f,
        26.180f,
        17.453f,
        13.090f,
        8.727f,
        7.272f,
        5.818f
    )
    private val distances: IntArray = intArrayOf(
        60,
        36,
        24,
        18,
        12,
        9,
        6,
        5,
        4
    )
    var guesses = BooleanArray(distances.size * 2)
    enum class Evaluation {GOOD, QUESTIONABLE, BAD}

    private var direction: Int = 0
    private val displayMetrics = DisplayMetrics()
    var results = IntArray(2)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_vatest)

        val sharedPref = getSharedPreferences("sp", Context.MODE_PRIVATE) ?: return
        distance = sharedPref.getFloat("distance", 6f)

        val gameControllers = File.getGameControllerIds()

        if(resources.configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES &&
                gameControllers.isEmpty()) {
            setContentView(R.layout.activity_vatest)

            upBtn.setOnClickListener{
                guess(0)
            }
            rightBtn.setOnClickListener{
                guess(1)
            }
            downBtn.setOnClickListener{
                guess(2)
            }
            leftBtn.setOnClickListener{
                guess(3)
            }

            keyboardConnected = false
        }
        else setContentView(R.layout.activity_vatest_keyboard)

        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        changeImage()

        results = intArrayOf(4,4)
        prevDir = intArrayOf(4,4)

    }

    private fun changeImage() {
        //Size
        val mmSize = sixMSizes[level] * distance / 6
        ivSnellen.requestLayout()
        ivSnellen.layoutParams.width = ViewMover.mmToPixels(mmSize, displayMetrics)

        //Direction
        direction = (0..3).random()
        while(direction==prevDir[0] && direction==prevDir[1]) {
            direction = (0..3).random()
        }
        when (direction) {
            0 -> ivSnellen.rotation = 270f //Up
            1 -> ivSnellen.rotation = 0f //Right
            2 -> ivSnellen.rotation = 90f //Down
            else -> ivSnellen.rotation=180f //Left
        }
        prevDir[1] = prevDir[0]; prevDir[0] = direction
    }

    //TODO: bug if pressing wrong 4 times
    private fun guess(dir: Int) {
        if(firstGuess) {
            if(!keyboardConnected){
                upArrow.visibility = View.GONE
                rightArrow.visibility = View.GONE
                downArrow.visibility = View.GONE
                leftArrow.visibility = View.GONE
                upBtn.setBackgroundColor(Color.TRANSPARENT)
                rightBtn.setBackgroundColor(Color.TRANSPARENT)
                downBtn.setBackgroundColor(Color.TRANSPARENT)
                leftBtn.setBackgroundColor(Color.TRANSPARENT)
            } else {
                upArrow.visibility = View.GONE
                rightArrow.visibility = View.GONE
                downArrow.visibility = View.GONE
                leftArrow.visibility = View.GONE
                upArrowBox.visibility = View.GONE
                rightArrowBox.visibility = View.GONE
                downArrowBox.visibility = View.GONE
                leftArrowBox.visibility = View.GONE
            }
            firstGuess = false
        }
        if(dir==direction) {
            guesses[level + doneOnce * firstRoundGuesses] = true
        }
        if(level>=1) {
            if (!guesses[level + doneOnce * firstRoundGuesses - 1]
                && !guesses[level + doneOnce * firstRoundGuesses]) {
                if(doneOnce == 0) firstRoundGuesses = level + 1
                allGuesses = firstRoundGuesses+level+1
                level = 10
            }
        }
        level++
        //Do the test twice:
        if(level>sixMSizes.size-1) {
            if(doneOnce == 1) evaluate()
            else {
                doneOnce = 1
                level = 0
                changeImage()
            }
        }
        else changeImage()
    }

    private fun evaluate() {
        if(allGuesses == 0) allGuesses = firstRoundGuesses+9

        for (i in 0 until firstRoundGuesses) {
            if(!guesses[i] && results[0] == 4) {
                if(i>0) results[0] = distances[i-1]
                else results[0] = 999
                //File.smallestVisibleInDegrees=atan(sixMSizes[i-1]/30000)*57.2957f
            }
        }
        for (i in firstRoundGuesses until allGuesses) {
            if(!guesses[i-firstRoundGuesses] && results[1] == 4) {
                results[1] = distances[i - firstRoundGuesses]
                val svid = sixMSizes[i - firstRoundGuesses] * distance / 30
                if(File.smallestVisibleInDegrees>svid) File.smallestVisibleInDegrees=atan(sixMSizes[i - firstRoundGuesses]/30000)*57.2957f
            }
        }
        if(File.smallestVisibleInDegrees == 0f) {
            File.smallestVisibleInDegrees = atan(sixMSizes.last()/30000)*57.2957f //57: rad to deg
        }

        //Popup:
        val eval: Evaluation = if(results[0] == 4 && results[1] == 4) Evaluation.GOOD
        else {
            if(results[0] <= 6 && results[1] <= 6) Evaluation.QUESTIONABLE
            else Evaluation.BAD
        }
        val toastText: String
        toastText = when(eval) {
            Evaluation.GOOD -> "High visual acuity."
            Evaluation.QUESTIONABLE -> "Mostly adequate visual acuity."
            Evaluation.BAD -> "Low visual acuity."
        }
        Toast.makeText(applicationContext, toastText, Toast.LENGTH_SHORT).show()

        //Writing to file:
        var fileText = "VISUAL ACUITY:\n\tFIRST ATTEMPT:\n"
        for (i in 0 until allGuesses) {
            val res = if(guesses[i]) "CORRECT"
            else "WRONG"
            val distIndex = if(i<firstRoundGuesses) i
            else i-firstRoundGuesses

            if(i==firstRoundGuesses) fileText+="\tSECOND ATTEMPT:\n"
            fileText+="\t\t60/"+distances[distIndex]+":\t"+res+'\n'
        }
        fileText+="\tRESULTS:\n\tFIRST ATTEMPT:\n\t\t60/"+results[0].toString()+
                "\n\tSECOND ATTEMPT:\n\t\t60/"+results[1].toString()+"\n\tEVALUATION:\n\t"+toastText
        File.fileText+=fileText
        val intent = Intent(this, ContrastTestActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        if (menu != null) {
            passButton = menu.getItem(1)
            menuText = menu.getItem(0)
        }
        menuText.isVisible=true
        if(!keyboardConnected) {
            menuText.title = "Can't see:"
            passButton.isVisible=true
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
                guess(4)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

}