package cn.netdiscovery.monica.ui.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.filter.blur.FastBlur2D
import cn.netdiscovery.monica.imageprocess.saveImage
import cn.netdiscovery.monica.imageprocess.subImage
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.filter.FilterViewModel
import cn.netdiscovery.monica.ui.controlpanel.filter.selectedIndex
import cn.netdiscovery.monica.utils.*
import com.safframework.kotlin.coroutines.IO
import filterNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import java.awt.Color
import java.awt.Graphics
import java.io.File
import java.util.*
import javax.swing.JFileChooser

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

    var saturation by mutableStateOf(0f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )

    fun chooseImage(state: ApplicationState) {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                state.scope.launch(IO) {
                    clickLoadingDisplay {
                        val file = it.getOrNull(0)
                        if (file != null) {
                            state.rawImage = BufferedImages.load(file)
                            state.currentImage = state.rawImage
                            state.rawImageFile = file
                        }
                    }
                }
            }
        )
    }

    fun loadUrl(picUrl:String, state: ApplicationState) {
        state.scope.launch(IO) {
            clickLoadingDisplay {
                BufferedImages.loadUrl(picUrl)?.let {
                    state.rawImage = it
                    state.currentImage = state.rawImage
                }
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
            val width = (60*xScale).toInt()
            // 打码区域高度
            val height = (60*yScale).toInt()

            val mosaicSize = 40
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

    fun previewImage(state: ApplicationState) {
        state.scope.launch {
            clickLoadingDisplayWithSuspend {
                if (state.currentImage == null)
                    return@clickLoadingDisplayWithSuspend

                if (!state.isHLS && (!state.isFilter || (state.isFilter && selectedIndex.value == 0)))  {
                    return@clickLoadingDisplayWithSuspend
                }

                val tempImage = state.currentImage!!

                if (state.isHLS) {
                    state.currentImage = hsl(state.currentImage!!, saturation, hue, luminance)
                }

                if(state.isFilter) {
                    val filterName = filterNames[selectedIndex.value]

                    val params = getFilterParam(filterName) // 从缓存中获取滤镜的参数信息

                    if (params!=null) {
                        // 按照参数名首字母进行排序
                        Collections.sort(params) { o1, o2 -> collator.compare(o1.first, o2.first) }
                        logger.info("sort params: $params")
                    }

                    val array = mutableListOf<Any>()

                    params?.forEach {
                        array.add(it.third)
                    }

                    logger.info("filterName: $filterName, array: $array")

                    state.currentImage = doFilter(filterName,array,state)
                }

                state.addQueue(tempImage)
            }
        }
    }

    fun saveImage(state: ApplicationState) {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.DIRECTORIES_ONLY,
            selectionFileFilter = null
        ) {
            state.scope.launch(IO) {
                val outputPath = it[0].absolutePath
                val saveFile = File(outputPath).getUniqueFile(state.rawImageFile?: File("${currentTime()}.png"))
                state.currentImage!!.saveImage(saveFile)
                state.showTray(msg = "保存成功（${outputPath}）")
            }
        }
    }

    fun clearImage(state: ApplicationState) {
        state.clearImage()
    }
}