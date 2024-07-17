package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.controlPanel
import cn.netdiscovery.monica.ui.preview.preview
import org.koin.compose.koinInject
import picUrl

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.main.MainView
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

/**
 * 加载网络图片的对话框
 */
@Composable
fun openURLDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.width(600.dp).height(200.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(text = "加载网络图片")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = picUrl,
                    onValueChange = { picUrl = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm.invoke()
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss.invoke()
                }
            ) {
                Text("取消")
            }
        }
    )
}