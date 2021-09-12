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

    private var firstpressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        plusButton.setOnClickListener{
            if(!firstpressed) {
                plusButton.text="+"
                ViewMover.move(dotView, noiseView, false)
                noiseView.setImageBitmap(
                    Noise.applyNoise(
                        BitmapFactory.decodeResource(
                            applicationContext.resources, R.drawable.black_square
                        ), 300, 600
                    )
                )
                firstpressed=true
            }
            dotView.alpha+=0.01f

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
//        dotView.setColorFilter(Color.BLUE, PorterDuff.Mode.ADD)

        val dotBitmap = Noise.applyNoiseCircle(
            BitmapFactory.decodeResource(
                applicationContext.resources, R.drawable.black_square
            ), 60
        )
//        radialMask(dotBitmap)
//        val canvas = Canvas()
//        val paint = Paint()
//        canvas.drawBitmap(dotBitmap, 0f, 0f, paint)
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
//        canvas.drawBitmap(
//            BitmapFactory.decodeResource(
//                applicationContext.resources, R.drawable.pont
//            ), 0f, 0f, paint
//        )

//        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        val gradient = RadialGradient((dotWidth/2).toFloat(), (dotWidth/2).toFloat(), (dotWidth/2).toFloat(),
//            intArrayOf(-0x1, -0x1, 0x00FFFFFF), null, Shader.TileMode.CLAMP)
//        canvas.drawBitmap(dotBitmap,0f,0f,shaderPaint)
//        shaderPaint.shader=gradient
        dotView.setImageBitmap(dotBitmap)

    }

    //TODO: lépcsős fentről-lenntről megbecsülés
    private fun guess(dir: Int) {
        ViewMover.move(dotView, noiseView, false)
    }

//    private fun radialMask(imageToApplyMaskTo: Bitmap) {
//        val canvas = Canvas(imageToApplyMaskTo)
//
//        val center = imageToApplyMaskTo.width * 0.5f
//        val radius = imageToApplyMaskTo.height * 0.7f
//        val gradient = RadialGradient(
//            center, center, radius,
//            Color.BLUE, Color.BLACK, Shader.TileMode.CLAMP
//        )
//        val p = Paint()
//        p.shader = gradient
//        p.color = 0x000000
//        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
//        canvas.drawRect(
//            0f, 0f, imageToApplyMaskTo.width.toFloat(),
//            imageToApplyMaskTo.height.toFloat(), p
//        )
//    }


}