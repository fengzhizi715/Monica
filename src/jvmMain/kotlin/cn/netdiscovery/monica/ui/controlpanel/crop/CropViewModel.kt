package cn.netdiscovery.monica.ui.controlpanel.crop

import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.imageprocess.*
import cn.netdiscovery.monica.state.ApplicationState
import java.awt.Image
import kotlin.math.abs

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
 * @author: Tony Shen
 * @date: 2024/5/8 11:46
 * @version: V1.0 <描述当前版本功能>
 */
class CropViewModel {

    private var offset:Offset = Offset.Zero

    fun flip(state: ApplicationState) {
        if (state.currentImage!=null) {
            state.addQueue(state.currentImage!!)
            state.currentImage = state.currentImage!!.flipHorizontally()
        }
    }

    fun rotate(state: ApplicationState) {
        if (state.currentImage!=null) {
            state.addQueue(state.currentImage!!)
            state.currentImage = state.currentImage!!.rotate(-90.0)
        }
    }

    fun resize(width:Int, height:Int, state: ApplicationState) {
        if (state.currentImage!=null) {

            if (width == state.currentImage!!.width && height == state.currentImage!!.height) {
                return
            }

            val resizedImage = state.currentImage!!.resize(width, height)
            state.addQueue(state.currentImage!!)
            state.currentImage = resizedImage
        }
    }

    fun crop(width:Int, height:Int, offset: Offset, state: ApplicationState) {

        if (this.offset == Offset.Zero) {
            this.offset = offset
            return
        }

        if (this.offset != offset) {
            val bufferedImage = state.currentImage!!
            val srcWidth = bufferedImage.width
            val srcHeight = bufferedImage.height

            val xScale = (srcWidth.toFloat()/width)
            val yScale = (srcHeight.toFloat()/height)

            // 打码区域左上角x坐标
            val x = (this.offset.x*xScale).toInt()
            // 打码区域左上角y坐标
            val y = (this.offset.y*yScale).toInt()

            val w = abs(offset.x*xScale - x)
            val h = abs(offset.y*xScale - y)

            state.addQueue(state.currentImage!!)
            state.currentImage = state.currentImage!!.subImage(x,y,w.toInt(),h.toInt())
            this.offset = Offset.Zero
        }
    }
}