package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ImageDenoisingViewModel
 * @author: Tony Shen
 * @date: 2024/12/4 15:44
 * @version: V1.0 <描述当前版本功能>
 */
class ImageDenoisingViewModel {
    private val logger: Logger = logger<ImageDenoisingViewModel>()

    fun gaussianBlur(state: ApplicationState, ksize:Int, sigmaX: Double = 0.0, sigmaY: Double = 0.0) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->
                ImageProcess.gaussianBlur(byteArray, ksize, sigmaX, sigmaY)
            }, failure = { e ->
                logger.error("gaussianBlur is failed", e)
            })
        }
    }

    fun medianBlur(state: ApplicationState, ksize:Int) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->
                ImageProcess.medianBlur(byteArray, ksize)
            }, failure = { e ->
                logger.error("medianBlur is failed", e)
            })
        }
    }

    fun bilateralFilter(state: ApplicationState, d:Int, sigmaColor:Double, sigmaSpace:Double) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->
                ImageProcess.bilateralFilter(byteArray, d, sigmaColor, sigmaSpace)
            }, failure = { e ->
                logger.error("medianBlur is failed", e)
            })
        }
    }

    fun pyrMeanShiftFiltering(state: ApplicationState, sp: Double, sr: Double) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->
                ImageProcess.pyrMeanShiftFiltering(byteArray, sp, sr)
            }, failure = { e ->
                logger.error("medianBlur is failed", e)
            })
        }
    }
}