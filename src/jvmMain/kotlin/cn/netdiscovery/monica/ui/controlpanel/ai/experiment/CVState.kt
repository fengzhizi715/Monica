package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.domain.ContourDisplaySettings
import cn.netdiscovery.monica.domain.ContourFilterSettings
import cn.netdiscovery.monica.domain.MatchTemplateSettings
import cn.netdiscovery.monica.domain.MorphologicalOperationSettings
import java.awt.image.BufferedImage

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

    var templateImage: BufferedImage? by mutableStateOf(null)

    /**
     * 清空状态
     */
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
        templateImage = null

        contourFilterSettings          = ContourFilterSettings()
        contourDisplaySettings         = ContourDisplaySettings()
        morphologicalOperationSettings = MorphologicalOperationSettings()
        matchTemplateSettings          = MatchTemplateSettings()
    }
}