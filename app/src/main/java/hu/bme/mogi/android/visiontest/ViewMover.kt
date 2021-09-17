package hu.bme.mogi.android.visiontest

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_contrasttest.*
import java.util.*

object ViewMover {
    fun move(viewToMove: View, bigView: View, rotate: Boolean): Int {
        val params = viewToMove.layoutParams as ConstraintLayout.LayoutParams
        val random = Random()
        val loc =  IntArray(2)
        val widthMiddle = (bigView.width - viewToMove.width)/2
        val heightMiddle = (bigView.height - viewToMove.height)/2
        val width = bigView.width
        val height = bigView.height
        val horWiggleRoom = (0.1f*width).toInt()
        val vertWiggleRoom = (0.1f*height).toInt()

        val dir = (0..3).random()
        when(dir) {
            0 -> {
                loc[0] = widthMiddle - horWiggleRoom/2 + random.nextInt(horWiggleRoom)
                loc[1] = 0 + random.nextInt(vertWiggleRoom)
            } //Up
            1 -> {
                loc[0] = bigView.width - viewToMove.width - random.nextInt(horWiggleRoom)
                loc[1] = heightMiddle - vertWiggleRoom/2 + random.nextInt(vertWiggleRoom)
            } //Right
            2 -> {
                loc[0] = widthMiddle - horWiggleRoom/2 + random.nextInt(horWiggleRoom)
                loc[1] = bigView.height - viewToMove.height - random.nextInt(vertWiggleRoom)
            } //Down
            else -> {
                loc[0] = 0 + random.nextInt(horWiggleRoom)
                loc[1] = heightMiddle - vertWiggleRoom/2 + random.nextInt(vertWiggleRoom)
            } //Left
        }
        params.setMargins(
            loc[0],
            loc[1],
            0,
            0
        )
        viewToMove.layoutParams = params
        if(rotate) viewToMove.rotation=random.nextInt(360).toFloat()

        return dir
    }
}