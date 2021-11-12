package Cutter

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.Node

class MenuBarController(private val painter: Painter) : MenuBar(){
    private var toolsMenu = Menu("Tools")
    private var fileMenu = Menu("File")
    private var slider = Slider(1.0, 100.0, 10.0)
    private var openImageButton = Button("Open Image")
    private var saveImageButton = Button("Save Image")
    private var colorPicker = ColorPicker()
    private var eraserCheckBox = CheckBox("Eraser")

    fun getSizeBrash() = slider.value

    fun getColorBrash() = colorPicker.value

    fun isEraser() = eraserCheckBox.isSelected

    private fun addElementsToMenu(menu: Menu,vararg nodes: Node) {
        for (node in nodes) {
            val customMenuItem = CustomMenuItem()
            customMenuItem.content = node
            customMenuItem.content.style = "-fx-background-color: GRAY";
            customMenuItem.isHideOnClick = false
            menu.items.add(customMenuItem)
        }
    }

    init {
        menus.addAll(fileMenu,toolsMenu)
        addElementsToMenu(toolsMenu,slider, colorPicker, eraserCheckBox)

        openImageButton.onAction = EventHandler {
            val img = ImageFileManager.readImage()
            if (img != null) painter.setImageIntoCanvas(img)
        }

        saveImageButton.onAction = EventHandler { painter.save() }

        addElementsToMenu(fileMenu,openImageButton, saveImageButton)
    }
}
