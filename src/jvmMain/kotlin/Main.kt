import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
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
        applicationState.window = window

        MainScreen(applicationState)

        if (loadingDisplay) {
            ThreeBallLoading(Modifier.width(loadingWidth).height(height))
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
        flag.set(true)
    }
}
