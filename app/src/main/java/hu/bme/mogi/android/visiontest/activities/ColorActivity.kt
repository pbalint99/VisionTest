package hu.bme.mogi.android.visiontest.activities

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
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
                    ), 300, 600
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
            ), 60
        ))

    }

    //TODO: lépcsős fentről-lenntről megbecsülés
    private fun guess(dir: Int) {
        ViewMover.move(dotView, noiseView, false)

        noiseView.setImageBitmap(
            Noise.applyNoise(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
                ), 300, 600
            )
        )
        dotView.setImageBitmap(
            Noise.applyNoiseCircle(
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.black_square
            ), 60
        ))
    }


}