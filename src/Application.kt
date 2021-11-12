package Cutter

import javafx.application.Application
import javafx.stage.Stage

class Screenshoter : Application() {
    override fun start(stage: Stage) {
        val mainWindow = MainWindow()
        mainWindow.show();
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(Screenshoter::class.java)
        }
    }
}