package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.config.MODULE_OPENCV
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.domain.MorphologicalOperationSettings
import cn.netdiscovery.monica.history.EditHistoryCenter
import cn.netdiscovery.monica.history.modules.opencv.CVParams
import cn.netdiscovery.monica.history.modules.opencv.recordCVOperation
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.utils.GsonUtils
import org.slf4j.Logger
import java.awt.image.BufferedImage
import kotlin.collections.set

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MorphologicalOperationsViewModel
 * @author: Tony Shen
 * @date: 2024/12/26 20:21
 * @version: V1.0 <描述当前版本功能>
 */
class MorphologicalOperationsViewModel {
    private val logger: Logger = logger<MorphologicalOperationsViewModel>()
    private val manager = EditHistoryCenter.getManager<CVParams>(MODULE_OPENCV)

    fun morphologyEx(state: ApplicationState, morphologicalOperationSettings: MorphologicalOperationSettings) {

        logger.info("morphologicalOperationSettings = ${GsonUtils.toJson(morphologicalOperationSettings)}")

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_BINARY, action = { byteArray ->

                manager.recordCVOperation(operation = "morphologyEx", description = "形态学操作") {
                    this.parameters["morphologicalOperationSettings"] = morphologicalOperationSettings
                }

                ImageProcess.morphologyEx(byteArray, morphologicalOperationSettings)
            }, failure = { e ->
                logger.error("contourAnalysis is failed", e)
            })
        }
    }
}