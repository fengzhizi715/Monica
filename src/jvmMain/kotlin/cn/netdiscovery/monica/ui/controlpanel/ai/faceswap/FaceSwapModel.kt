package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapModel
 * @author: Tony Shen
 * @date: 2024/8/25 14:55
 * @version: V1.0 <描述当前版本功能>
 */
class FaceSwapModel {
    private val logger: Logger = logger<FaceSwapModel>()

    var targetImage: BufferedImage? by mutableStateOf(null)

}