package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

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
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.EdgeDetectionViewModel
 * @author: Tony Shen
 * @date:  2024/10/13 22:23
 * @version: V1.0 <描述当前版本功能>
 */
class EdgeDetectionViewModel {
    private val logger: Logger = logger<EdgeDetectionViewModel>()

    fun roberts(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.roberts(byteArray)
            }, failure = { e ->
                logger.error("roberts is failed", e)
            })
        }
    }

    fun prewitt(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.prewitt(byteArray)
            }, failure = { e ->
                logger.error("prewitt is failed", e)
            })
        }
    }

    fun sobel(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.sobel(byteArray)
            }, failure = { e ->
                logger.error("sobel is failed", e)
            })
        }
    }

    fun laplace(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.laplace(byteArray)
            }, failure = { e ->
                logger.error("laplace is failed", e)
            })
        }
    }

    fun log(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.log(byteArray)
            }, failure = { e ->
                logger.error("log is failed", e)
            })
        }
    }

    fun dog(state: ApplicationState, sigma1:Double, sigma2: Double, size:Int) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
                ImageProcess.dog(byteArray, sigma1, sigma2, size)
            }, failure = { e ->
                logger.error("log is failed", e)
            })
        }
    }

    fun canny(state: ApplicationState, threshold1:Double, threshold2: Double, apertureSize:Int) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_BINARY, action = { byteArray ->
                ImageProcess.canny(byteArray,threshold1,threshold2,apertureSize)
            }, failure = { e ->
                logger.error("canny is failed", e)
            })
        }
    }
}