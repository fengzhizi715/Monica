import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.state.rememberApplicationState
import cn.netdiscovery.monica.ui.MainScreen
import cn.netdiscovery.monica.ui.ShowImgView
import cn.netdiscovery.monica.ui.getFilterNames

const val previewWidth = 750

val filterNames = mutableListOf("选择滤镜")

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
        state = rememberWindowState(width = Dp(previewWidth * 2.toFloat()), height = 900.dp).apply {
            position = WindowPosition(Alignment.BottomCenter)
        }) {
        applicationState.window = window

        MainScreen(applicationState)
    }

    if (applicationState.isShowPreviewWindow && applicationState.showImg != null) {
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
            ShowImgView(applicationState.showImg!!.toComposeImageBitmap())
        }
    }
}

/**
 * 初始化数据
 */
private fun initData() {

    filterNames.addAll(getFilterNames())
}
