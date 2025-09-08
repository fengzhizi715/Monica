package cn.netdiscovery.monica.ui.controlpanel.ai

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.ui.widget.toolTipButton
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.AIView
 * @author: Tony Shen
 * @date: 2024/7/27 11:13
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun aiView(state: ApplicationState) {
    val i18nState = rememberI18nState()
    val viewModel: AIViewModel = koinInject()

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(
            text = i18nState.getString("simple_cv_algorithm"),
            painter = painterResource("images/ai/experiment.png"),
            enable = { state.isAI },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(OpenCVDebugStatus)
            })

        toolTipButton(
            text = i18nState.getString("face_detection"),
            painter = painterResource("images/ai/face_detect.png"),
            enable = { state.isAI },
            onClick = {
                state.currentStatus = FaceDetectStatus
                viewModel.faceDetect(state)
            })

        toolTipButton(
            text = i18nState.getString("generate_sketch"),
            painter = painterResource("images/ai/sketch_drawing.png"),
            enable = { state.isAI },
            onClick = {
                state.currentStatus = SketchDrawingStatus
                viewModel.sketchDrawing(state)
            })

        toolTipButton(text = i18nState.getString("face_swap"),
            painter = painterResource("images/ai/face_swap.png"),
            enable = { state.isAI },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(FaceSwapStatus)
            })

    }

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = i18nState.getString("anime_style"),
            painter = painterResource("images/ai/cartoon.png"),
            enable = { state.isAI },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(CartoonStatus)
            })
    }
}