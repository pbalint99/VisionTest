package hu.bme.mogi.android.visiontest.activities

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import hu.bme.mogi.android.visiontest.Noise
import hu.bme.mogi.android.visiontest.R
import hu.bme.mogi.android.visiontest.ViewMover
import kotlinx.android.synthetic.main.activity_color.*
import kotlinx.android.synthetic.main.activity_contrasttest.downBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.leftBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.noiseView
import kotlinx.android.synthetic.main.activity_contrasttest.plusButton
import kotlinx.android.synthetic.main.activity_contrasttest.rightBtnC
import kotlinx.android.synthetic.main.activity_contrasttest.upBtnC

//TODO: background, dot unnecessary
class ColorActivity: AppCompatActivity() {
    var bgColor = 90f
    var dotColor = 360f
    var prevDir = 5
    var level = 0
    var results = BooleanArray(3)
    private var aspectRatio = 0
    private var noiseDensity = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        upBtnC.isEnabled = false
        downBtnC.isEnabled = false
        leftBtnC.isEnabled = false
        rightBtnC.isEnabled = false

        //Set sizes
        val params = dotView.layoutParams
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dotWidth = displayMetrics.widthPixels/5
        aspectRatio = displayMetrics.heightPixels/displayMetrics.widthPixels
        params.width = dotWidth
        params.height = dotWidth

        plusButton.setOnClickListener {
            plusButton.isEnabled = false
            plusButton.text = ""
            plusButton.setBackgroundColor(Color.TRANSPARENT)

            upBtnC.isEnabled = true
            downBtnC.isEnabled = true
            leftBtnC.isEnabled = true
            rightBtnC.isEnabled = true
            prevDir = ViewMover.move(dotView, noiseView, false)

            applyNoises()
        }

        //Button Listeners:
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
        //Seekbars:
//        seekBar?.setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seek: SeekBar,
//                                           progress: Int, fromUser: Boolean) {
//                // write custom code for progress is changed
//            }
//
//            override fun onStartTrackingTouch(seek: SeekBar) {
//                // write custom code for progress is started
//            }
//
//            override fun onStopTrackingTouch(seek: SeekBar) {
//                // write custom code for progress is stopped
//                bgColor=60f+seekBar.progress
//                noiseView.setImageBitmap(
//                    Noise.applyNoise(
//                        BitmapFactory.decodeResource(
//                            applicationContext.resources, R.drawable.background
//                        ), noiseDensity, noiseDensity*aspectRatio, bgColor
//                    )
//                )
//                dotView.setImageBitmap(Noise.applyNoiseCircle(
//                    BitmapFactory.decodeResource(
//                        applicationContext.resources, R.drawable.dot
//                    ), noiseDensity/10, dotColor, bgColor
//                ))
//                ViewMover.move(dotView, noiseView, false)
//            }
//        })
//        seekBar2?.setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seek: SeekBar,
//                                           progress: Int, fromUser: Boolean) {
//                // write custom code for progress is changed
//            }
//
//            override fun onStartTrackingTouch(seek: SeekBar) {
//                // write custom code for progress is started
//            }
//
//            override fun onStopTrackingTouch(seek: SeekBar) {
//                // write custom code for progress is stopped
//                dotColor=260f+seekBar.progress
//                noiseView.setImageBitmap(
//                    Noise.applyNoise(
//                        BitmapFactory.decodeResource(
//                            applicationContext.resources, R.drawable.background
//                        ), noiseDensity, noiseDensity*aspectRatio, bgColor
//                    )
//                )
//                dotView.setImageBitmap(Noise.applyNoiseCircle(
//                    BitmapFactory.decodeResource(
//                        applicationContext.resources, R.drawable.dot
//                    ), noiseDensity/10, dotColor, bgColor
//                ))
//                ViewMover.move(dotView, noiseView, false)
//            }
//        })


    }

    //TODO: lépcsős fentről-lenntről megbecsülés

    //TODO: Ötlet: hol NINCS pötty?
    private fun guess(dir: Int) {
        results[level] = dir == prevDir

        if(level>1) {
            evaluate()
            return
        }

        prevDir = ViewMover.move(dotView, noiseView, false)

        applyNoises()

        level++
    }

    private fun evaluate() {
        var correct = 0
        for (i in 0..2) {
            if (results[i]) correct++
        }
        Toast.makeText(applicationContext,"Your result is: "+correct+"/3",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun applyNoises() {
        noiseView.setImageBitmap(
            Noise.applyNoise(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
                ), noiseDensity, noiseDensity*aspectRatio, bgColor
            )
        )
        dotView.setImageBitmap(
            Noise.applyNoiseCircle(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
                ), noiseDensity/8, dotColor, bgColor
            )
        )
    }
}