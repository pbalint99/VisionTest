package hu.bme.mogi.android.visiontest.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import hu.bme.mogi.android.visiontest.File
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_contrasttest.*
import kotlinx.android.synthetic.main.activity_contrasttest.demoView
import kotlinx.android.synthetic.main.activity_contrasttest.gaussView
import kotlinx.android.synthetic.main.activity_contrasttest.noiseView
import kotlinx.android.synthetic.main.activity_contrasttest.textView
import kotlinx.android.synthetic.main.activity_contrasttest_keyboard.*
import kotlin.math.*


class ContrastTestActivity: AppCompatActivity() {
//    var tryNumber: Int = 1
//    var results: FloatArray = floatArrayOf(0f, 0f, 0f)
    //TODO: apply aspect ratio to noise
    var prevDir = 5
    var level = 0
    var guesses = BooleanArray(40)
    var alphas = FloatArray(40)
    var frequencies = FloatArray(40)
    var alpha = 0.1f
    var cpd = 1.5f
    var started = false
    var displayMetrics = DisplayMetrics()
    var noiseNumber = 0
    var keyboardConnected = true
    lateinit var passButton : MenuItem
    lateinit var menuText : MenuItem
    var changeFreqBasedOnVA = false
    var trials = 6
    var cpdAdd = 2f
    var contrastAdd = 2f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameControllers = File.getGameControllerIds()

        if(resources.configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES &&
            gameControllers.isEmpty()) {
            setContentView(R.layout.activity_contrasttest)
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
            startButton.setOnClickListener{
                start()
                startButton.isEnabled=false
                startButton.text=""
                startButton.setBackgroundColor(Color.TRANSPARENT)
                upBtnC.setBackgroundColor(Color.BLACK)
                rightBtnC.setBackgroundColor(Color.BLACK)
                downBtnC.setBackgroundColor(Color.BLACK)
                leftBtnC.setBackgroundColor(Color.BLACK)
                menuText.title = "Can't see:"
                passButton.isVisible=true
            }
            keyboardConnected = false
        } else setContentView(R.layout.activity_contrasttest_keyboard)

        setFrequency()
        gaussView.alpha=0f

        //set size
        displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val gaussParams = gaussView.layoutParams
        val sharedPref = getSharedPreferences("sp", Context.MODE_PRIVATE) ?: return
        val contrastDegrees = sharedPref.getFloat("contrastDegrees",1f)
        val gaussWidth = ViewMover.degreeToPixels(
            contrastDegrees.toDouble(), displayMetrics, sharedPref
        )
        gaussParams.width = gaussWidth
        gaussParams.height = gaussWidth

        changeFreqBasedOnVA = sharedPref.getBoolean("useVA",false)
        trials = sharedPref.getInt("contrastTrials",6)
        cpd = sharedPref.getFloat("minFreq",1.5f)
        alpha = sharedPref.getFloat("maxContrast",10f)/100
        val maxCpd = sharedPref.getFloat("maxFreq", 4f)
        val minContrast = sharedPref.getFloat("minContrast",4f)/100
        cpdAdd = (maxCpd-cpd)/(trials-1)
        contrastAdd = (minContrast-alpha)/(1-trials)
        //alpha *= 0.01f
        //Toast.makeText(applicationContext,cpdMult.toString()+" "+contrastMult.toString(),Toast.LENGTH_SHORT).show()
    }

    private fun setFrequency() {
        //A térszög a teljes alfájú kör sugara legyen?
        val width = 100
        val pixels = IntArray(width * width)
        var hsv: FloatArray
        val svd = File.smallestVisibleInDegrees
        val alpha = 0.2f //A teljes alfájú kör sugara, max 0.5f
        val wfel = width.toFloat()/2
        val wa = wfel*2*alpha

        if(svd>2/cpd && changeFreqBasedOnVA) {
            cpd = 1/svd
            //Toast.makeText(applicationContext,"Spatial freq. max at $cpd cpd",Toast.LENGTH_SHORT).show()
        }
        val freq = (cpd*4).roundToInt()/2
0
        var index = 0
        for (y in 0 until width) {
            val value = sin(2f/freq + (6.28f * y.toFloat() * freq) / width ) / 2 + 0.5f
            for (x in 0 until width) {
                hsv= floatArrayOf(
                    0f,
                    0f,
                    value
                )
                val hsvColor = Color.HSVToColor(hsv)
                val r = sqrt((x-wfel).pow(2)+(y-wfel).pow(2))
                if(r<=wa) pixels[index] = hsvColor
                else if (r>wa && r<wfel) {
                    val alfa = ((1 - (r-wa)/(wfel-wa))*255).toInt()
                    pixels[index] = Color.argb(alfa,hsvColor.red,hsvColor.green,hsvColor.blue)
                } else {
                    pixels[index] = Color.TRANSPARENT
                }
                index++
            }
        }

        val bmOut = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        bmOut.setPixels(pixels, 0, width, 0, 0, width, width)
        gaussView.setImageBitmap(bmOut)
        //File.saveImage(bmOut)
    }

    //TODO: lépcsős fentről-lentről megbecsülés
    private fun guess(dir: Int) {
        if(!started) return
        val correct = dir == prevDir
        guesses[level] = correct
        frequencies[level] = cpd
        alphas[level] = alpha

        cpd += cpdAdd
        alpha -= contrastAdd
        //Toast.makeText(applicationContext,alpha.toString()+" "+contrastAdd.toString(),Toast.LENGTH_SHORT).show()
        gaussView.alpha = alpha

        if(level == trials-1) {
            evaluate()
            return
        }

        prevDir = ViewMover.move(gaussView, noiseView, true)

        applyNoise()
        setFrequency()

        level++
    }

    private fun evaluate() {
        var fileText="\n\nCONTRAST SENSITIVITY:\n"
        var result = "High contrast sensitivity."
        var evalInt = 0
        var mistakeMade = false

        for (i in 0 until trials) {
            if(!guesses[i] && !mistakeMade){
                when(i) {
                    in 0..4 -> {
                        result = "Low contrast sensitivity."
                        evalInt = 1
                    }
                    else -> {
                        result = "Mostly adequate contrast sensitivity."
                        evalInt = 1
                    }
                }
                mistakeMade = true
            }
            val res = if(guesses[i]) "CORRECT"
            else "WRONG"
            val freqRound = round(frequencies[i], 2)
            val alphaRound = round(alphas[i] * 100, 2)
            fileText+="\tCPD: "+freqRound.toString()+"\tCONTR: "+alphaRound.toString()+"%\t"+res+"\n"
        }
        fileText+= "\tEVALUATION:\n\t$result\n"
        File.fileText+=fileText
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra("type",1)
            putExtra("result",evalInt)
        }
        startActivity(intent)
    }

    private fun applyNoise() {
        when(noiseNumber) {
            0 -> noiseView.setImageResource(R.drawable.noise1)
            1 -> noiseView.setImageResource(R.drawable.noise2)
            2 -> noiseView.setImageResource(R.drawable.noise3)
            else -> noiseView.setImageResource(R.drawable.noise4)
        }
        noiseNumber++
        if(noiseNumber==4) noiseNumber=0
    }

    private fun round(number: Float, decimalPlaces: Int): Float {
        val number3digits = (number * 10f.pow(decimalPlaces + 2)).roundToInt() / 10f.pow(
            decimalPlaces + 2
        )
        val number2digits = (number3digits * 10f.pow(decimalPlaces + 1)).roundToInt() / 10f.pow(
            decimalPlaces + 1
        )
        return (number2digits * 10f.pow(decimalPlaces)).roundToInt() / 10f.pow(decimalPlaces)
    }

    fun start() {
        prevDir = ViewMover.move(gaussView, noiseView, true)
        applyNoise()
        gaussView.alpha = alpha

        demoView.alpha=0f
        textView.text=""
        menuText.isVisible=true
        started = true
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
            KeyEvent.KEYCODE_ESCAPE, KeyEvent.KEYCODE_ENTER -> {
                true
            }
            KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_BUTTON_X,  KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_BUTTON_START, KeyEvent.KEYCODE_BUTTON_SELECT -> {
                if (!started) {
                    start()
                    startTextView.setBackgroundColor(Color.TRANSPARENT)
                    startTextView.text = ""
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

}