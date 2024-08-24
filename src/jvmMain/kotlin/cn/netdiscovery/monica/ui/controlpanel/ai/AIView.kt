package cn.netdiscovery.monica.ui.controlpanel.ai

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.controlpanel.enhance.ImageEnhanceViewModel
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
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

    val viewModel: AIViewMode = koinInject()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isAI, onCheckedChange = {
            state.isAI = it

            if (!state.isAI) {
                state.resetCurrentStatus()
                logger.info("取消了 AI 实验室")
            } else {
                logger.info("勾选了 AI 实验室")
            }
        })
        Text("AI 实验室", color = Color.Black, fontSize = 20.sp)
    }

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        toolTipButton(text = "人脸 landmark 提取",
            painter = painterResource("images/ai/face_landmark.png"),
            enable = { state.isAI },
            onClick = {
                state.currentStatus = FaceLandMarkStatus
                viewModel.faceLandMark(state)
            })

        toolTipButton(text = "换脸",
            painter = painterResource("images/ai/face_swap.png"),
            enable = { state.isAI },
            onClick = {
                state.currentStatus = FaceSwapStatus
                state.togglePreviewWindow(true)
            })
    }
}