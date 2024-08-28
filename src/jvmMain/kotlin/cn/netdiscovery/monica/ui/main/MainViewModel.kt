package cn.netdiscovery.monica.ui.main

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.loadingDisplay
import cn.netdiscovery.monica.utils.dropFileTarget
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.legalSuffixList
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
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

    fun dropFile(state: ApplicationState) {
        state.window.contentPane.dropTarget = dropFileTarget {
            state.scope.launchWithLoading {
                val filePath = it.getOrNull(0)
                if (filePath != null) {
                    val file = File(filePath)
                    if (file.isFile && file.extension in legalSuffixList) {
                        state.rawImage = BufferedImages.load(file)
                        state.currentImage = state.rawImage
                        state.rawImageFile = file
                    }
                }
            }
        }
    }
}