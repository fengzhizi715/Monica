package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.controlPanel
import cn.netdiscovery.monica.ui.preview.preview
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.utils.*
import org.koin.compose.koinInject
import picUrl
import showTopToast

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
fun openURLDialog(onConfirm: Action, onDismiss: Action) {
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

@Composable
fun showVersionInfo(onClick: Action) {
    AlertDialog(onDismissRequest = {},
        title = {
            Text("Monica 软件信息")
        },
        text = {
            Column {
                val versionInfo = if (isProVersion) "Pro 版本" else "普通版本"
                Text("Monica 版本: $appVersion, $versionInfo, 编译时间: $buildTime")
                Text("OS: $os, $osVersion, $arch")
                Text("JDK: $javaVersion, $javaVendor")
                Text("Kotlin: $kotlinVersion, Compose Desktop: $composeVersion")
                Text("OpenCV 版本: $openCVVersion, ONNXRuntime 版本: $onnxRuntimeVersion")
                Text("本地算法库: $imageProcessVersion")
                Text("版权信息: Copyright 2024-Present，Tony Shen")
                Text("Wechat: fengzhizi715")
                Text("Github 地址: https://github.com/fengzhizi715/Monica")
            }
        },
        confirmButton = {
            Button(onClick = {
                onClick.invoke()
            }) {
                Text("关闭")
            }
        })
}

@Composable
fun generalSettings(state: ApplicationState, onClick: Action) {

    var rText by remember { mutableStateOf(state.rText.toString()) }
    var gText by remember { mutableStateOf(state.gText.toString()) }
    var bText by remember { mutableStateOf(state.bText.toString()) }

    AlertDialog(onDismissRequest = {},
        title = {
            Text("Monica 通用设置")
        },
        text = {
            Column {

                Row {
                    Text("对象输出框颜色:")

                    basicTextFieldWithTitle(textModifier = Modifier.padding(start = 20.dp), titleText = "R", value = rText, width = 80.dp) { str ->
                        rText = str
                    }

                    basicTextFieldWithTitle(titleText = "G", value = gText, width = 80.dp) { str ->
                        gText = str
                    }

                    basicTextFieldWithTitle(titleText = "B", value = bText, width = 80.dp) { str ->
                        bText = str
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                state.rText = getValidateField(block = { rText.toInt() } , failed = { showTopToast("R 需要 int 类型") }) ?: return@Button
                state.gText = getValidateField(block = { gText.toInt() } , failed = { showTopToast("G 需要 int 类型") }) ?: return@Button
                state.bText = getValidateField(block = { bText.toInt() } , failed = { showTopToast("B 需要 int 类型") }) ?: return@Button

                state.saveColor()

                onClick()
            }) {
                Text("更新")
            }

            Button(onClick = {
                onClick()
            }) {
                Text("关闭")
            }
        })
}