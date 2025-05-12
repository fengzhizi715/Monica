package cn.netdiscovery.monica.utils

import androidx.compose.ui.awt.ComposeWindow
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File
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
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val legalSuffixList: Array<String> = arrayOf("jpg", "jpeg", "png","webp","svg")

fun chooseImage(state: ApplicationState, block:(file: File)->Unit) {
    showFileSelector(
        isMultiSelection = false,
        selectionMode = JFileChooser.FILES_ONLY,
        onFileSelected = {
            state.scope.launchWithLoading {
                val file = it.getOrNull(0)
                if (file != null) {
                    logger.info("load file: ${file.absolutePath}")
                    block.invoke(file)
                }
            }
        }
    )
}

private fun showFileSelector(
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

fun exportImage(
    onFileSelected: (JFileChooser) -> Unit
) {
    JFileChooser().apply {
        this.dialogTitle = "导出图像"

        // 添加格式选项
        val pngFilter = FileNameExtensionFilter("PNG 图像 (*.png)", "png")
        val jpgFilter = FileNameExtensionFilter("JPG 图像 (*.jpg)", "jpg")
        val webpFilter = FileNameExtensionFilter("Webp 图像 (*.webp)", "webp")
        this.addChoosableFileFilter(pngFilter)
        this.addChoosableFileFilter(jpgFilter)
        this.addChoosableFileFilter(webpFilter)
        this.fileFilter = pngFilter // 默认选择 PNG

        val result = this.showSaveDialog(null)

        if (result == JFileChooser.APPROVE_OPTION) {
            onFileSelected(this)
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