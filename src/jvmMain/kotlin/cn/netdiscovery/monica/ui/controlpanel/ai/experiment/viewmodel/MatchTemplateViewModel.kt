package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.config.MODULE_OPENCV
import cn.netdiscovery.monica.domain.MatchTemplateSettings
import cn.netdiscovery.monica.history.EditHistoryCenter
import cn.netdiscovery.monica.history.modules.opencv.CVParams
import cn.netdiscovery.monica.history.modules.opencv.recordCVOperation
import cn.netdiscovery.monica.imageprocess.utils.extension.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.CVState
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.utils.GsonUtils
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MatchTemplateViewModel
 * @author: Tony Shen
 * @date: 2025/1/1 15:32
 * @version: V1.0 <描述当前版本功能>
 */
class MatchTemplateViewModel {

    private val logger: Logger = logger<MatchTemplateViewModel>()
    private val manager = EditHistoryCenter.getManager<CVParams>(MODULE_OPENCV)

    fun clearTemplateImage() {
        if (CVState.templateImage!=null) {
            CVState.templateImage = null
        }
    }

    fun matchTemplate(state: ApplicationState, matchTemplateSettings: MatchTemplateSettings) {
        logger.info("matchTemplateSettings = ${GsonUtils.toJson(matchTemplateSettings)}")

        if (CVState.templateImage != null) {
            state.scope.launchWithLoading {
                OpenCVManager.invokeCV(state, action = { byteArray ->
                    val templateByteArray = CVState.templateImage!!.image2ByteArray()
                    val scalar = state.toOutputBoxScalar()

                    manager.recordCVOperation(operation = "matchTemplate", description = "模版匹配") {
                        this.parameters["matchTemplateSettings"] = matchTemplateSettings
                    }

                    ImageProcess.matchTemplate(byteArray, templateByteArray, scalar, matchTemplateSettings)
                }, failure = { e ->
                    logger.error("contourAnalysis is failed", e)
                })
            }
        }
    }
}