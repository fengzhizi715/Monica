package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import client
import cn.netdiscovery.monica.imageprocess.filter.blur.BoxBlurFilter
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.ui.selectedIndex
import cn.netdiscovery.monica.utils.*
import cn.netdiscovery.monica.utils.extension.subImage
import filterNames
import kotlinx.coroutines.*
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.text.Collator
import java.util.*
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
    var isBlur by mutableStateOf(false)

    var isHLS by mutableStateOf(false)
    var isFilter by mutableStateOf(false)

    var isShowPreviewWindow by mutableStateOf(false)

    private val blurFilter = BoxBlurFilter(15,15,1)

    fun togglePreviewWindow(isShow: Boolean = true) {
        isShowPreviewWindow = isShow
    }

    fun onClickImageChoose() {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                scope.launch(Dispatchers.IO) {
                    clickLoadingDisplay {
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
            clickLoadingDisplayWithSuspend {
                if (isHLS) {
                    currentImage = hsl(rawImage!!, saturation, hue, luminance)
                }

                if(isFilter) {
                    val filterName = filterNames[selectedIndex.value]

                    val params = getFilterParam(filterName)

                    if (params!=null) {
                        // 按照参数名首字母进行排序
                        Collections.sort(params) { o1, o2 -> Collator.getInstance(Locale.UK).compare(o1.first, o2.first) }
                        println("sort params: $params")
                    }

                    val array = mutableListOf<Any>()

                    params?.forEach {
                        array.add(it.third)
                    }

                    if (selectedIndex.value>0) {
                        println("filterName: $filterName, array: $array")
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
            val width = (60*xScale).toInt()
            // 打码区域高度
            val height = (60*yScale).toInt()

            val mosaicSize = 40
            //2. 设置各方向绘制的马赛克块个数
            var xcount = 0 // 方向绘制个数
            var ycount = 0 // y方向绘制个数
            if (width % mosaicSize === 0) {
                xcount = width / mosaicSize
            } else {
                xcount = width / mosaicSize + 1
            }
            if (height % mosaicSize === 0) {
                ycount = height / mosaicSize
            } else {
                ycount = height / mosaicSize + 1
            }

            var xTmp = x
            var yTmp = y
            for (i in 0 until xcount) {
                for (j in 0 until ycount) {
                    //马赛克矩形格大小
                    var mwidth = mosaicSize
                    var mheight = mosaicSize
                    if (i == xcount - 1) {   //横向最后一个比较特殊，可能不够一个size
                        mwidth = width - xTmp
                    }
                    if (j == ycount - 1) {  //同理
                        mheight = height - yTmp
                    }
                    //矩形颜色取中心像素点RGB值
                    var centerX = xTmp
                    var centerY = yTmp
                    centerX += if (mwidth % 2 == 0) {
                        mwidth / 2
                    } else {
                        (mwidth - 1) / 2
                    }
                    centerY += if (mheight % 2 == 0) {
                        mheight / 2
                    } else {
                        (mheight - 1) / 2
                    }
                    val color: Color = Color(bufferedImage.getRGB(centerX, centerY))
                    graphics.setColor(color)
                    graphics.fillRect(xTmp, yTmp, mwidth, mheight)
                    yTmp = yTmp + mosaicSize // 计算下一个矩形的y坐标
                }
                yTmp = y // 还原y坐标
                xTmp = xTmp + mosaicSize // 计算x坐标
            }
            // 释放资源
            graphics.dispose()

            currentImage = outputImage
        }
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