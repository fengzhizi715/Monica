package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

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
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ContourAnalysisViewModel
 * @author: Tony Shen
 * @date: 2024/10/26 13:54
 * @version: V1.0 <描述当前版本功能>
 */
class ContourAnalysisViewModel {
    private val logger: Logger = logger<ContourAnalysisViewModel>()

    fun findContours(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_INT_ARGB, action = { byteArray ->
                val (width,height,srcByteArray) = state.rawImage!!.getImageInfo()

                ImageProcess.findContours(srcByteArray, byteArray)
            }, failure = { e ->
                logger.error("findContours is failed", e)
            })
        }
    }
}