package cn.netdiscovery.monica.utils

import androidx.compose.ui.awt.ComposeWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.FileChoose
 * @author: Tony Shen
 * @date: 2024/4/26 10:57
 * @version: V1.0 <描述当前版本功能>
 */
val legalSuffixList: Array<String> = arrayOf("jpg", "jpeg", "png")

fun showFileSelector(
    suffixList: Array<String> = legalSuffixList,
    isMultiSelection: Boolean = true,
    selectionMode: Int = JFileChooser.FILES_AND_DIRECTORIES, // 可以选择目录和文件
    selectionFileFilter: FileNameExtensionFilter? = FileNameExtensionFilter("图片(${legalSuffixList.contentToString()})", *suffixList), // 文件过滤
    onFileSelected: (Array<File>) -> Unit
) {
    JFileChooser().apply {
        try {
            val lookAndFeel = UIManager.getSystemLookAndFeelClassName()
            UIManager.setLookAndFeel(lookAndFeel)
            SwingUtilities.updateComponentTreeUI(this)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        fileSelectionMode = selectionMode
        isMultiSelectionEnabled = isMultiSelection
        fileFilter = selectionFileFilter

        val result = showOpenDialog(ComposeWindow())
        if (result == JFileChooser.APPROVE_OPTION) {
            if (isMultiSelection) {
                onFileSelected(this.selectedFiles)
            }
            else {
                val resultArray = arrayOf(this.selectedFile)
                onFileSelected(resultArray)
            }
        }
    }
}

fun dropFileTarget(
    onFileDrop: (List<String>) -> Unit
): DropTarget {
    return object : DropTarget() {
        override fun drop(event: DropTargetDropEvent) {

            event.acceptDrop(DnDConstants.ACTION_REFERENCE)
            val dataFlavors = event.transferable.transferDataFlavors
            dataFlavors.forEach {
                if (it == DataFlavor.javaFileListFlavor) {
                    val list = event.transferable.getTransferData(it) as List<*>

                    val pathList = mutableListOf<String>()
                    list.forEach { filePath ->
                        pathList.add(filePath.toString())
                    }
                    onFileDrop(pathList)
                }
            }
            event.dropComplete(true)
        }
    }
}

suspend fun BufferedImage.saveImg(saveFile: File?, quality: Float = 0.8f) {
    withContext(Dispatchers.IO) {
        val outputStream = ImageIO.createImageOutputStream(saveFile)
        val jpgWriter: ImageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
        val jpgWriteParam: ImageWriteParam = jpgWriter.defaultWriteParam
        jpgWriteParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
        jpgWriteParam.compressionQuality = quality
        jpgWriter.output = outputStream
        val outputImage = IIOImage(this@saveImg, null, null)
        jpgWriter.write(null, outputImage, jpgWriteParam)
        jpgWriter.dispose()
        outputStream.flush()
        outputStream.close()
    }
}

fun File.getUniqueFile(sourceFile: File = File("")): File {
    var newFile = this

    if (newFile.isDirectory) {
        newFile = File(newFile, sourceFile.name)
    }

    var index = 1
    while (newFile.exists()) {
        newFile = File(newFile.parentFile, "${newFile.nameWithoutExtension}($index).${newFile.extension}")
        index++
    }

    return newFile
}