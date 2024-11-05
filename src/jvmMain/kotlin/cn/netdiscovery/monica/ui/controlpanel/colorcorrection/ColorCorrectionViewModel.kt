package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ColorCorrectionViewModel
 * @author: Tony Shen
 * @date: 2024/11/5 15:17
 * @version: V1.0 <描述当前版本功能>
 */
class ColorCorrectionViewModel {

    private val logger: Logger = logger<ColorCorrectionViewModel>()

    var saturation by mutableStateOf(0f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )

}