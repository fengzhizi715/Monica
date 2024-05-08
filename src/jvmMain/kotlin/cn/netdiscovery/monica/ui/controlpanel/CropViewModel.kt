package cn.netdiscovery.monica.ui.controlpanel

import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.flipHorizontally
import cn.netdiscovery.monica.utils.extension.rotate

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
}