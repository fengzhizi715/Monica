package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.imageprocess.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MatchTemplateSettings
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import cn.netdiscovery.monica.utils.showFileSelector
import com.safframework.rxcache.utils.GsonUtils
import org.slf4j.Logger
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JFileChooser

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

    var templateImage: BufferedImage? by mutableStateOf(null)

    fun clearTemplateImage() {
        if (templateImage!=null) {
            templateImage = null
        }
    }


    fun matchTemplate(state: ApplicationState, matchTemplateSettings: MatchTemplateSettings) {
        logger.info("matchTemplateSettings = ${GsonUtils.toJson(matchTemplateSettings)}")

        if (templateImage != null) {
            state.scope.launchWithLoading {
                OpenCVManager.invokeCV(state, action = { byteArray ->
                    val templateByteArray = templateImage!!.image2ByteArray()
                    ImageProcess.matchTemplate(byteArray, templateByteArray, matchTemplateSettings)
                }, failure = { e ->
                    logger.error("contourAnalysis is failed", e)
                })
            }
        }
    }
}