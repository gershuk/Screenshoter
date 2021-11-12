package Cutter

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class CutterRectangle : Rectangle(0.0, 0.0, Color.gray(0.0, 0.5)) {
    private var isCutting = false
    private var startX = 0.0
    private var startY = 0.0

    fun startCutting(newX: Double, newY: Double) {
        x = newX
        y = newY
        isCutting = true
        startX = newX
        startY = newY
    }

    fun endCutting() {
        isCutting = false
        x = 0.0
        y = 0.0
        width = 0.0
        height = 0.0
    }

    fun setOffset(newX: Double, newY: Double) {
        if (isCutting) {
            val deltaX: Double = newX - startX
            val deltaY: Double = newY - startY

            if (deltaX > 0) {
                width = deltaX
            } else {
                x = newX
                width = startX - x
            }

            if (deltaY > 0) {
                height = deltaY
            } else {
                y = newY
                height = startY - y
            }
        }
    }
}