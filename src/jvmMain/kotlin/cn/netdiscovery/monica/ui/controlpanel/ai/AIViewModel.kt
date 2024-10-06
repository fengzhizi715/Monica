package cn.netdiscovery.monica.ui.controlpanel.ai

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.getImageInfo
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.AIViewModel
 * @author: Tony Shen
 * @date: 2024/7/28 11:21
 * @version: V1.0 <描述当前版本功能>
 */
class AIViewModel {
    private val logger: Logger = logger<AIViewModel>()

    fun faceDetect(state: ApplicationState) {
        state.scope.launchWithLoading {

            OpenCVManager.invokeCV(state, action = { byteArray ->
                ImageProcess.faceDetect(byteArray)
            }, failure = { e ->
                logger.error("faceDetect is failed", e)
            })
        }
    }

    fun sketchDrawing(state: ApplicationState) {
        state.scope.launchWithLoading {

            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.sketchDrawing(byteArray)
            }, failure = { e ->
                logger.error("sketchDrawing is failed", e)
            })
        }
    }
}