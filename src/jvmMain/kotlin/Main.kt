import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.http.HttpConnectionClient
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.saveFilterParams
import cn.netdiscovery.monica.state.rememberApplicationState
import cn.netdiscovery.monica.ui.*
import cn.netdiscovery.monica.utils.currentTime
import cn.netdiscovery.monica.utils.extension.saveImage
import cn.netdiscovery.monica.utils.getUniqueFile
import cn.netdiscovery.monica.utils.showFileSelector
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JFileChooser

val filterNames = mutableListOf("选择滤镜")

val flag = AtomicBoolean(false)

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)

lateinit var client: HttpConnectionClient

fun main() = application {

    initData()

    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )

    Tray(
        state = trayState,
        icon = painterResource("images/launcher.ico"),
    )

    Window(onCloseRequest = ::exitApplication,
        title = "Monica 图片编辑器 $appVersion",
        state = rememberWindowState(width = width, height = height).apply {
            position = WindowPosition(Alignment.BottomCenter)
        }) {
        MenuBar{
            Menu(text = "文件", mnemonic = 'O') {
                Item(
                    text = "打开本地图片",
                    onClick = {
                        applicationState.onClickImageChoose()
                    },
                )
                Item(
                    text = "加载网络图片",
                    onClick = {
                        openURLDialog = true
                    },
                )
                Item(
                    text = "保存图像",
                    onClick = {
                        showFileSelector(
                            isMultiSelection = false,
                            selectionMode = JFileChooser.DIRECTORIES_ONLY,
                            selectionFileFilter = null
                        ) {
                            applicationState.scope.launch {
                                val outputPath = it[0].absolutePath
                                val saveFile = File(outputPath).getUniqueFile(applicationState.rawImageFile?:File("${currentTime()}.jpg"))
                                applicationState.currentImage!!.saveImage(saveFile, 0.8f)
                                applicationState.showTray(msg = "保存成功（${outputPath}）")
                            }
                        }
                    },
                )
            }
        }
        applicationState.window = window

        MainScreen(applicationState)

        if (loadingDisplay) {
            ThreeBallLoading(Modifier.width(loadingWidth).height(height))
        }

        var picUrl by remember { mutableStateOf("") }

        if (openURLDialog) {
            AlertDialog(
                modifier = Modifier.width(600.dp).height(250.dp),
                onDismissRequest = {
                    openURLDialog = false
                },
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
                buttons = {
                    Row(
                        modifier = Modifier.padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            modifier = Modifier.weight(1.0f).padding(5.dp),
                            onClick = { openURLDialog = false }
                        ) {
                            Text("取消")
                        }

                        Button(
                            modifier = Modifier.weight(1.0f).padding(5.dp),
                            onClick = {
                                openURLDialog = false

                                applicationState.loadUrl(picUrl)
                                picUrl = ""
                            }
                        ) {
                            Text("确定")
                        }
                    }
                }
            )
        }
    }

    if (applicationState.isShowPreviewWindow && applicationState.currentImage != null) {
        Window(
            title = "放大预览",
            onCloseRequest = {
                applicationState.togglePreviewWindow(false)
            },
            state = rememberWindowState().apply {
                position = WindowPosition(Alignment.Center)
                placement = WindowPlacement.Fullscreen
            }
        ) {
            ShowImageView(applicationState, applicationState.currentImage!!.toComposeImageBitmap())
        }
    }
}

/**
 * 初始化数据
 */
private fun initData() {

    if (!flag.get()) { // 防止被多次初始化
        filterNames.addAll(getFilterNames())
        saveFilterParams()
        client = HttpConnectionClient(timeout, retryNum)
        flag.set(true)
    }
}
