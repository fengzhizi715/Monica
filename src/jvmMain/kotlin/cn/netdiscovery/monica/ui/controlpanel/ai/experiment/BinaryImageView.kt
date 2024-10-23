package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.composeClick
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.BinaryImageAnalysisView
 * @author: Tony Shen
 * @date:  2024/10/2 15:03
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val typeSelectTags = arrayListOf("THRESH_BINARY", "THRESH_BINARY_INV")
val thresholdSelectTags = arrayListOf("THRESH_OTSU", "THRESH_TRIANGLE")
val adaptiveMethodSelectTags = arrayListOf("ADAPTIVE_THRESH_MEAN_C", "ADAPTIVE_THRESH_GAUSSIAN_C")

@Composable
fun binaryImage(state: ApplicationState) {
    val viewModel: BinaryImageViewModel = koinInject()
    val edgeDetectionViewModel: EdgeDetectionViewModel = koinInject()

    var typeSelectedOption = remember { mutableStateOf("Null") }
    var thresholdSelectedOption = remember { mutableStateOf("Null") }
    var adaptiveMethodSelectedOption = remember { mutableStateOf("Null") }

    var blockSizeText = remember { mutableStateOf("") }
    var cText = remember { mutableStateOf("") }

    var threshold1Text = remember { mutableStateOf("") }
    var threshold2Text = remember { mutableStateOf("") }
    var apertureSizeText = remember { mutableStateOf("3") }

    var hminText = remember { mutableStateOf("") }
    var sminText = remember { mutableStateOf("") }
    var vminText = remember { mutableStateOf("") }
    var hmaxText = remember { mutableStateOf("") }
    var smaxText = remember { mutableStateOf("") }
    var vmaxText = remember { mutableStateOf("") }

    fun clearAdaptiveThreshParams() {
        blockSizeText.value = ""
        cText.value = ""
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 20.dp)) {
//        Column {
//            subTitle(text = "灰度图像", color = Color.Black)
//            divider()
//
//            Button(
//                modifier = Modifier.align(Alignment.End),
//                onClick = composeClick {
//
//                    if(state.currentImage!= null && state.currentImage?.type != BufferedImage.TYPE_BYTE_GRAY) {
//                        viewModel.cvtGray(state)
//                    }
//                }
//            ) {
//                Text(text = "图像灰度化", color = Color.Unspecified)
//            }
//        }

        Column {
            subTitle(text = "阈值分割", color = Color.Black)
            divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {
                        typeSelectedOption.value = "Null"
                        logger.info("取消了阈值化类型")
                    } else {
                        logger.info("勾选了阈值化类型")
                    }
                })
                Text("阈值化类型", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                typeSelectTags.forEach {
                    RadioButton(
                        selected = (state.isThreshType && it == typeSelectedOption.value),
                        onClick = {
                            typeSelectedOption.value = it
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshSegment, onCheckedChange = {
                    state.isThreshSegment = it

                    if (!state.isThreshSegment) {
                        thresholdSelectedOption.value = "Null"
                        logger.info("取消了全局阈值分割")
                    } else {
                        state.isAdaptiveThresh = false
                        adaptiveMethodSelectedOption.value = "Null"
                        clearAdaptiveThreshParams()
                        logger.info("勾选了全局阈值分割")
                    }
                })
                Text("全局阈值分割", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                thresholdSelectTags.forEach {
                    RadioButton(
                        selected = (state.isThreshSegment && it == thresholdSelectedOption.value),
                        onClick = {
                            thresholdSelectedOption.value = it
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isAdaptiveThresh, onCheckedChange = {
                    state.isAdaptiveThresh = it

                    if (!state.isAdaptiveThresh) {
                        adaptiveMethodSelectedOption.value = "Null"
                        clearAdaptiveThreshParams()
                        logger.info("取消了自适应阈值分割")
                    } else {
                        state.isThreshSegment = false
                        thresholdSelectedOption.value = "Null"
                        logger.info("勾选了自适应阈值分割")
                    }
                })
                Text("自适应阈值分割", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                Text("自适应阈值算法", modifier = Modifier.align(Alignment.CenterVertically))

                adaptiveMethodSelectTags.forEach {
                    RadioButton(
                        selected = (state.isAdaptiveThresh && it == adaptiveMethodSelectedOption.value),
                        onClick = {
                            adaptiveMethodSelectedOption.value = it
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row {
                Text(text = "blockSize")

                basicTextField(blockSizeText.value) { str ->
                    if (state.isAdaptiveThresh) {
                        blockSizeText.value = str
                    }
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "c")

                basicTextField(cText.value) { str ->
                    if (state.isAdaptiveThresh) {
                        cText.value = str
                    }
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = composeClick {
                    if(state.currentImage!= null && state.currentImage?.type != BufferedImage.TYPE_BYTE_BINARY) {

                        if (state.isThreshType && state.isThreshSegment) {

                            if (typeSelectedOption.value == "Null") {
                                verifyToastMessage = "请选择阈值化类型类型"
                                showVerifyToast = true

                                return@composeClick
                            }

                            if (thresholdSelectedOption.value == "Null") {
                                verifyToastMessage = "请选择全局阈值分割类型"
                                showVerifyToast = true

                                return@composeClick
                            }

                            viewModel.threshold(state, typeSelectedOption.value, thresholdSelectedOption.value)
                        } else if (state.isThreshType && state.isAdaptiveThresh) {
                            // TODO 增加校验
                            viewModel.adaptiveThreshold(state, adaptiveMethodSelectedOption.value, typeSelectedOption.value, blockSizeText.value.toInt(), cText.value.toInt())
                        }
                    }
                }
            ) {
                Text(text = "阈值分割", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitle(text = "Canny 边缘检测", color = Color.Black)
            divider()

            Row {
                Text(text = "threshold1")

                basicTextField(threshold1Text.value) { str ->
                    threshold1Text.value = str
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "threshold2")

                basicTextField(threshold2Text.value) { str ->
                    threshold2Text.value = str
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "apertureSize")

                basicTextField(apertureSizeText.value) { str ->
                    apertureSizeText.value = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = composeClick {
                    if(state.currentImage!= null && state.currentImage?.type != BufferedImage.TYPE_BYTE_BINARY) {
                        // TODO 增加校验
                        edgeDetectionViewModel.canny(state, threshold1Text.value.toDouble(), threshold2Text.value.toDouble(), apertureSizeText.value.toInt())
                    }
                }
            ) {
                Text(text = "Canny 边缘检测", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitle(text = "彩色图像分割", color = Color.Black)
            divider()

            Row {
                Text(text = "hmin")

                basicTextField(hminText.value) { str ->
                    hminText.value = str
                }

                Text(text = "smin")

                basicTextField(sminText.value) { str ->
                    sminText.value = str
                }

                Text(text = "vmin")

                basicTextField(vminText.value) { str ->
                    vminText.value = str
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp)){
                Text(text = "hmax")

                basicTextField(hmaxText.value) { str ->
                    hmaxText.value = str
                }

                Text(text = "smax")

                basicTextField(smaxText.value) { str ->
                    smaxText.value = str
                }

                Text(text = "vmax")

                basicTextField(vmaxText.value) { str ->
                    vmaxText.value = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = composeClick {
                    if(state.currentImage!= null && state.currentImage?.type!! in 1..9) {
                        // TODO 增加校验
                        viewModel.inRange(state, hminText.value.toInt(), sminText.value.toInt(), vminText.value.toInt(), hmaxText.value.toInt(), smaxText.value.toInt(), vmaxText.value.toInt())
                    }
                }
            ) {
                Text(text = "彩色图像分割", color = Color.Unspecified)
            }
        }
    }
}