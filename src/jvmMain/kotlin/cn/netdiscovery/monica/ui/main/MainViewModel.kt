package cn.netdiscovery.monica.ui.main

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import cn.netdiscovery.monica.utils.dropFileTarget
import cn.netdiscovery.monica.utils.legalSuffixList
import cn.netdiscovery.monica.utils.logger
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
import org.slf4j.Logger
import java.io.File

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.main.MainViewModel
 * @author: Tony Shen
 * @date: 2024/5/24 11:03
 * @version: V1.0 <描述当前版本功能>
 */
class MainViewModel {
    private val logger: Logger = logger<MainViewModel>()

    fun dropFile(state: ApplicationState) {
        state.window.contentPane.dropTarget = dropFileTarget {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    val filePath = it.getOrNull(0)
                    if (filePath != null) {
                        val file = File(filePath)
                        if (file.isFile && file.extension in legalSuffixList) {
                            logger.info("load file: ${file.absolutePath}")
                            state.rawImage = BufferedImages.load(file)
                            state.currentImage = state.rawImage
                            state.rawImageFile = file
                        }
                    }
                }
            }
        }
    }
}
