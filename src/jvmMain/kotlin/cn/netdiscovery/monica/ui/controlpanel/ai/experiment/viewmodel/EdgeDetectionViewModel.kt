package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.config.MODULE_OPENCV
import cn.netdiscovery.monica.history.EditHistoryCenter
import cn.netdiscovery.monica.history.modules.opencv.CVParams
import cn.netdiscovery.monica.history.modules.opencv.recordCVOperation
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger
import java.awt.image.BufferedImage
import kotlin.collections.set

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
    private val manager = EditHistoryCenter.getManager<CVParams>(MODULE_OPENCV)

    fun roberts(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->

                manager.recordCVOperation(operation = "roberts", description = "实现 roberts 算子") {}

                ImageProcess.roberts(byteArray)
            }, failure = { e ->
                logger.error("roberts is failed", e)
            })
        }
    }

    fun prewitt(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->

                manager.recordCVOperation(operation = "prewitt", description = "实现 prewitt 算子") {}

                ImageProcess.prewitt(byteArray)
            }, failure = { e ->
                logger.error("prewitt is failed", e)
            })
        }
    }

    fun sobel(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->

                manager.recordCVOperation(operation = "sobel", description = "实现 sobel 算子") {}

                ImageProcess.sobel(byteArray)
            }, failure = { e ->
                logger.error("sobel is failed", e)
            })
        }
    }

    fun laplace(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->

                manager.recordCVOperation(operation = "laplace", description = "实现 laplace 算子") {}

                ImageProcess.laplace(byteArray)
            }, failure = { e ->
                logger.error("laplace is failed", e)
            })
        }
    }

    fun log(state: ApplicationState) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->

                manager.recordCVOperation(operation = "log", description = "实现 LoG 算子") {}

                ImageProcess.log(byteArray)
            }, failure = { e ->
                logger.error("log is failed", e)
            })
        }
    }

    fun dog(state: ApplicationState, sigma1:Double, sigma2: Double, size:Int) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->

                manager.recordCVOperation(operation = "dog", description = "实现 DoG 算子") {
                    this.parameters["sigma1"] = sigma1
                    this.parameters["sigma2"] = sigma2
                    this.parameters["size"] = size
                }

                ImageProcess.dog(byteArray, sigma1, sigma2, size)
            }, failure = { e ->
                logger.error("log is failed", e)
            })
        }
    }

    fun canny(state: ApplicationState, threshold1:Double, threshold2: Double, apertureSize:Int) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_BINARY, action = { byteArray ->

                manager.recordCVOperation(operation = "canny", description = "实现 canny 算子") {
                    this.parameters["threshold1"] = threshold1
                    this.parameters["threshold2"] = threshold2
                    this.parameters["apertureSize"] = apertureSize
                }

                ImageProcess.canny(byteArray,threshold1,threshold2,apertureSize)
            }, failure = { e ->
                logger.error("canny is failed", e)
            })
        }
    }
}