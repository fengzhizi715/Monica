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
const val ZoomPreviewStatus: Int = 1
const val BlurStatus: Int = 2
const val MosaicStatus: Int = 3
const val DoodleStatus: Int = 4
const val ShapeDrawingStatus: Int = 5
const val ColorPickStatus: Int = 6
const val FlipStatus: Int = 7
const val RotateStatus: Int = 8
const val ResizeStatus: Int = 9
const val ShearingStatus: Int = 10
const val CropSizeStatus: Int = 11


const val ColorCorrectionStatus: Int = 12


const val EqualizeHistStatus: Int = 13
const val ClaheStatus: Int = 14
const val GammaStatus: Int = 15
const val LaplaceStatus: Int = 16
const val USMStatus: Int = 17
const val ACEStatus: Int = 18


const val OpenCVDebugStatus: Int = 19
const val FaceDetectStatus: Int = 20
const val SketchDrawingStatus: Int = 21
const val FaceSwapStatus: Int = 22


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

    var isColorCorrection by mutableStateOf(false)

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

    /**
     * 弹出新的页面，更新 currentStatus 状态
     */
    fun togglePreviewWindowAndUpdateStatus(status:Int) {
        currentStatus = status
        isShowPreviewWindow = true
    }

    fun resetCurrentStatus() {
        currentStatus = 0
    }

    /**
     * 关闭当前弹出的页面
     */
    fun closeWindows() {
        resetCurrentStatus()
        togglePreviewWindow(false)
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