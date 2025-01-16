package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import cn.netdiscovery.monica.utils.showFileSelector
import org.slf4j.Logger
import java.io.File
import javax.swing.JFileChooser

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ExperimentViewModel
 * @author: Tony Shen
 * @date:  2024/10/2 15:24
 * @version: V1.0 <描述当前版本功能>
 */
class ExperimentViewModel {

    private val logger: Logger = logger<ExperimentViewModel>()

    fun chooseImage(state: ApplicationState, block:(file: File)->Unit) {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                state.scope.launchWithLoading {
                    val file = it.getOrNull(0)
                    if (file != null) {
                        logger.info("load file: ${file.absolutePath}")
                        block.invoke(file)
                    }
                }
            }
        )
    }

    fun getLastImage(state: ApplicationState) {
        state.getLastImage()?.let {
            state.currentImage = it
        }
    }
}