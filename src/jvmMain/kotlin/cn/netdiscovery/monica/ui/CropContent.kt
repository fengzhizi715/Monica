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
import cn.netdiscovery.monica.utils.extension.flipHorizontally
import cn.netdiscovery.monica.utils.extension.rotate

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.CropContent
 * @author: Tony Shen
 * @date: 2024/5/7 13:56
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun CropContent(state: ApplicationState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isCrop, onCheckedChange = {
            state.isCrop = it
        })
        Text("裁剪", color = Color.Black, fontSize = 20.sp)
    }

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.padding(5.dp),
            onClick = {
                if (state.currentImage!=null) {
                    state.addQueue(state.currentImage!!)
                    state.currentImage = state.currentImage!!.flipHorizontally()
                }
            },
            enabled = state.isCrop
        ) {
            Icon(
                painter = painterResource("flip.png"),
                contentDescription = "翻转",
                modifier = Modifier.size(36.dp)
            )
        }

        IconButton(
            modifier = Modifier.padding(5.dp),
            onClick = {
                if (state.currentImage!=null) {
                    state.addQueue(state.currentImage!!)
                    state.currentImage = state.currentImage!!.rotate(-90.0)
                }
            },
            enabled = state.isCrop
        ) {
            Icon(
                painter = painterResource("rotate.png"),
                contentDescription = "旋转",
                modifier = Modifier.size(36.dp)
            )
        }
    }

}