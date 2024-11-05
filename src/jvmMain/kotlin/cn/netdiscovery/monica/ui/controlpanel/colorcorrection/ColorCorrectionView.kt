package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.utils.extension.to2fStr
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ColorCorrectionView
 * @author: Tony Shen
 * @date: 2024/11/5 15:05
 * @version: V1.0 <描述当前版本功能>
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun colorCorrection(state: ApplicationState) {
    val viewModel: ColorCorrectionViewModel = koinInject()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.padding(10.dp).weight(1.0f),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {

                },
                enabled = state.currentImage == null
            ) {
                Image(
                    painter = state.currentImage!!.toPainter(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                )
            }

            rightSideMenuBar(modifier = Modifier.weight(0.5f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.padding(start = 10.dp).width(60.dp), text = "饱和度：", color = Color.Unspecified)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = viewModel.saturation,
                            onValueChange = {
                                viewModel.saturation = it
                            },
                            enabled = true,
                            modifier = Modifier.weight(9f),
                            valueRange = -1f..1f)

                        Text(
                            text = viewModel.saturation.to2fStr(),
                            color = Color.Unspecified,
                            modifier = Modifier.weight(1f))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.padding(start = 10.dp).width(60.dp), text = "色相：", color = Color.Unspecified)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = viewModel.hue,
                            onValueChange = {
                                viewModel.hue = it
                            },
                            enabled = true,
                            modifier = Modifier.weight(9f),
                            valueRange = -1f..1f)

                        Text(
                            text = viewModel.hue.to2fStr(),
                            color = Color.Unspecified,
                            modifier = Modifier.weight(1f))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.padding(start = 10.dp).width(60.dp), text = "亮度：", color = Color.Unspecified)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = viewModel.luminance,
                            onValueChange = {
                                viewModel.luminance = it
                            },
                            enabled = true,
                            modifier = Modifier.weight(9f),
                            valueRange = -1f..1f)

                        Text(
                            text = viewModel.luminance.to2fStr(),
                            color = Color.Unspecified,
                            modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}