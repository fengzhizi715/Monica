package cn.netdiscovery.monica.ui.controlpanel.ai

import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.loadingDisplay
import cn.netdiscovery.monica.utils.logger
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
import org.slf4j.Logger

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
        if (state.currentImage!=null) {
            state.scope.launchWithLoading {

                OpenCVManager.invokeCV(state, action = { byteArray ->
                    ImageProcess.faceDetect(byteArray)
                }, failure = { e ->
                    logger.error("faceDetect is failed", e)
                })
            }
        }
    }

    fun sketchDrawing(state: ApplicationState) {
        if (state.currentImage!=null) {
            state.scope.launchWithLoading {

                OpenCVManager.invokeCV(state, action = { byteArray ->
                    ImageProcess.sketchDrawing(byteArray)
                }, failure = { e ->
                    logger.error("sketchDrawing is failed", e)
                })
            }
        }
    }

    fun faceLandMark(state: ApplicationState) {
        if (state.currentImage!=null) {
            state.scope.launchWithLoading {

                OpenCVManager.invokeCV(state, action = { byteArray ->
                    ImageProcess.faceLandMark(byteArray)
                }, failure = { e ->
                    logger.error("faceLandMark is failed", e)
                })
            }
        }
    }
}