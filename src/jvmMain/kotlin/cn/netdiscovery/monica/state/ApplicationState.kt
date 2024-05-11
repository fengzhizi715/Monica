package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import cn.netdiscovery.monica.imageprocess.filter.blur.BoxBlurFilter
import cn.netdiscovery.monica.imageprocess.subImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Color
import java.awt.Graphics
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

    var isCrop by mutableStateOf(false)

    var isHLS by mutableStateOf(false)
    var isFilter by mutableStateOf(false)

    var isShowPreviewWindow by mutableStateOf(false)

    private val blurFilter = BoxBlurFilter(15,15,1)

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

    fun blur(width:Int, height:Int,offset: Offset) {
        scope.launch(Dispatchers.IO) {
            val bufferedImage = currentImage!!

            val srcWidth = bufferedImage.width
            val srcHeight = bufferedImage.height

            val xScale = (srcWidth.toFloat()/width)
            val yScale = (srcHeight.toFloat()/height)

            // 打码区域左上角x坐标
            val x = (offset.x*xScale).toInt()
            // 打码区域左上角y坐标
            val y = (offset.y*yScale).toInt()
            // 打码区域宽度
            val width = (100*xScale).toInt()
            // 打码区域高度
            val height = (100*yScale).toInt()

            var tempImage = bufferedImage.subImage(x,y,width,height)
            tempImage = blurFilter.transform(tempImage)

            val outputImage = BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB)
            val graphics2D = outputImage.createGraphics()
            graphics2D.drawImage(bufferedImage, 0, 0, null)
            graphics2D.drawImage(tempImage, x, y, width, height, null)
            graphics2D.dispose()

            queue.putFirst(currentImage)
            currentImage = outputImage
        }
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