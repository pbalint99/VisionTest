package hu.bme.mogi.android.visiontest

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_contrasttest.*
import java.util.*

object ViewMover {
    fun move(viewToMove: View, bigView: View, rotate: Boolean) {
        val params = viewToMove.layoutParams as ConstraintLayout.LayoutParams
        val random = Random()
        val loc =  IntArray(2)
        val widthMiddle = (bigView.width - viewToMove.width)/2
        val heightMiddle = (bigView.height - viewToMove.height)/2

        when((0..3).random()) {
            0 -> {
                loc[0] = widthMiddle
                loc[1] = 0
            } //Up
            1 -> {
                loc[0] = bigView.width - viewToMove.width
                loc[1] = heightMiddle
            } //Right
            2 -> {
                loc[0] = widthMiddle
                loc[1] = bigView.height - viewToMove.height
            } //Down
            else -> {
                loc[0] = 0
                loc[1] = heightMiddle
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
    }
}