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
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ImageEnhanceViewModel
 * @author: Tony Shen
 * @date: 2024/7/17 21:33
 * @version: V1.0 <描述当前版本功能>
 */
class ImageEnhanceViewModel {
    private val logger: Logger = logger<ImageEnhanceViewModel>()
    private val manager = EditHistoryCenter.getManager<CVParams>(MODULE_OPENCV)

    fun equalizeHist(state: ApplicationState) {
        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "equalizeHist", description = "直方图均衡化") {}

                ImageProcess.equalizeHist(byteArray)
            }, failure = { e ->
                logger.error("equalizeHist is failed", e)
            })
        }
    }

    fun clahe(state: ApplicationState, clipLimit:Double, size:Int) {
        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "clahe", description = "限制对比度自适应直方图均衡") {
                    this.parameters["clipLimit"] = clipLimit
                    this.parameters["size"] = size
                }

                ImageProcess.clahe(byteArray, clipLimit, size)
            }, failure = { e ->
                logger.error("clahe is failed", e)
            })
        }
    }

    fun gammaCorrection(state: ApplicationState, gamma:Float) {
        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "gammaCorrection", description = "gamma 校正") {
                    this.parameters["gamma"] = gamma
                }

                ImageProcess.gammaCorrection(byteArray, gamma)
            }, failure = { e ->
                logger.error("gammaCorrection is failed", e)
            })
        }
    }

    fun laplaceSharpening(state: ApplicationState) {
        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "laplaceSharpening", description = "laplace 锐化") {}

                ImageProcess.laplaceSharpening(byteArray)
            }, failure = { e ->
                logger.error("laplace is failed", e)
            })
        }
    }

    fun unsharpMask(state: ApplicationState,radius:Int,threshold:Int,amount:Int) {
        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "unsharpMask", description = "USM 锐化") {
                    this.parameters["radius"] = radius
                    this.parameters["threshold"] = threshold
                    this.parameters["amount"] = amount
                }

                ImageProcess.unsharpMask(byteArray,radius,threshold,amount)
            }, failure = { e ->
                logger.error("unsharpMask is failed", e)
            })
        }
    }

    fun ace(state: ApplicationState, ratio:Int, radius:Int) {
        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->

                manager.recordCVOperation(operation = "ace", description = "自动色彩均衡") {
                    this.parameters["ratio"] = ratio
                    this.parameters["radius"] = radius
                }

                ImageProcess.ace(byteArray,ratio,radius)
            }, failure = { e ->
                logger.error("ace is failed", e)
            })
        }
    }
}