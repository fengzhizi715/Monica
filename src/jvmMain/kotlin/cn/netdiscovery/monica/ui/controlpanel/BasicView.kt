package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
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
                state.isMosaic = false
                state.isBlur = false
            }
        })
        Text("基础功能", color = Color.Black, fontSize = 20.sp)
    }
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = "图像模糊",
            painter = painterResource("images/blur.png"),
            enable = { state.isBasic },
            onClick = {
                state.isBlur = true
                state.isMosaic = false
            })

        toolTipButton(text = "图像马赛克",
            painter = painterResource("images/mosaic.png"),
            enable = { state.isBasic },
            onClick = {
                state.isMosaic = true
                state.isBlur = false
            })

        toolTipButton(text = "图像涂鸦",
            painter = painterResource("images/doodle.png"),
            enable = { state.isBasic },
            onClick = {
                state.isDoodle = true
                state.togglePreviewWindow(true)
            })
    }
}