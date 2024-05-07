package cn.netdiscovery.monica.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.preview.PreviewContent
import cn.netdiscovery.monica.utils.dropFileTarget
import cn.netdiscovery.monica.utils.legalSuffixList
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.MainScreen
 * @author: Tony Shen
 * @date: 2024/4/26 10:54
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun MainScreen(
    state: ApplicationState
) {
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

    MaterialTheme {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PreviewContent(state, Modifier.weight(1.4f))
            ControlContent(state, Modifier.weight(0.6f))
        }
    }
}