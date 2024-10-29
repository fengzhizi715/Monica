package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

//    var rawImage: BufferedImage? by mutableStateOf(null)
}