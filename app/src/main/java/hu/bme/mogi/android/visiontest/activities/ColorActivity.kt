package hu.bme.mogi.android.visiontest.activities

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
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


class ColorActivity: AppCompatActivity() {
    var bgColor = 90f
    var dotColor = 360f
    var prevDir = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        plusButton.setOnClickListener {
            plusButton.isEnabled = false
            ViewMover.move(dotView, noiseView, false)
            noiseView.setImageBitmap(
                Noise.applyNoise(
                    BitmapFactory.decodeResource(
                        applicationContext.resources, R.drawable.black_square
                    ), 300, 600, bgColor
                )
            )
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

        val params = dotView.layoutParams
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dotWidth = displayMetrics.widthPixels/5
        params.width = dotWidth
        params.height = dotWidth

        dotView.setImageBitmap(Noise.applyNoiseCircle(
            BitmapFactory.decodeResource(
                applicationContext.resources, R.drawable.black_square
            ), 60, dotColor, bgColor
        ))

    }

    //TODO: lépcsős fentről-lenntről megbecsülés

    //TODO: Ötlet: hol NINCS pötty?
    private fun guess(dir: Int) {
        if (dir == prevDir) Toast.makeText(applicationContext,"correct",Toast.LENGTH_SHORT).show()

        prevDir = ViewMover.move(dotView, noiseView, false)

        noiseView.setImageBitmap(
            Noise.applyNoise(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
                ), 300, 600, bgColor
            )
        )
        dotView.setImageBitmap(
            Noise.applyNoiseCircle(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
            ), 60,dotColor,bgColor
        ))


    }


}