package cn.netdiscovery.monica.ui.preview

import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.config.KEY_GENERAL_SETTINGS
import cn.netdiscovery.monica.domain.GeneralSettings
import cn.netdiscovery.monica.http.httpClient
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.filter.blur.FastBlur2D
import cn.netdiscovery.monica.imageprocess.utils.extension.*
import cn.netdiscovery.monica.imageprocess.utils.writeImageFile
import cn.netdiscovery.monica.imageprocess.utils.writeImageFileAsWebP
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.exportImage
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import cn.netdiscovery.monica.utils.logger
import com.safframework.kotlin.coroutines.IO
import com.safframework.rxcache.ext.get
import kotlinx.coroutines.launch
import org.slf4j.Logger
import showTopToast
import java.awt.Color
import java.awt.Graphics
import java.io.File
import javax.imageio.ImageIO
import javax.swing.filechooser.FileNameExtensionFilter


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.preview.PreviewViewModel
 * @author: Tony Shen
 * @date: 2024/5/7 20:30
 * @version: V1.0 <描述当前版本功能>
 */
class PreviewViewModel {

    private val logger: Logger = logger<PreviewViewModel>()

    private val blurFilter = FastBlur2D(15)

    fun loadUrl(picUrl:String, state: ApplicationState) {
        logger.info("load picUrl: $picUrl")

        state.scope.launchWithSuspendLoading {
            try {
                val inputStream = httpClient.get(picUrl).body?.byteStream()
                val bufferedImage = ImageIO.read(inputStream)

                state.rawImage = bufferedImage
                state.currentImage = state.rawImage
            } catch (_: Exception) {
            }
        }
    }

    fun recoverImage(state: ApplicationState) {
        state.currentImage = state.rawImage
        state.clearQueue()
    }

    fun getLastImage(state: ApplicationState) {
        state.getLastImage()?.let {
            state.currentImage = it
        }
    }

    fun blur(width:Int, height:Int,offset: Offset,state: ApplicationState) {

        state.scope.launch(IO) {
            val bufferedImage = state.currentImage!!

            val srcWidth = bufferedImage.width
            val srcHeight = bufferedImage.height

            val xScale = (srcWidth.toFloat()/width)
            val yScale = (srcHeight.toFloat()/height)

            val size = rxCache.get<GeneralSettings>(KEY_GENERAL_SETTINGS)?.data?.size?:100

            // 打码区域左上角x坐标
            val x = (offset.x*xScale).toInt()
            // 打码区域左上角y坐标
            val y = (offset.y*yScale).toInt()
            // 打码区域宽度
            val width = (size*xScale).toInt()
            // 打码区域高度
            val height = (size*yScale).toInt()

            var tempImage = bufferedImage.subImage(x,y,width,height)
            tempImage = blurFilter.transform(tempImage)

            val outputImage = BufferedImages.create(srcWidth, srcHeight, state.currentImage!!.type)
            val graphics2D = outputImage.createGraphics()
            graphics2D.drawImage(bufferedImage, 0, 0, null)
            graphics2D.drawImage(tempImage, x, y, width, height, null)
            graphics2D.dispose()

            state.addQueue(state.currentImage!!)
            state.currentImage = outputImage
        }
    }

    fun mosaic(width:Int, height:Int,offset: Offset,state: ApplicationState) {

        state.scope.launch(IO) {
            val bufferedImage = state.currentImage!!

            val srcWidth = bufferedImage.width
            val srcHeight = bufferedImage.height

            val xScale = (srcWidth.toFloat()/width)
            val yScale = (srcHeight.toFloat()/height)

            val size = rxCache.get<GeneralSettings>(KEY_GENERAL_SETTINGS)?.data?.size?:100

            // 创建与输入图像相同大小的新图像
            val outputImage = BufferedImages.create(srcWidth, srcHeight, state.currentImage!!.type)
            // 创建画笔
            val graphics: Graphics = outputImage.graphics
            // 将原始图像绘制到新图像中
            graphics.drawImage(bufferedImage, 0, 0, null)
            // 打码区域左上角x坐标
            val x = (offset.x*xScale).toInt()
            // 打码区域左上角y坐标
            val y = (offset.y*yScale).toInt()
            // 打码区域宽度
            val width = (size*xScale).toInt()
            // 打码区域高度
            val height = (size*yScale).toInt()

            val mosaicSize = 40
            var xcount = 0 // 方向绘制个数
            var ycount = 0 // y方向绘制个数
            xcount = if (width % mosaicSize === 0) {
                width / mosaicSize
            } else {
                width / mosaicSize + 1
            }

            ycount = if (height % mosaicSize === 0) {
                height / mosaicSize
            } else {
                height / mosaicSize + 1
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
                    yTmp += mosaicSize // 计算下一个矩形的y坐标
                }
                yTmp = y // 还原y坐标
                xTmp += mosaicSize // 计算x坐标
            }
            // 释放资源
            graphics.dispose()

            state.addQueue(state.currentImage!!)
            state.currentImage = outputImage
        }
    }

    fun flip(state: ApplicationState) {

        state.currentImage?.let {
            state.addQueue(it)
            state.currentImage = it.flipHorizontally()
        }
    }

    fun rotate(state: ApplicationState) {

        state.currentImage?.let {
            state.addQueue(it)
            state.currentImage = it.rotate(-90.0)
        }
    }

    fun resize(width:Int, height:Int, state: ApplicationState) {

        state.currentImage?.let {
            if (width == it.width && height == it.height) {
                return@let
            }

            val resizedImage = it.resize(width, height)
            state.addQueue(it)
            state.currentImage = resizedImage
        }
    }

    fun shearing(x:Float, y:Float, state: ApplicationState) {

        state.currentImage?.let {
            if (x == 0f && y == 0f) {
                return@let
            }

            state.scope.launchWithLoading {

                OpenCVManager.invokeCV(state, action = { byteArray ->
                    ImageProcess.shearing(byteArray, x, y)
                }, failure = { e ->
                    logger.error("shearing is failed", e)
                })
            }
        }
    }

    fun saveImage(state: ApplicationState) {

        state.currentImage?.let {
            exportImage { chooser ->
                val selectedFile = chooser.selectedFile
                val selectedFilter = chooser.fileFilter as FileNameExtensionFilter
                val format = selectedFilter.extensions[0] // "png" or "jpg"

                val outputFile = if (selectedFile.name.lowercase().endsWith(".${format}")) {
                    selectedFile
                } else {
                    File(selectedFile.parent, "${selectedFile.name}.${format}")
                }

                val b = when(format) {
                    "jpg" -> {
                        val finalImage = if (state.rawImageFile?.getImageFormat() != ".jpg") {
                            state.currentImage!!.convertToRGB()
                        } else state.currentImage!!

                        writeImageFile(finalImage, outputFile.absolutePath, format)
                    }

                    "png" -> {
                        writeImageFile(state.currentImage!!, outputFile.absolutePath, format)
                    }

                    "webp" -> {
                        writeImageFileAsWebP(state.currentImage!!, outputFile.absolutePath)
                    }

                    else -> {
                        writeImageFile(state.currentImage!!, outputFile.absolutePath, format)
                    }
                }

                if (b)
                    showTopToast("图像保存成功")
                else
                    showTopToast("图像保存失败")
            }
        }
    }

    fun clearImage(state: ApplicationState) {
        state.clearImage()
    }
}