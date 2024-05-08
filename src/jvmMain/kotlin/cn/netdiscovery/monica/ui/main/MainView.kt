package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.controlPanel
import cn.netdiscovery.monica.ui.preview.preview
import org.koin.compose.koinInject

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
            controlPanel(state, Modifier.weight(0.6f))
        }
    }
}