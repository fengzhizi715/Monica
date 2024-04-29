package cn.netdiscovery.monica.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.dropFileTarget
import cn.netdiscovery.monica.utils.legalSuffixList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import loadingDisplay
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
            loadingDisplay{
                val filePath = it.getOrNull(0)
                if (filePath != null) {
                    val file = File(filePath)
                    if (file.isFile && file.extension in legalSuffixList) {
                        state.rawImg = ImageIO.read(file)
                        state.showImg = state.rawImg
                        state.rawImgFile = file
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

fun loadingDisplay(block:()->Unit) {
    loadingDisplay = true
    block.invoke()
    loadingDisplay = false
}

suspend fun loadingDisplayWithSuspend(block:suspend ()->Unit) {
    loadingDisplay = true
    block.invoke()
    loadingDisplay = false
}