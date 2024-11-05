package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.state.ColorCorrectionStatus
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.utils.composeClick
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ColorCorrectionView
 * @author: Tony Shen
 * @date: 2024/5/1 00:43
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun colorCorrectionView(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isColorCorrection, onCheckedChange = {
            state.isColorCorrection = it

            if (!state.isColorCorrection) {
                logger.info("取消了图像调色")
            } else {
                logger.info("勾选了图像调色")
            }
        })
        subTitle(text = "图像调色", color = Color.Black)
    }
    Column(modifier = Modifier.fillMaxWidth()){
        Button(
            modifier = Modifier.align(Alignment.Start).padding(start = 15.dp),
            enabled = state.isColorCorrection,
            onClick = composeClick {
                state.currentStatus = ColorCorrectionStatus
                state.togglePreviewWindow(true)
            }
        ) {
            Text(text = "进入图像调色界面", color = Color.Unspecified)
        }
    }
}