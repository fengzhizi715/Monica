package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.ControlContent
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import cn.netdiscovery.monica.ui.preview.preview
import cn.netdiscovery.monica.utils.dropFileTarget
import cn.netdiscovery.monica.utils.legalSuffixList
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.MainView
 * @author: Tony Shen
 * @date: 2024/4/26 10:54
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun mainView(
    state: ApplicationState
) {
    val viewModel: MainViewModel = koinInject()

    viewModel.dropFile(state)

    MaterialTheme {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            preview(state, Modifier.weight(1.4f))
            ControlContent(state, Modifier.weight(0.6f))
        }
    }
}