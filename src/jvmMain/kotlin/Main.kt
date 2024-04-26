import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.state.rememberApplicationState
import cn.netdiscovery.monica.ui.MainScreen

const val previewWidth = 600

fun main() = application {

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
}
