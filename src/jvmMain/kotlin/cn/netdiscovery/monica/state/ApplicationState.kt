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
val BlurStatus: Int = 1 shl 0       // 1
val MosaicStatus: Int = 1 shl 1     // 2
val DoodleStatus: Int = 1 shl 2     // 4
val ColorPickStatus: Int = 1 shl 3  // 8
val FlipStatus: Int = 1 shl 4       // 16
val RotateStatus: Int = 1 shl 5     // 32
val ResizeStatus: Int = 1 shl 6     // 64
val CropSizeStatus: Int = 1 shl 7   // 128
val ShearingStatus: Int = 1 shl 8

val EqualizeHistStatus: Int = 1 shl 9
val GammaStatus: Int = 1 shl 10
val LaplaceStatus: Int = 1 shl 11
val USMStatus: Int = 1 shl 12
val ACEStatus: Int = 1 shl 13

val FaceDetectStatus: Int = 1 shl 14


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