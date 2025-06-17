package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.imageprocess.utils.extension.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.Action
import cn.netdiscovery.monica.utils.CVSuccess
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.utils.GsonUtils
import org.slf4j.Logger
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean

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

    private var cppObjectPtr:Long = 0

    private var init:AtomicBoolean = AtomicBoolean(false)

    /**
     * 封装图像调色的方法
     * @param state   当前应用的 state
     * @param image   需要调色的图像
     * @param colorCorrectionSettings 图像调色所需要的参数
     * @param success 成功后的回调
     */
    fun colorCorrection(state: ApplicationState,
                        image: BufferedImage,
                        colorCorrectionSettings: ColorCorrectionSettings,
                        success: CVSuccess) {

        logger.info("colorCorrectionSettings = ${GsonUtils.toJson(colorCorrectionSettings)}")

        state.scope.launchWithLoading {
            if (!init.get()) {
                init.set(true)

                val byteArray = image.image2ByteArray()
                cppObjectPtr = ImageProcess.initColorCorrection(byteArray)
            }

            OpenCVManager.invokeCV(image,
                action  = { byteArray -> ImageProcess.colorCorrection(byteArray, colorCorrectionSettings, cppObjectPtr) },
                success = { success.invoke(it) },
                failure = { e ->
                    logger.error("colorCorrection is failed", e)
                })
        }
    }

    /**
     * 保存图像
     */
    fun save(action: Action) {
        action.invoke()
    }

    fun clearAllStatus() {
        init.set(false)

        contrast = 255f
        hue = 180f
        saturation = 255f
        lightness = 255f
        temperature = 255f
        highlight = 255f
        shadow = 255f
        sharpen = 0f
        corner = 0f

        colorCorrectionSettings = ColorCorrectionSettings()

        ImageProcess.deleteColorCorrection(cppObjectPtr)
        cppObjectPtr = 0
    }
}