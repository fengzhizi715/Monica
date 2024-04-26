package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.TrayState
import cn.netdiscovery.monica.utils.showFileSelector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.state.ApplicationState
 * @author: Tony Shen
 * @date: 2024/4/26 10:42
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun rememberApplicationState(
    scope: CoroutineScope,
    trayState: TrayState
) = remember {
    ApplicationState(scope, trayState)
}

class ApplicationState(val scope:CoroutineScope,
                       val trayState: TrayState) {

    lateinit var window: ComposeWindow

    var rawImg: BufferedImage? by mutableStateOf(null)
    var showImg: BufferedImage? by mutableStateOf( rawImg )
    var rawImgFile: File? = null

    var saturation by mutableStateOf(0.8f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )

    var topPercent by mutableStateOf(0.3f)
    var bottomPercent by mutableStateOf(0.3f)

    var isFilterImg by mutableStateOf(true)
    var isBlur by mutableStateOf(true)

    var isShowGuideLine by mutableStateOf(false)

    var outputPath by mutableStateOf("")
    var isUsingSourcePath by mutableStateOf(false)

    var dialogTitle by mutableStateOf("")
    var dialogMsg by mutableStateOf("")
    var dialogSureBtnText by mutableStateOf("")
    var dialogCancelBtnText by mutableStateOf("")
    var onDialogSure: (() -> Unit)? = null
    var onDialogCloseRequest: (() -> Unit)? = null

    var isShowPreviewWindow by mutableStateOf(false)

    fun onClickImgChoose() {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                scope.launch(Dispatchers.IO) {
                    val file = it.getOrNull(0)
                    if (file != null) {
                        rawImg = ImageIO.read(file)
                        showImg = rawImg
                        rawImgFile = file
                    }
                }
            }
        )
    }

    fun togglePreviewWindow(isShow: Boolean = true) {
        isShowPreviewWindow = isShow
    }
}