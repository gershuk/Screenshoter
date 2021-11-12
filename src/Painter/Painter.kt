package Cutter

import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.awt.Toolkit
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Painter(image: Image) : Stage() {
    private val mainContainer = VBox()
    private val canvasContainer = Pane();
    private val canvas = Canvas()
    private val size = Toolkit.getDefaultToolkit().screenSize
    private var clearImage: Image? = null

    private var imageWidth = 0.0;
    private var imageHeight = 0.0;

    fun canvasToImage(): WritableImage {
        val parameters = SnapshotParameters()
        parameters.fill = Color.TRANSPARENT
        val image = WritableImage(canvas.width.toInt(), canvas.height.toInt())
        canvas.snapshot(parameters, image);
        return image
    }

    fun canvasToImage(rectangle: Rectangle): WritableImage {
        val parameters = SnapshotParameters()
        parameters.fill = Color.TRANSPARENT
        val bounds = rectangle.boundsInParent
        parameters.viewport = Rectangle2D(bounds.minX, bounds.minY, bounds.width, bounds.height)
        val image = WritableImage(bounds.width.toInt(), bounds.height.toInt())
        canvas.snapshot(parameters, image);
        return image
    }

    fun quickSave() {
        val dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd-HH:mm:ss")
        val time = dtf.format(LocalDateTime.now())
        val fileName = "image-$time"
        val file = File("$fileName.png")
        val img = canvasToImage()
        ImageFileManager.saveImage(img, file)
    }

    fun save() {
        val window = Stage()
        val fileChooser = FileChooser()
        val lastPath = ImageFileManager.getLastSavePath();
        if (lastPath != null) {
            fileChooser.initialDirectory = File(lastPath).parentFile
        }
        val extensionFilter = FileChooser.ExtensionFilter("image files (*.png)", "*.png")
        fileChooser.extensionFilters.add(extensionFilter)
        var file = fileChooser.showSaveDialog(window)
        if (file != null) {
            val fileName = file.name
            if (!fileName.toUpperCase().endsWith(".PNG")) {
                file = File(file.absolutePath + ".png")
            }
            val img = canvasToImage()
            ImageFileManager.saveImage(img, file)
        }
    }

    fun setImageIntoCanvas(img: Image) {
        val graphicsContext = canvas.graphicsContext2D
        graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height);
        imageWidth = if (img.width < img.height) img.width * (canvas.height / img.height) else canvas.width;
        imageHeight = if (img.width > img.height) img.height * (canvas.width / img.width) else canvas.height;
        graphicsContext.drawImage(
            img, (canvas.width - imageWidth) / 2, (canvas.height - imageHeight) / 2, imageWidth, imageHeight
        )
        clearImage = canvasToImage()
    }

    init {
        scene = Scene(mainContainer, size.width.toDouble(), size.height.toDouble())
        canvas.width = scene.width
        canvas.height = scene.height
        title = "Paint"

        val cutterRectangle = CutterRectangle()
        val menuBar = MenuBarController(this)
        val pressedKeys = HashSet<KeyCode>()

        scene.onKeyPressed = EventHandler { e: KeyEvent ->
            pressedKeys.add(e.code)
            if (pressedKeys.contains(KeyCode.CONTROL)) {
                if (pressedKeys.contains(KeyCode.S))
                    save()
                if (pressedKeys.contains(KeyCode.Q))
                    quickSave()
            }
        }

        scene.onKeyReleased = EventHandler { e: KeyEvent -> pressedKeys.remove(e.code) }

        canvas.onMouseDragged = EventHandler<MouseEvent> { e ->
            val size = menuBar.getSizeBrash()
            val x = e.x - size / 2
            val y = e.y - size / 2

            if (e.button == MouseButton.PRIMARY) {
                if (menuBar.isEraser()) {
                    val graphicsContext = canvas.graphicsContext2D
                    graphicsContext.clearRect(x, y, size, size)
                    graphicsContext.drawImage(clearImage, x, y, size, size, x, y, size, size)
                } else {
                    canvas.graphicsContext2D.fill = menuBar.getColorBrash()
                    canvas.graphicsContext2D.fillOval(x, y, size, size)
                }
            }

            if (e.button == MouseButton.SECONDARY) {
                cutterRectangle.setOffset(e.x, e.y)
            }
        }

        canvas.onMousePressed =
            EventHandler { e -> if (e.button == MouseButton.SECONDARY) cutterRectangle.startCutting(e.x, e.y) }

        canvas.onMouseReleased = EventHandler { e ->
            if (e.button == MouseButton.SECONDARY) {
                setImageIntoCanvas(canvasToImage(cutterRectangle))
                cutterRectangle.endCutting()
            }
        }

        canvasContainer.children.addAll(canvas, cutterRectangle)
        mainContainer.children.addAll(menuBar, canvasContainer)
        setImageIntoCanvas(image)
    }
}




