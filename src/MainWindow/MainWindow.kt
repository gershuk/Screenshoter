package Cutter

import Cutter.ImageFileManager.Factory.getLastSavePath
import javafx.animation.PauseTransition
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.util.Duration
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit

class MainWindow : Stage() {
    private val openLastImageButton = Button("Last save")
    private val screenshotButton = Button("Take screenshot")
    private val openImageButton = Button("Open Image")
    private val slider = Slider(0.0, 10.0, 0.0)
    private val checkBox = CheckBox("Hide")
    private val container = HBox(openLastImageButton,openImageButton, screenshotButton, slider, checkBox)

    private fun takeScreenshot() {
        if (checkBox.isSelected) {
            opacity = 0.0
            isIconified = true
        }

        val pause = PauseTransition(Duration.seconds(slider.value + 0.01))
        pause.onFinished = EventHandler {
            Painter(getScreenshot()).show()
            opacity = 1.0
        }
        pause.play();
    }

    init {
        container.padding = Insets(10.0)
        slider.padding = Insets(0.0, 10.0, 0.0, 10.0)
        title = "Cutter"

        openLastImageButton.onAction = EventHandler {
            val img = ImageFileManager.readImage(getLastSavePath().orEmpty())
            if (img != null) Painter(img).show()
        }

        screenshotButton.onAction = EventHandler { takeScreenshot() }

        openImageButton.onAction = EventHandler {
            val img = ImageFileManager.readImage()
            if (img != null) Painter(img).show()
        }

        scene = Scene(container, 550.0, 50.0)
        scene.onKeyPressed = EventHandler { e: KeyEvent -> if (e.code == KeyCode.P) takeScreenshot() }
    }

    private fun getScreenshot() =
        SwingFXUtils.toFXImage(Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize)), null)
}

