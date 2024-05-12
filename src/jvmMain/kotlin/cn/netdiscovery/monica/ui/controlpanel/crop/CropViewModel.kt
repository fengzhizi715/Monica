package cn.netdiscovery.monica.ui.controlpanel.crop

import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.flipHorizontally
import cn.netdiscovery.monica.imageprocess.rotate
import cn.netdiscovery.monica.state.ApplicationState
import java.awt.Image

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

            val tmp = state.currentImage!!.getScaledInstance(width, height, Image.SCALE_SMOOTH)
            val resizedImage = BufferedImages.create(width, height,state.currentImage!!.type)
            val g2d = resizedImage.createGraphics()
            try {
                g2d.drawImage(tmp, 0, 0, null)
            } finally {
                g2d.dispose()
            }
            state.addQueue(state.currentImage!!)
            state.currentImage = resizedImage
        }
    }

    fun crop(width:Int, height:Int, offset: Offset, state: ApplicationState) {

        if (this.offset == Offset.Zero) {
            this.offset = offset
            return
        }

        if (this.offset!=offset) {
            this.offset = offset
        }


    }
}