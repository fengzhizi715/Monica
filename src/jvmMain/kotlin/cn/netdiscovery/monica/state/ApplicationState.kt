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

    var saturation by mutableStateOf(0f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )

    var scale by mutableStateOf(1f )

    var isBasic by mutableStateOf(false)
    var isMosaic by mutableStateOf(false)
    var isBlur by mutableStateOf(false)
    var isDoodle by mutableStateOf(false)

    var isCrop by mutableStateOf(false)
    var isCropSize by mutableStateOf(false)

    var isHLS by mutableStateOf(false)
    var isFilter by mutableStateOf(false)

    var isShowPreviewWindow by mutableStateOf(false)

    private val queue: LinkedBlockingDeque<BufferedImage> = LinkedBlockingDeque(20)

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