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
import kotlin.collections.set

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
    private val manager = EditHistoryCenter.getManager<CVParams>(MODULE_OPENCV)

    fun gaussianBlur(state: ApplicationState, ksize:Int, sigmaX: Double = 0.0, sigmaY: Double = 0.0) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "gaussianBlur", description = "实现高斯滤波") {
                    this.parameters["ksize"] = ksize
                    this.parameters["sigmaX"] = sigmaX
                    this.parameters["sigmaY"] = sigmaY
                }

                ImageProcess.gaussianBlur(byteArray, ksize, sigmaX, sigmaY)
            }, failure = { e ->
                logger.error("gaussianBlur is failed", e)
            })
        }
    }

    fun medianBlur(state: ApplicationState, ksize:Int) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "medianBlur", description = "实现中值滤波") {
                    this.parameters["ksize"] = ksize
                }

                ImageProcess.medianBlur(byteArray, ksize)
            }, failure = { e ->
                logger.error("medianBlur is failed", e)
            })
        }
    }

    fun bilateralFilter(state: ApplicationState, d:Int, sigmaColor:Double, sigmaSpace:Double) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "bilateralFilter", description = "实现高斯双边滤波") {
                    this.parameters["d"] = d
                    this.parameters["sigmaColor"] = sigmaColor
                    this.parameters["sigmaSpace"] = sigmaSpace
                }

                ImageProcess.bilateralFilter(byteArray, d, sigmaColor, sigmaSpace)
            }, failure = { e ->
                logger.error("medianBlur is failed", e)
            })
        }
    }

    fun pyrMeanShiftFiltering(state: ApplicationState, sp: Double, sr: Double) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "pyrMeanShiftFiltering", description = "实现均值迁移滤波") {
                    this.parameters["sp"] = sp
                    this.parameters["sr"] = sr
                }

                ImageProcess.pyrMeanShiftFiltering(byteArray, sp, sr)
            }, failure = { e ->
                logger.error("medianBlur is failed", e)
            })
        }
    }
}