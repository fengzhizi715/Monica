package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import cn.netdiscovery.monica.imageprocess.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.ContourDisplaySettings
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

    fun findContours(state: ApplicationState, contourDisplaySettings: ContourDisplaySettings) {

        val type = if (contourDisplaySettings.showOriginalImage) { BufferedImage.TYPE_INT_ARGB } else BufferedImage.TYPE_BYTE_BINARY

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = type, action = { byteArray ->
                val srcByteArray = state.rawImage!!.image2ByteArray()

                ImageProcess.findContours(srcByteArray, byteArray, contourDisplaySettings)
            }, failure = { e ->
                logger.error("findContours is failed", e)
            })
        }
    }
}