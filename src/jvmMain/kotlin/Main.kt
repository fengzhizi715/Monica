import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.di.viewModelModule
import cn.netdiscovery.monica.http.HttpConnectionClient
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.saveFilterParams
import cn.netdiscovery.monica.state.rememberApplicationState
import cn.netdiscovery.monica.ui.main.mainView
import cn.netdiscovery.monica.ui.showimage.showImage
import cn.netdiscovery.monica.ui.widget.ThreeBallLoading
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.Koin
import java.util.concurrent.atomic.AtomicBoolean

val filterNames = mutableListOf("选择滤镜")

val flag = AtomicBoolean(false)

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)

lateinit var client: HttpConnectionClient

lateinit var mAppKoin: Koin

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

        KoinApplication(application = {
            mAppKoin = koin
            modules(viewModelModule)
        }) {
            val previewViewModel: PreviewViewModel = koinInject()

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
                            previewViewModel.saveImage(applicationState)
                        },
                    )
                }
            }
            applicationState.window = window

            mainView(applicationState)

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
            showImage(applicationState, applicationState.currentImage!!.toComposeImageBitmap())
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
