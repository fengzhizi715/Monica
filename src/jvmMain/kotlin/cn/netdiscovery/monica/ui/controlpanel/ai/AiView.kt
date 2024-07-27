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
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.AiView
 * @author: Tony Shen
 * @date: 2024/7/27 11:13
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun aiView(state: ApplicationState) {

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

                if (state.currentImage!=null) {
                    val width = state.currentImage!!.width
                    val height = state.currentImage!!.height
                    val byteArray = state.currentImage!!.image2ByteArray()

                    state.scope.launch(IO) {
                        clickLoadingDisplay {
                            try {
                                val outPixels = ImageProcess.faceDetect(byteArray)
                                state.addQueue(state.currentImage!!)
                                state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
                            } catch (e:Exception) {
                                logger.error("faceDetect is failed", e)
                            }
                        }
                    }
                }
            })
    }
}