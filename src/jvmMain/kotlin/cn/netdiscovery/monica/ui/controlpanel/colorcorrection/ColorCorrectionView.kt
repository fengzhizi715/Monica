package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.llm.DialogSession
import cn.netdiscovery.monica.llm.systemPromptForColorCorrection
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.PageLifecycle
import cn.netdiscovery.monica.ui.widget.showLoading
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator
import cn.netdiscovery.monica.utils.extensions.to2fStr
import cn.netdiscovery.monica.i18n.getCurrentStringResource
import loadingDisplay
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.roundToInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ColorCorrectionView
 * @author: Tony Shen
 * @date: 2024/11/5 15:05
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

var colorCorrectionSettings = ColorCorrectionSettings()
private var showLLMDialog by mutableStateOf(false)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun colorCorrection(state: ApplicationState) {
    val viewModel: ColorCorrectionViewModel = koinInject()
    val density = LocalDensity.current
    val i18nState = getCurrentStringResource()

    var cachedImage by remember { mutableStateOf(state.currentImage!!) } // 缓存 state.currentImage

    val enableSlider = !loadingDisplay

    val session = remember { DialogSession(systemPromptForColorCorrection, colorCorrectionSettings) }
    
    // 使用统一的图片尺寸计算
    val (imageWidth, imageHeight) = ImageSizeCalculator.calculateImageSize(state)
    
    // 获取原始图片尺寸和显示尺寸，用于坐标转换
    val originalSize = ImageSizeCalculator.getImagePixelSize(state)

    PageLifecycle(
        onInit = {
            logger.info("ColorCorrectionView 启动时初始化")
        },
        onDisposeEffect = {
            logger.info("ColorCorrectionView 关闭时释放资源")
            viewModel.clearAllStatus()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background,
                        MaterialTheme.colors.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1.4f)
                    .width(imageWidth)
                    .height(imageHeight),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                },
                enabled = false
            ) {
                Image(
                    bitmap = cachedImage.toComposeImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(modifier = Modifier.weight(0.6f)
                .padding(start = 10.dp, end = 10.dp)
                .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(5))) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("contrast") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.contrast,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.contrast = value.toFloat()
                                    colorCorrectionSettings = colorCorrectionSettings.copy(contrast = value, status = 1)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f,
                                colors = SliderDefaults.colors())

                            Text(
                                text = viewModel.contrast.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("hue") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.hue,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.hue = value.toFloat()
                                    colorCorrectionSettings = colorCorrectionSettings.copy(hue = value, status = 2)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..360f)

                            Text(
                                text = viewModel.hue.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("saturation") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.saturation,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.saturation = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(saturation = value, status = 3)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f,
                                colors = SliderDefaults.colors())

                            Text(
                                text = viewModel.saturation.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("lightness") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.lightness,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.lightness = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(lightness = value, status = 4)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f,
                                colors = SliderDefaults.colors())

                            Text(
                                text = viewModel.lightness.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("temperature") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.temperature,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.temperature = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(temperature = value, status = 5)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f,
                                colors = SliderDefaults.colors())

                            Text(
                                text = viewModel.temperature.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("highlight") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.highlight,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.highlight = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(highlight = value, status = 6)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f,
                                colors = SliderDefaults.colors())

                            Text(
                                text = viewModel.highlight.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("shadow") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.shadow,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.shadow = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(shadow = value, status = 7)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..510f,
                                colors = SliderDefaults.colors())

                            Text(
                                text = viewModel.shadow.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("sharpen") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.sharpen,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.sharpen = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(sharpen = value, status = 8)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..255f)

                            Text(
                                text = viewModel.sharpen.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.width(100.dp), text = i18nState.get("corner") + "：", color = MaterialTheme.colors.onSurface)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = viewModel.corner,
                                onValueChange = {
                                    val value = it.roundToInt()
                                    viewModel.corner = value.toFloat()

                                    colorCorrectionSettings = colorCorrectionSettings.copy(corner = value, status = 9)

                                    viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image-> cachedImage = image }
                                },
                                enabled = enableSlider,
                                modifier = Modifier.weight(9f),
                                valueRange = 0f..255f)

                            Text(
                                text = viewModel.corner.to2fStr(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.weight(1f))
                        }
                    }

                    // 底部菜单
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)) {
                        toolTipButton(text = i18nState.get("save"),
                            painter = painterResource("images/doodle/save.png"),
                            iconModifier = Modifier.size(36.dp),
                            onClick = {
                                viewModel.save(state) {
                                    state.addQueue(state.currentImage!!)
                                    state.currentImage = cachedImage
                                    state.togglePreviewWindow(false)
                                }
                            })

                        toolTipButton(text = i18nState.get("natural_language_color_correction"),
                            painter = painterResource("images/colorcorrection/chatbot.png"),
                            iconModifier = Modifier.size(36.dp),
                            onClick = {
                                showLLMDialog = true
                            })
                    }
                }
            }
        }

        if (loadingDisplay) {
            showLoading()
        }

        if (showLLMDialog) {
            NaturalLanguageDialog(showLLMDialog, session, state.deepSeekApiKeyText, state.geminiApiKeyText, onDismissRequest = {
                showLLMDialog = false
            }) {
                colorCorrectionSettings = it
                viewModel.updateParams(colorCorrectionSettings)
                viewModel.colorCorrection(state, cachedImage, colorCorrectionSettings)  { image -> cachedImage = image }
            }
        }
    }
}