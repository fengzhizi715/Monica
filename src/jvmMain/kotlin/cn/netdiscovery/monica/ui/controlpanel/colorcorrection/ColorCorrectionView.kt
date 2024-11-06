package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import kotlin.math.roundToInt

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

            Row(modifier = Modifier.weight(0.5f)
                .padding(start = 10.dp, end = 10.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(5))) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "contrast：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.contrast,
                                onValueChange = {
                                    viewModel.contrast = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f)

                            Text(
                                text = viewModel.contrast.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "hue：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.hue,
                                onValueChange = {
                                    viewModel.hue = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..360f)

                            Text(
                                text = viewModel.hue.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "saturation：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.saturation,
                                onValueChange = {
                                    viewModel.saturation = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f)

                            Text(
                                text = viewModel.saturation.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "lightness：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.lightness,
                                onValueChange = {
                                    viewModel.lightness = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f)

                            Text(
                                text = viewModel.lightness.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "temperature：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.temperature,
                                onValueChange = {
                                    viewModel.temperature = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f)

                            Text(
                                text = viewModel.temperature.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "highlight：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.highlight,
                                onValueChange = {
                                    viewModel.highlight = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f)

                            Text(
                                text = viewModel.highlight.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "shadow：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.shadow,
                                onValueChange = {
                                    viewModel.shadow = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f)

                            Text(
                                text = viewModel.shadow.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "sharpen：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.sharpen,
                                onValueChange = {
                                    viewModel.sharpen = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..255f)

                            Text(
                                text = viewModel.sharpen.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "corner：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.corner,
                                onValueChange = {
                                    viewModel.corner = it.roundToInt().toFloat()
                                },
                                enabled = true,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..255f)

                            Text(
                                text = viewModel.corner.to2fStr(),
                                color = Color.Unspecified,
                                modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}