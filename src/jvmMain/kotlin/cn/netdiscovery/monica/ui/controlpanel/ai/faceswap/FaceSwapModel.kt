package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import cn.netdiscovery.monica.utils.logger
import cn.netdiscovery.monica.utils.showFileSelector
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
import org.slf4j.Logger
import java.awt.image.BufferedImage
import javax.swing.JFileChooser

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

    fun chooseImage(state: ApplicationState) {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                state.scope.launch(IO) {
                    clickLoadingDisplay {
                        val file = it.getOrNull(0)
                        if (file != null) {
                            logger.info("load file: ${file.absolutePath}")
                            targetImage = BufferedImages.load(file)
                        }
                    }
                }
            }
        )
    }
}