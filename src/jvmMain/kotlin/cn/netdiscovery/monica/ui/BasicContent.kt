package cn.netdiscovery.monica.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.BasicContent
 * @author: Tony Shen
 * @date: 2024/5/1 00:39
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun basicContent(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isBasic, onCheckedChange = {
            state.isBasic = it

            if (!state.isBasic) {
                state.isMosaic = false
            }
        })
        Text("基础功能", color = Color.Black, fontSize = 20.sp)
    }
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
//        IconButton(
//            modifier = Modifier.padding(5.dp),
//            onClick = {
//            },
//            enabled = state.isBasic
//        ) {
//            Icon(
//                painter = painterResource("draw.png"),
//                contentDescription = "绘画",
//                modifier = Modifier.size(36.dp)
//            )
//        }

        IconButton(
            modifier = Modifier.padding(5.dp),
            onClick = {
            },
            enabled = state.isBasic
        ) {
            Icon(
                painter = painterResource("blur.png"),
                contentDescription = "模糊",
                modifier = Modifier.size(36.dp)
            )
        }

        IconButton(
            modifier = Modifier.padding(5.dp),
            onClick = {
                state.isMosaic = true
            },
            enabled = state.isBasic
        ) {
            Icon(
                painter = painterResource("mosaic.png"),
                contentDescription = "马赛克",
                modifier = Modifier.size(36.dp)
            )
        }

//        IconButton(
//            modifier = Modifier.padding(5.dp),
//            onClick = {
//            },
//            enabled = state.isBasic
//        ) {
//            Icon(
//                painter = painterResource("add_text.png"),
//                contentDescription = "添加文字",
//                modifier = Modifier.size(36.dp)
//            )
//        }
    }
}