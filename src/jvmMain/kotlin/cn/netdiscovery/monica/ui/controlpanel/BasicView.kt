package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.widget.toolTipButton

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.BasicView
 * @author: Tony Shen
 * @date: 2024/5/1 00:39
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun basicView(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isBasic, onCheckedChange = {
            state.isBasic = it

            if (!state.isBasic) {
                state.currentStatus = 0
            }
        })
        Text("基础功能", color = Color.Black, fontSize = 20.sp)
    }
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = "图像模糊",
            painter = painterResource("images/controlpanel/blur.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = BlurStatus
            })

        toolTipButton(text = "图像马赛克",
            painter = painterResource("images/controlpanel/mosaic.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = MosaicStatus
            })

        toolTipButton(text = "图像涂鸦",
            painter = painterResource("images/controlpanel/doodle.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = DoodleStatus
                state.togglePreviewWindow(true)
            })

        toolTipButton(text = "图像取色",
            painter = painterResource("images/controlpanel/color-picker.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = ColorPickStatus
                state.togglePreviewWindow(true)
            })
    }
}