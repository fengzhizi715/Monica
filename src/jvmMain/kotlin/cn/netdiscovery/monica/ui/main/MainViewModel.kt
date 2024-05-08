package cn.netdiscovery.monica.ui.main

import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import cn.netdiscovery.monica.utils.dropFileTarget
import cn.netdiscovery.monica.utils.legalSuffixList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.main.MainViewModel
 * @author: Tony Shen
 * @date: 2024/5/8 10:39
 * @version: V1.0 <描述当前版本功能>
 */
class MainViewModel {

    fun dropFile(state: ApplicationState) {
        state.window.contentPane.dropTarget = dropFileTarget {
            state.scope.launch(Dispatchers.IO) {
                clickLoadingDisplay {
                    val filePath = it.getOrNull(0)
                    if (filePath != null) {
                        val file = File(filePath)
                        if (file.isFile && file.extension in legalSuffixList) {
                            state.rawImage = ImageIO.read(file)
                            state.currentImage = state.rawImage
                            state.rawImageFile = file
                        }
                    }
                }
            }
        }
    }
}