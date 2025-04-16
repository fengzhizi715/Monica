package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import cn.netdiscovery.monica.config.KEY_GENERAL_SETTINGS
import cn.netdiscovery.monica.domain.GeneralSettings
import cn.netdiscovery.monica.rxcache.rxCache
import com.safframework.rxcache.ext.get
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
const val GenerateGifStatus: Int = 7
const val FlipStatus: Int = 8
const val RotateStatus: Int = 9
const val ResizeStatus: Int = 10
const val ShearingStatus: Int = 11
const val CropSizeStatus: Int = 12


const val ColorCorrectionStatus: Int = 13
const val FilterStatus: Int = 14


const val OpenCVDebugStatus: Int = 15
const val FaceDetectStatus: Int = 16
const val SketchDrawingStatus: Int = 17
const val FaceSwapStatus: Int = 18
const val CartoonStatus: Int = 19


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

    var isGeneralSettings by mutableStateOf(false)
    var isBasic by mutableStateOf(false)
    var isColorCorrection by mutableStateOf(false)
    var isFilter by mutableStateOf(false)
    var isAI by mutableStateOf(false)

    var isShowPreviewWindow by mutableStateOf(false)

    private val queue: LinkedBlockingDeque<BufferedImage> = LinkedBlockingDeque(40)

    // 通用输出框的颜色
    var outputBoxRText by mutableStateOf(rxCache.get<GeneralSettings>(KEY_GENERAL_SETTINGS)?.data?.outputBoxR?:255)
    var outputBoxGText by mutableStateOf(rxCache.get<GeneralSettings>(KEY_GENERAL_SETTINGS)?.data?.outputBoxG?:0)
    var outputBoxBText by mutableStateOf(rxCache.get<GeneralSettings>(KEY_GENERAL_SETTINGS)?.data?.outputBoxB?:0)

    var sizeText by mutableStateOf(rxCache.get<GeneralSettings>(KEY_GENERAL_SETTINGS)?.data?.size?:100)
    var algorithmUrlText by mutableStateOf(rxCache.get<GeneralSettings>(KEY_GENERAL_SETTINGS)?.data?.algorithmUrl?:"")

    fun toOutputBoxScalar() = intArrayOf(outputBoxBText, outputBoxGText, outputBoxRText)

    fun saveGeneralSettings() {
        rxCache.saveOrUpdate(KEY_GENERAL_SETTINGS, GeneralSettings(outputBoxRText, outputBoxGText, outputBoxBText, sizeText, algorithmUrlText))
    }

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
     * @param status 更新为当前的状态
     */
    fun togglePreviewWindowAndUpdateStatus(status:Int) {
        currentStatus = status
        isShowPreviewWindow = true
    }

    /**
     * 关闭当前弹出的页面
     */
    fun closePreviewWindow() {
        resetCurrentStatus()
        togglePreviewWindow(false)
    }

    /**
     * 清空了当前的状态
     */
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