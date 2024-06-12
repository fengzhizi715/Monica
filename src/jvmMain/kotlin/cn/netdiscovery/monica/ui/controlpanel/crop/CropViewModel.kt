package cn.netdiscovery.monica.ui.controlpanel.crop

import cn.netdiscovery.monica.config.KEY_CROP_FIRST
import cn.netdiscovery.monica.config.KEY_CROP_SECOND
import cn.netdiscovery.monica.imageprocess.flipHorizontally
import cn.netdiscovery.monica.imageprocess.resize
import cn.netdiscovery.monica.imageprocess.rotate
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.contentScalesIndex
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropFlag1
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropFlag2
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropTypesIndex

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
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

            val resizedImage = state.currentImage!!.resize(width, height)
            state.addQueue(state.currentImage!!)
            state.currentImage = resizedImage
        }
    }

    fun clearCropImageView() {
        cropTypesIndex.value = 0
        contentScalesIndex.value = 1

        cropFlag1.set(false)
        cropFlag2.set(false)
        rxCache.remove(KEY_CROP_FIRST)
        rxCache.remove(KEY_CROP_SECOND)
    }
}