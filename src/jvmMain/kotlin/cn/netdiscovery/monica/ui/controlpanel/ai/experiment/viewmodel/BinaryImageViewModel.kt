package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.BinaryImageAnalysisViewModel
 * @author: Tony Shen
 * @date: 2024/10/7 16:07
 * @version: V1.0 <描述当前版本功能>
 */
class BinaryImageViewModel {
    private val logger: Logger = logger<BinaryImageViewModel>()

    fun cvtGray(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.cvtGray(byteArray)
            }, failure = { e ->
                logger.error("cvtGray is failed", e)
            })
        }
    }

    fun threshold(state: ApplicationState, typeSelected: String, thresholdSelected: String) {

        val thresholdType1 = when(typeSelected) {
            "THRESH_BINARY" -> 0
            "THRESH_BINARY_INV" -> 1
            else -> 0
        }

        val thresholdType2 = when(thresholdSelected) {
            "THRESH_OTSU" -> 8
            "THRESH_TRIANGLE" -> 16
            else -> 8
        }

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_BINARY, action = { byteArray ->
                ImageProcess.threshold(byteArray, thresholdType1, thresholdType2)
            }, failure = { e ->
                logger.error("threshold is failed", e)
            })
        }
    }

    fun adaptiveThreshold(state: ApplicationState, adaptiveMethodSelected: String, typeSelected: String, blockSize:Int, c:Int) {

        val adaptiveMethod = when(adaptiveMethodSelected) {
            "ADAPTIVE_THRESH_MEAN_C" -> 0
            "ADAPTIVE_THRESH_GAUSSIAN_C" -> 1
            else -> 0
        }

        val thresholdType = when(typeSelected) {
            "THRESH_BINARY" -> 0
            "THRESH_BINARY_INV" -> 1
            else -> 0
        }

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_BINARY, action = { byteArray ->
                ImageProcess.adaptiveThreshold(byteArray, adaptiveMethod, thresholdType, blockSize, c)
            }, failure = { e ->
                logger.error("adaptiveThreshold is failed", e)
            })
        }
    }

    fun inRange(state: ApplicationState, hmin:Int, smin:Int, vmin:Int, hmax:Int, smax:Int, vmax:Int) {
        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_BINARY, action = { byteArray ->
                ImageProcess.inRange(byteArray, hmin, smin, vmin, hmax, smax, vmax)
            }, failure = { e ->
                logger.error("inRange is failed", e)
            })
        }
    }
}