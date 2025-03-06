package cn.netdiscovery.monica.ui.controlpanel.ai

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import cn.netdiscovery.monica.config.isProVersion
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

    val viewModel: AIViewModel = koinInject()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isAI, onCheckedChange = {
            state.isAI = it

            if (!state.isAI) {
                state.resetCurrentStatus()
                logger.info("取消了 AI 实验室")
            } else {
                logger.info("勾选了 AI 实验室")

                state.isGeneralSettings = false
                state.isBasic = false
                state.isColorCorrection = false
                state.isFilter = false
            }
        })
        subTitle(text = "AI 实验室", color = Color.Black)
    }

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = "简单 CV 算法的快速验证",
            painter = painterResource("images/ai/experiment.png"),
            enable = { state.isAI },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(OpenCVDebugStatus)
            })

        if (isProVersion) {
            toolTipButton(text = "人脸检测",
                painter = painterResource("images/ai/face_detect.png"),
                enable = { state.isAI },
                onClick = {
                    state.currentStatus = FaceDetectStatus
                    viewModel.faceDetect(state)
                })

            toolTipButton(text = "生成素描画",
                painter = painterResource("images/ai/sketch_drawing.png"),
                enable = { state.isAI },
                onClick = {
                    state.currentStatus = SketchDrawingStatus
                    viewModel.sketchDrawing(state)
                })

            toolTipButton(text = "人脸替换",
                painter = painterResource("images/ai/face_swap.png"),
                enable = { state.isAI },
                onClick = {
                    state.togglePreviewWindowAndUpdateStatus(FaceSwapStatus)
                })
        }
    }
}