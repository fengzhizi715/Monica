package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import kotlinx.coroutines.CoroutineScope
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.state.ApplicationState
 * @author: Tony Shen
 * @date: 2024/4/26 10:42
 * @version: V1.0 <描述当前版本功能>
 */
val BlurStatus: Int = 1
val MosaicStatus: Int = 2
val DoodleStatus: Int = 3
val ColorPickStatus: Int = 4
val FlipStatus: Int = 5
val RotateStatus: Int = 6
val ResizeStatus: Int = 7
val ShearingStatus: Int = 8
val CropSizeStatus: Int = 9


val EqualizeHistStatus: Int = 10
val ClaheStatus: Int = 11
val GammaStatus: Int = 12
val LaplaceStatus: Int = 13
val USMStatus: Int = 14
val ACEStatus: Int = 15


val OpenCVDebugStatus: Int = 16
val FaceDetectStatus: Int = 17
val SketchDrawingStatus: Int = 18
val FaceSwapStatus: Int = 19


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

    var rawImage: BufferedImage? by mutableStateOf(null)
    var currentImage: BufferedImage? by mutableStateOf( rawImage )
    var rawImageFile: File? = null

    // 表示用于点击了哪个功能
    var currentStatus by mutableStateOf(0)

    var isBasic by mutableStateOf(false)

    var isHLS by mutableStateOf(false)

    var isEnhance by mutableStateOf(false)

    var isFilter by mutableStateOf(false)

    var isAI by mutableStateOf(false)

    var isShowPreviewWindow by mutableStateOf(false)

    private val queue: LinkedBlockingDeque<BufferedImage> = LinkedBlockingDeque(40)

    fun getLastImage():BufferedImage? = queue.pollFirst(1, TimeUnit.SECONDS)

    fun addQueue(bufferedImage: BufferedImage) {
        queue.putFirst(bufferedImage)
    }

    fun clearQueue() {
        queue.clear()
    }

    fun togglePreviewWindow(isShow: Boolean = true) {
        isShowPreviewWindow = isShow
    }

    fun resetCurrentStatus() {
        currentStatus = 0
    }

    fun clearImage() {
        this.rawImage = null
        this.currentImage = null
        this.rawImageFile = null
    }

    fun showTray(
        msg: String,
        title: String = "通知",
        type: Notification.Type = Notification.Type.Info
    ) {
        val notification = Notification(title, msg, type)
        trayState.sendNotification(notification)
    }
}