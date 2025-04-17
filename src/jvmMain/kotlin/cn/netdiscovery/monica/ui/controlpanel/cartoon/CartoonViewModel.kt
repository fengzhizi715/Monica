package cn.netdiscovery.monica.ui.controlpanel.cartoon

import cn.netdiscovery.monica.http.createRequest
import cn.netdiscovery.monica.http.createRequestBody
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.CVFailure
import cn.netdiscovery.monica.utils.extensions.getImageFormat
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import cn.netdiscovery.monica.utils.logger
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.cartoon.CartoonViewModel
 * @author: Tony Shen
 * @date: 2025/4/16 18:21
 * @version: V1.0 <描述当前版本功能>
 */
class CartoonViewModel {

    private val logger: Logger = logger<CartoonViewModel>()

    fun convert2Cartoon(state: ApplicationState, type:Int, failure: CVFailure) {
        if (state.currentImage == null) return

        state.scope.launchWithSuspendLoading {

            createRequest(request = {
                val format = state.rawImageFile!!.getImageFormat()?:"jpg"

                val requestBody: RequestBody = createRequestBody(state.currentImage!!,format)

                Request.Builder()
                    .url( "${state.algorithmUrlText}api/cartoon?type=$type")
                    .post(requestBody)
                    .build()
            }, success = {
                state.addQueue(state.currentImage!!)
                state.currentImage = it
            }, failure = {
                logger.error(it.message)
                failure.invoke(it)
            })
        }
    }
}