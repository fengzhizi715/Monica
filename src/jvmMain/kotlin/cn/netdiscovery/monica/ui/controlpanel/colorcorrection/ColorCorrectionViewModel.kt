package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.imageprocess.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.model.ColorCorrectionSettings
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.utils.GsonUtils
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

    var contrast by mutableStateOf(255f )
    var hue by mutableStateOf(180f )
    var saturation by mutableStateOf(255f )
    var lightness by mutableStateOf(255f )
    var temperature by mutableStateOf(255f )
    var highlight by mutableStateOf(255f )
    var shadow by mutableStateOf(255f )
    var sharpen by mutableStateOf(0f )
    var corner by mutableStateOf(0f )

    fun colorCorrection(state: ApplicationState, colorCorrectionSettings: ColorCorrectionSettings) {

        state.scope.launchWithLoading {
            OpenCVManager.invokeCV(state, action = { byteArray ->
                ImageProcess.colorCorrection(byteArray, colorCorrectionSettings)
            }, failure = { e ->
                logger.error("colorCorrection is failed", e)
            })
        }
    }

    fun clear() {
        contrast = 255f
        hue = 180f
        saturation = 255f
        lightness = 255f
        temperature = 255f
        highlight = 255f
        shadow = 255f
        sharpen = 0f
        corner = 0f
    }
}