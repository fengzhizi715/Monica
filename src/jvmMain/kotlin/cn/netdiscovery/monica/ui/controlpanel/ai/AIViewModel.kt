package cn.netdiscovery.monica.ui.controlpanel.ai

import cn.netdiscovery.monica.http.createRequest
import cn.netdiscovery.monica.http.createRequestBody
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.ImageFormatDetector
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import cn.netdiscovery.monica.utils.logger
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.Logger
import showCenterToast

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
        if (state.currentImage == null) return

        state.scope.launchWithSuspendLoading {

            createRequest(request = {
                val format = ImageFormatDetector.getImageFormat(state.rawImageFile!!)?:"jpg"

                val requestBody: RequestBody = createRequestBody(state.currentImage!!,format)

                Request.Builder()
                    .url( "${state.algorithmUrlText}api/faceDetect")
                    .post(requestBody)
                    .build()
            }, success = {
                state.addQueue(state.currentImage!!)
                state.currentImage = it
            }, failure = {
                logger.error(it.message)
                showCenterToast("算法服务异常")
            })
        }
    }

    fun sketchDrawing(state: ApplicationState) {
        if (state.currentImage == null) return

        state.scope.launchWithSuspendLoading {

            createRequest(request = {
                val format = ImageFormatDetector.getImageFormat(state.rawImageFile!!)?:"jpg"

                val requestBody: RequestBody = createRequestBody(state.currentImage!!,format)

                Request.Builder()
                    .url( "${state.algorithmUrlText}api/sketchDrawing")
                    .post(requestBody)
                    .build()
            }, success = {
                state.addQueue(state.currentImage!!)
                state.currentImage = it
            }, failure = {
                logger.error(it.message)
                showCenterToast("算法服务异常")
            })
        }
    }
}