package Cutter

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.stage.FileChooser
import java.io.*
import javax.imageio.ImageIO

class ImageFileManager {
    companion object Factory {
        private var lastSaveFilePath:String? = null;

        fun readImage(path: String): WritableImage? {
            return try {
                val bufferedImage = ImageIO.read(File(path))
                SwingFXUtils.toFXImage(bufferedImage, null)
            } catch (e: IOException) {
                null
            }
        }

        fun readImage(): WritableImage? {
            return try {
                val fileChooser = FileChooser()
                val lastPath = getLastSavePath();
                if (lastPath != null) {
                    fileChooser.initialDirectory = File(lastPath).parentFile
                }
                val extFilterJPG = FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg")
                val extFilterPNG = FileChooser.ExtensionFilter("PNG files (*.png)", "*.png")
                fileChooser.extensionFilters.addAll(extFilterJPG, extFilterPNG)
                SwingFXUtils.toFXImage(ImageIO.read(fileChooser.showOpenDialog(null)), null)
            } catch (e: IOException) {
                null
            }
        }

        fun saveImage(img: WritableImage, file: File) {
            DataOutputStream(FileOutputStream("config.txt")).use { dos -> dos.writeUTF(file.absolutePath) }
            lastSaveFilePath = file.absolutePath
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
        }

        fun getLastSavePath(): String? {
            if (lastSaveFilePath == null) {
                try {
                    lastSaveFilePath = DataInputStream(FileInputStream("config.txt")).readUTF()
                } catch (e: IOException) {
                    return null
                }
            }
            return lastSaveFilePath
        }
    }
}