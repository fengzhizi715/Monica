package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.ContourDisplaySettings
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.ContourFilterSettings
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MorphologicalOperationSettings

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.CVState
 * @author: Tony Shen
 * @date: 2024/10/27 18:40
 * @version: V1.0 <描述当前版本功能>
 */
object CVState {

    var isThreshType by mutableStateOf(false)

    var isThreshSegment by mutableStateOf(false)

    var isAdaptiveThresh by mutableStateOf(false)

    var isFirstDerivativeOperator by mutableStateOf(false)

    var isSecondDerivativeOperator by mutableStateOf(false)

    var isCannyOperator by mutableStateOf(false)

    var isContourPerimeter by mutableStateOf(false)

    var isContourArea by mutableStateOf(false)

    var isContourRoundness by mutableStateOf(false)

    var isContourAspectRatio by mutableStateOf(false)

    var showOriginalImage by mutableStateOf(false)

    var showBoundingRect by mutableStateOf(false)

    var showMinAreaRect by mutableStateOf(false)

    var showCenter by mutableStateOf(false)

    fun clearAllStatus() {
        isThreshType = false
        isThreshSegment = false
        isAdaptiveThresh = false
        isFirstDerivativeOperator = false
        isSecondDerivativeOperator = false
        isCannyOperator = false
        isContourPerimeter = false
        isContourArea = false
        isContourRoundness = false
        isContourAspectRatio = false
        showOriginalImage = false
        showBoundingRect = false
        showMinAreaRect = false
        showCenter = false

        contourFilterSettings          = ContourFilterSettings()
        contourDisplaySettings         = ContourDisplaySettings()
        morphologicalOperationSettings = MorphologicalOperationSettings()
    }
}