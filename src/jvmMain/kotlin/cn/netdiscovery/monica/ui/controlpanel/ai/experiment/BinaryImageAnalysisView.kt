package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.divider
import cn.netdiscovery.monica.ui.widget.subTitle
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
val firstDerivativeOperatorTags = arrayListOf("Roberts算子", "Prewitt算子", "Sobel算子")
val secondDerivativeOperatorTags = arrayListOf("Laplace算子")

@Composable
fun binaryImageAnalysis(state: ApplicationState) {
    val viewModel: BinaryImageAnalysisViewModel = koinInject()

    var typeSelectedOption = remember { mutableStateOf("Null") }
    var thresholdSelectedOption = remember { mutableStateOf("Null") }
    var adaptiveMethodSelectedOption = remember { mutableStateOf("Null") }
    var firstDerivativeOperatorSelectedOption = remember { mutableStateOf("Null") }
    var secondDerivativeOperatorSelectedOption = remember { mutableStateOf("Null") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp)) {
        Column(modifier = Modifier.padding(top = 5.dp).weight(0.1f)) {
            subTitle(text = "灰度图像", color = Color.Black)
            divider()

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = composeClick {

                    if(state.currentImage!= null && state.currentImage?.type != BufferedImage.TYPE_BYTE_GRAY) {
                        viewModel.cvtGray(state)
                    }
                }
            ) {
                Text(text = "图像灰度化", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.weight(0.45f)) {
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

            var blockSizeText by remember {
                mutableStateOf("")
            }

            var cText by remember {
                mutableStateOf("")
            }

            Row {
                Text(text = "blockSize")

                BasicTextField(
                    value = blockSizeText,
                    onValueChange = { str ->
                        blockSizeText = str
                    },
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions.Default,
                    cursorBrush = SolidColor(Color.Gray),
                    singleLine = true,
                    modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
                    textStyle = TextStyle(Color.Black, fontSize = 12.sp)
                )
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "c")

                BasicTextField(
                    value = cText,
                    onValueChange = { str ->
                        cText = str
                    },
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions.Default,
                    cursorBrush = SolidColor(Color.Gray),
                    singleLine = true,
                    modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
                    textStyle = TextStyle(Color.Black, fontSize = 12.sp)
                )
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = composeClick {
                    if(state.currentImage!= null && state.currentImage?.type != BufferedImage.TYPE_BYTE_BINARY) {

                        if (state.isThreshType && state.isThreshSegment) {
                            // TODO 增加校验
                            viewModel.threshold(state, typeSelectedOption.value, thresholdSelectedOption.value)
                        } else if (state.isThreshType && state.isAdaptiveThresh) {
                            // TODO 增加校验
                            viewModel.adaptiveThreshold(state, adaptiveMethodSelectedOption.value, typeSelectedOption.value, blockSizeText.toInt(), cText.toInt())
                        }
                    }
                }
            ) {
                Text(text = "阈值分割", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.weight(0.45f)) {
            subTitle(text = "边缘检测算子", color = Color.Black)
            divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isFirstDerivativeOperator, onCheckedChange = {
                    state.isFirstDerivativeOperator = it

                    if (!state.isFirstDerivativeOperator) {
                        firstDerivativeOperatorSelectedOption.value = "Null"
                    }
                })
                Text("一阶导数算子", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                firstDerivativeOperatorTags.forEach {
                    RadioButton(
                        selected = (state.isFirstDerivativeOperator && it == firstDerivativeOperatorSelectedOption.value),
                        onClick = {
                            firstDerivativeOperatorSelectedOption.value = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp),verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isSecondDerivativeOperator, onCheckedChange = {
                    state.isSecondDerivativeOperator = it

                    if (!state.isSecondDerivativeOperator) {
                        secondDerivativeOperatorSelectedOption.value = "Null"
                    }
                })
                Text("二阶导数算子", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                secondDerivativeOperatorTags.forEach {
                    RadioButton(
                        selected = (state.isSecondDerivativeOperator && it == secondDerivativeOperatorSelectedOption.value),
                        onClick = {
                            secondDerivativeOperatorSelectedOption.value = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isCannyOperator, onCheckedChange = {
                    state.isCannyOperator = it

                    if (!state.isCannyOperator) {
                        typeSelectedOption.value = "Null"
                    }
                })
                Text("Canny算子", modifier = Modifier.align(Alignment.CenterVertically))
            }

            var threshold1Text by remember {
                mutableStateOf("")
            }

            var threshold2Text by remember {
                mutableStateOf("")
            }

            var apertureSizeText by remember {
                mutableStateOf("3")
            }

            Row {
                Text(text = "threshold1")

                BasicTextField(
                    value = threshold1Text,
                    onValueChange = { str ->
                        threshold1Text = str
                    },
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions.Default,
                    cursorBrush = SolidColor(Color.Gray),
                    singleLine = true,
                    modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
                    textStyle = TextStyle(Color.Black, fontSize = 12.sp)
                )
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "threshold2")

                BasicTextField(
                    value = threshold2Text,
                    onValueChange = { str ->
                        threshold2Text = str
                    },
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions.Default,
                    cursorBrush = SolidColor(Color.Gray),
                    singleLine = true,
                    modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
                    textStyle = TextStyle(Color.Black, fontSize = 12.sp)
                )
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "apertureSize")

                BasicTextField(
                    value = apertureSizeText,
                    onValueChange = { str ->
                        apertureSizeText = str
                    },
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions.Default,
                    cursorBrush = SolidColor(Color.Gray),
                    singleLine = true,
                    modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
                    textStyle = TextStyle(Color.Black, fontSize = 12.sp)
                )
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = composeClick {

                }
            ) {
                Text(text = "Canny 边缘检测", color = Color.Unspecified)
            }
        }
    }
}