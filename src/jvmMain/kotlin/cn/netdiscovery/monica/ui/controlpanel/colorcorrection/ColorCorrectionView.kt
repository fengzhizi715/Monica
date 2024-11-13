package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.model.ColorCorrectionSettings
import cn.netdiscovery.monica.ui.widget.showLoading
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.extension.to2fStr
import loadingDisplay
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
var colorCorrectionSettings = ColorCorrectionSettings()

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun colorCorrection(state: ApplicationState) {
    val viewModel: ColorCorrectionViewModel = koinInject()

    var cachedImage by remember { mutableStateOf(state.currentImage!!) } // 缓存 state.currentImage

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
                modifier = Modifier.padding(10.dp).weight(1.4f),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                },
                enabled = true
            ) {
                Image(
                    painter = cachedImage.toPainter(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                )
            }

            Row(modifier = Modifier.weight(0.6f)
                .padding(start = 10.dp, end = 10.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(5))) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = "对比度：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.contrast,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.contrast = value.toFloat()
                                    colorCorrectionSettings = colorCorrectionSettings.copy(contrast = value, status = 1)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "色调：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.hue,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.hue = value.toFloat()
                                    colorCorrectionSettings = colorCorrectionSettings.copy(hue = value, status = 2)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "饱和度：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.saturation,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.saturation = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(saturation = value, status = 3)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "亮度：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.lightness,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.lightness = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(lightness = value, status = 4)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "色温：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.temperature,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.temperature = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(temperature = value, status = 5)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "高光：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.highlight,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.highlight = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(highlight = value, status = 6)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "阴影：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.shadow,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.shadow = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(shadow = value, status = 7)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "锐化：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.sharpen,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.sharpen = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(sharpen = value, status = 8)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { cachedImage = it}
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
                        Text(modifier = Modifier.width(100.dp), text = "暗角：", color = Color.Unspecified)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.corner,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.corner = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(corner = value, status = 9)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings) { cachedImage = it}
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

                    Row(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)) {
                        toolTipButton(text = "保存",
                            painter = painterResource("images/doodle/save.png"),
                            iconModifier = Modifier.size(36.dp),
                            onClick = {
                                viewModel.save(cachedImage) { state.currentImage = it}
                                state.togglePreviewWindow(false)
                            })
                    }
                }
            }
        }

        if (loadingDisplay) {
            showLoading()
        }
    }
}