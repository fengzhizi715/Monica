package cn.netdiscovery.monica.ui.controlpanel

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
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.state.EqualizeHistStatus
import cn.netdiscovery.monica.state.GammaStatus
import cn.netdiscovery.monica.ui.widget.toolTipButton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ImageEnhanceView
 * @author: Tony Shen
 * @date: 2024/7/13 21:22
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun imageEnhanceView(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isEnhance, onCheckedChange = {
            state.isEnhance = it

            if (!state.isEnhance) {
                state.resetCurrentStatus()
                logger.info("取消了图像增强")
            } else {
                logger.info("勾选了图像增强")
            }
        })
        Text("图像增强", color = Color.Black, fontSize = 20.sp)
    }
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = "直方图均衡化",
            painter = painterResource("images/imageenhance/histogram.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = EqualizeHistStatus

                val width = state.currentImage!!.width
                val height = state.currentImage!!.height
                val byteArray = state.currentImage!!.image2ByteArray()

                val result = ImageProcess.equalizeHist(byteArray,width,height)

                state.currentImage = BufferedImages.toBufferedImage(result,width,height)
            })

        toolTipButton(text = "gamma 变换",
            painter = painterResource("images/imageenhance/gamma.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = GammaStatus
            })
    }
}