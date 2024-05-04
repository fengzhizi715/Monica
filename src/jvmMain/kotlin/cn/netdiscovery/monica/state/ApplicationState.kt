package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import client
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.ui.selectedIndex
import cn.netdiscovery.monica.utils.*
import filterNames
import kotlinx.coroutines.*
import java.awt.Color
import java.awt.Graphics
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

    var rawImage: BufferedImage? by mutableStateOf(null)
    var currentImage: BufferedImage? by mutableStateOf( rawImage )
    var lastImage: BufferedImage? by mutableStateOf( rawImage )
    var rawImageFile: File? = null

    var saturation by mutableStateOf(0f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )

    var isBasic by mutableStateOf(false)
    var isMosaic by mutableStateOf(false)
    var isHLS by mutableStateOf(false)
    var isFilter by mutableStateOf(false)

    var isShowPreviewWindow by mutableStateOf(false)

    fun togglePreviewWindow(isShow: Boolean = true) {
        isShowPreviewWindow = isShow
    }

    fun onClickImageChoose() {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                scope.launch(Dispatchers.IO) {
                    loadingDisplay {
                        val file = it.getOrNull(0)
                        if (file != null) {
                            rawImage = ImageIO.read(file)
                            currentImage = rawImage
                            rawImageFile = file
                        }
                    }
                }
            }
        )
    }

    fun onClickPreviewImage() {
        scope.launch {
            loadingDisplayWithSuspend {
                if (isHLS) {
                    currentImage = hsl(rawImage!!, saturation, hue, luminance)
                }

                if(isFilter) {
                    val filterName = filterNames[selectedIndex.value]

                    val params = getFilterParam(filterName)

                    val array = mutableListOf<Any>()
                    params?.forEach {
                        array.add(it.third)
                    }

                    if (selectedIndex.value>0) {
                        println("filterName: $filterName, params: $array")
                    }

                    lastImage = currentImage
                    currentImage = doFilter(filterName,array,this@ApplicationState)
                }
            }
        }
    }

    fun mosaic(width:Int, height:Int,offset: Offset) {
        scope.launch(Dispatchers.IO) {
            val bufferedImage = currentImage!!

            val srcWidth = bufferedImage.width
            val srcHeight = bufferedImage.height

            val xScale = (srcWidth.toFloat()/width)
            val yScale = (srcHeight.toFloat()/height)

            // 创建与输入图像相同大小的新图像
            val outputImage = BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB)
            // 创建画笔
            val graphics: Graphics = outputImage.graphics
            // 将原始图像绘制到新图像中
            graphics.drawImage(bufferedImage, 0, 0, null)
            // 打码区域左上角x坐标
            val x = (offset.x*xScale).toInt()
            // 打码区域左上角y坐标
            val y = (offset.y*yScale).toInt()
            // 打码区域宽度
            val width = (50*xScale).toInt()
            // 打码区域高度
            val height = (50*yScale).toInt()
            graphics.color = Color.GRAY
            graphics.fillRect(x, y, width, height)
            // 释放资源
            graphics.dispose()

            currentImage = outputImage
        }
    }

    fun loadUrl(picUrl:String) {
        scope.launch {
            rawImage = client.getImage(url = picUrl)

            currentImage = rawImage
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