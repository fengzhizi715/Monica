package cn.netdiscovery.monica.ui.controlpanel

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
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.CropView
 * @author: Tony Shen
 * @date: 2024/5/7 13:56
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun cropView(state: ApplicationState) {
    val viewModel:CropViewModel = koinInject()

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
                viewModel.flip(state)
            },
            enabled = state.isCrop
        ) {
            Icon(
                painter = painterResource("images/flip.png"),
                contentDescription = "翻转",
                modifier = Modifier.size(36.dp)
            )
        }

        IconButton(
            modifier = Modifier.padding(5.dp),
            onClick = {
                viewModel.rotate(state)
            },
            enabled = state.isCrop
        ) {
            Icon(
                painter = painterResource("images/rotate.png"),
                contentDescription = "旋转",
                modifier = Modifier.size(36.dp)
            )
        }
    }

}