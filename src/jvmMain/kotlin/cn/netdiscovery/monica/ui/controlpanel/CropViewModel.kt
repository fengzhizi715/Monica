package cn.netdiscovery.monica.ui.controlpanel

import cn.netdiscovery.monica.imageprocess.flipHorizontally
import cn.netdiscovery.monica.imageprocess.rotate
import cn.netdiscovery.monica.state.ApplicationState
import java.awt.Image
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.CropViewModel
 * @author: Tony Shen
 * @date: 2024/5/8 11:46
 * @version: V1.0 <描述当前版本功能>
 */
class CropViewModel {

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
            val resizedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val g2d = resizedImage.createGraphics()
            try {
                g2d.drawImage(tmp, 0, 0, null)
            } finally {
                g2d.dispose()
            }
            state.currentImage = resizedImage
        }
    }
}