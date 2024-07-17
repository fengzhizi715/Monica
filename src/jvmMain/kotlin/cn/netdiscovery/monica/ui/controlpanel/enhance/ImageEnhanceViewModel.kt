package cn.netdiscovery.monica.ui.controlpanel.enhance

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import cn.netdiscovery.monica.utils.logger
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.enhance.ImageEnhanceViewModel
 * @author: Tony Shen
 * @date: 2024/7/17 21:33
 * @version: V1.0 <描述当前版本功能>
 */
class ImageEnhanceViewModel {
    private val logger: Logger = logger<ImageEnhanceViewModel>()

    fun equalizeHist(state: ApplicationState) {
        val width = state.currentImage!!.width
        val height = state.currentImage!!.height
        val byteArray = state.currentImage!!.image2ByteArray()

        state.scope.launch(IO) {
            clickLoadingDisplay {
                val outPixels = ImageProcess.equalizeHist(byteArray)
                state.addQueue(state.currentImage!!)
                state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
            }
        }
    }
}