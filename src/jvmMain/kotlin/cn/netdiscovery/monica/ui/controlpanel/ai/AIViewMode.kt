package cn.netdiscovery.monica.ui.controlpanel.ai

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.getImageInfo
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
 *          cn.netdiscovery.monica.ui.controlpanel.ai.AIViewMode
 * @author: Tony Shen
 * @date: 2024/7/28 11:21
 * @version: V1.0 <描述当前版本功能>
 */
class AIViewMode {
    private val logger: Logger = logger<AIViewMode>()

    fun faceDetect(state: ApplicationState) {
        if (state.currentImage!=null) {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    val (width,height,byteArray) = state.currentImage!!.getImageInfo()

                    try {
                        val outPixels = ImageProcess.faceDetect(byteArray)
                        state.addQueue(state.currentImage!!)
                        state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
                    } catch (e:Exception) {
                        logger.error("faceDetect is failed", e)
                    }
                }
            }
        }
    }
}