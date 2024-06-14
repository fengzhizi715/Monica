import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.di.viewModelModule
import cn.netdiscovery.monica.http.HttpConnectionClient
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.saveFilterParams
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.state.rememberApplicationState
import cn.netdiscovery.monica.ui.controlpanel.colorpick.colorPick
import cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropImage
import cn.netdiscovery.monica.ui.controlpanel.doodle.drawImage
import cn.netdiscovery.monica.ui.main.mainView
import cn.netdiscovery.monica.ui.main.openURLDialog
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import cn.netdiscovery.monica.ui.showimage.showImage
import cn.netdiscovery.monica.ui.widget.ThreeBallLoading
import cn.netdiscovery.monica.ui.widget.TopToast
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.Koin
import java.util.concurrent.atomic.AtomicBoolean

val filterNames = mutableListOf("选择滤镜")

val flag = AtomicBoolean(false)

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)
var picUrl by mutableStateOf("")

var showToast by mutableStateOf(false)
var toastMessage by mutableStateOf("")

lateinit var client: HttpConnectionClient

lateinit var mAppKoin: Koin

fun main() = application {

    initData()

    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )

    lateinit var previewViewModel: PreviewViewModel
    lateinit var cropViewModel: CropViewModel

    Tray(
        state = trayState,
        icon = painterResource("images/launcher.ico"),
        menu = {
            Item(
                text = "打开本地图片",
                onClick = {
                    previewViewModel.chooseImage(applicationState)
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
            previewViewModel = koinInject()
            cropViewModel    = koinInject()

            applicationState.window = window

            mainView(applicationState)

            if (loadingDisplay) {
                ThreeBallLoading(Modifier.width(loadingWidth).height(height))
            }

            if (openURLDialog) {
                openURLDialog(
                    onConfirm = {
                        openURLDialog = false

                        previewViewModel.loadUrl(picUrl, applicationState)

                        picUrl = ""
                    },
                    onDismiss = {
                        openURLDialog = false
                    })
            }

            if (showToast) {
                TopToast(Modifier,toastMessage, onDismissCallback = {
                    showToast = false
                })
            }
        }
    }

    if (applicationState.isShowPreviewWindow && applicationState.currentImage != null) {
        Window(
            title = getWindowsTitle(applicationState),
            onCloseRequest = {
                if (applicationState.isDoodle) {
                    toastMessage = "想要保存涂鸦效果，需要点击保存按钮"
                    showToast = true
                } else if (applicationState.isCropSize) {
                    cropViewModel.clearCropImageView()
                }

                applicationState.isColorPick = false
                applicationState.isDoodle = false
                applicationState.isCropSize = false
                applicationState.togglePreviewWindow(false)
            },
            state = rememberWindowState().apply {
                position = WindowPosition(Alignment.Center)
                placement = WindowPlacement.Fullscreen
            }
        ) {
            if (applicationState.isColorPick) {
                colorPick(applicationState)
            } else if (applicationState.isDoodle) {
                drawImage(applicationState)
            } else if (applicationState.isCropSize) {
                cropImage(applicationState)
            } else {
                showImage(applicationState)
            }
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

private fun getWindowsTitle(state: ApplicationState):String {

   return if (state.isDoodle) "涂鸦图像" else if (state.isCropSize) "图像裁剪" else "放大预览"
}
