import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.http.HttpConnectionClient
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.saveFilterParams
import cn.netdiscovery.monica.state.rememberApplicationState
import cn.netdiscovery.monica.ui.*
import java.util.concurrent.atomic.AtomicBoolean

const val previewWidth = 750

val width = (previewWidth * 2.toFloat()).dp
val height = 900.dp
val loadingWidth = (previewWidth*2*0.7).dp

val filterNames = mutableListOf("选择滤镜")

val flag = AtomicBoolean(false)

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)

lateinit var client: HttpConnectionClient

@OptIn(ExperimentalMaterialApi::class)
fun main() = application {

    initData()

    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )

    Tray(
        state = trayState,
        icon = painterResource("launcher.ico"),
    )

    Window(onCloseRequest = ::exitApplication,
        title = "Monica 图片编辑器",
        state = rememberWindowState(width = width, height = height).apply {
            position = WindowPosition(Alignment.BottomCenter)
        }) {
        MenuBar{
            Menu(text = "打开", mnemonic = 'O') {
                Item(
                    text = "本地图片",
                    onClick = {
                        applicationState.onClickImgChoose()
                    },
                )
                Item(
                    text = "网络图片",
                    onClick = {
                        openURLDialog = true
                    },
                )
            }

            Menu(text = "保存", mnemonic = 'S') {
                Item(
                    text = "随机显示图片",
                    onClick = {

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
                    Text(text = "加载网络图片地址")
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
                                picUrl = ""
                                openURLDialog = false
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
            title = "预览",
            onCloseRequest = {
                applicationState.togglePreviewWindow(false)
            },
            state = rememberWindowState().apply {
                position = WindowPosition(Alignment.Center)
                placement = WindowPlacement.Fullscreen
            }
        ) {
            ShowImgView(applicationState.currentImage!!.toComposeImageBitmap())
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
        client = HttpConnectionClient(timeout = 6000,retryNum = 3)
        flag.set(true)
    }
}
