package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import cn.netdiscovery.monica.utils.extension.to2fStr
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ImageProcessView
 * @author: Tony Shen
 * @date: 2024/5/1 00:43
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun imageProcessView(state: ApplicationState) {
    val viewModel: PreviewViewModel = koinInject()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isHLS, onCheckedChange = {
            state.isHLS = it

            if (!state.isHLS) {
                viewModel.saturation = 0f
                viewModel.hue = 0f
                viewModel.luminance = 0f

                logger.info("取消了图像处理")
            } else {
                logger.info("勾选了图像处理")
            }
        })
        Text("图像处理", color = Color.Black, fontSize = 20.sp)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "饱和度增益：",
            color = if (state.isHLS) Color.Unspecified else Color.LightGray
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = viewModel.saturation,
                onValueChange = {
                    viewModel.saturation = it
                },
                enabled = state.isHLS,
                modifier = Modifier.weight(8f),
                valueRange = -1f..1f
            )
            Text(
                text = viewModel.saturation.to2fStr(),
                color = if (state.isHLS) Color.Unspecified else Color.LightGray,
                modifier = Modifier.weight(2f)
            )
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "色相增益：",
            color = if (state.isHLS) Color.Unspecified else Color.LightGray
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = viewModel.hue,
                onValueChange = {
                    viewModel.hue = it
                },
                enabled = state.isHLS,
                modifier = Modifier.padding(start = 15.dp).weight(8f),
                valueRange = -1f..1f
            )
            Text(
                text = viewModel.hue.to2fStr(),
                color = if (state.isHLS) Color.Unspecified else Color.LightGray,
                modifier = Modifier.weight(2f)
            )
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "亮度增益：",
            color = if (state.isHLS) Color.Unspecified else Color.LightGray
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = viewModel.luminance,
                onValueChange = {
                    viewModel.luminance = it
                },
                enabled = state.isHLS,
                modifier = Modifier.padding(start = 15.dp).weight(8f),
                valueRange = -1f..1f
            )
            Text(
                text = viewModel.luminance.to2fStr(),
                color = if (state.isHLS) Color.Unspecified else Color.LightGray,
                modifier = Modifier.weight(2f)
            )
        }
    }
}