package cn.netdiscovery.monica.ui.controlpanel.ai.experimental

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.getValidateField
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.EdgeDetectionView
 * @author: Tony Shen
 * @date:  2024/10/13 22:17
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val firstDerivativeOperatorTags = arrayListOf("Roberts算子", "Prewitt算子", "Sobel算子")
val secondDerivativeOperatorTags = arrayListOf("Laplace算子", "LoG算子", "DoG算子")

@Composable
fun edgeDetection(state: ApplicationState) {
    val viewModel: EdgeDetectionViewModel = koinInject()

    var firstDerivativeOperatorSelectedOption  = remember { mutableStateOf("Null") }
    var secondDerivativeOperatorSelectedOption = remember { mutableStateOf("Null") }

    var threshold1Text = remember { mutableStateOf("") }
    var threshold2Text = remember { mutableStateOf("") }
    var apertureSizeText = remember { mutableStateOf("3") }

    var sigma1Text = remember { mutableStateOf("") }
    var sigma2Text = remember { mutableStateOf("") }
    var sizeText = remember { mutableStateOf("") }

    fun clearCannyParams() {
        threshold1Text.value = ""
        threshold2Text.value = ""
        apertureSizeText.value = "3"
    }

    fun clearDoGParams() {
        sigma1Text.value = ""
        sigma2Text.value = ""
        sizeText.value = ""
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = "边缘检测", color = Color.Black)

        Column{
            subTitleWithDivider(text = "边缘检测算子", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isFirstDerivativeOperator, onCheckedChange = {
                    CVState.isFirstDerivativeOperator = it

                    if (!CVState.isFirstDerivativeOperator) {
                        firstDerivativeOperatorSelectedOption.value = "Null"
                    } else {
                        CVState.isSecondDerivativeOperator = false
                        CVState.isCannyOperator = false
                        clearCannyParams()
                        clearDoGParams()
                    }
                })
                Text("一阶导数算子", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                firstDerivativeOperatorTags.forEach {
                    RadioButton(
                        selected = (CVState.isFirstDerivativeOperator && it == firstDerivativeOperatorSelectedOption.value),
                        onClick = {
                            firstDerivativeOperatorSelectedOption.value = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }

                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center) {
                    Button(
                        onClick = experimentalViewClick(state) {
                            if (firstDerivativeOperatorSelectedOption.value == "Null") {
                                experimentalViewVerifyToast("请选择一阶导数算子类型")
                                return@experimentalViewClick
                            }

                            when(firstDerivativeOperatorSelectedOption.value) {
                                "Roberts算子" -> viewModel.roberts(state)
                                "Prewitt算子" -> viewModel.prewitt(state)
                                "Sobel算子"   -> viewModel.sobel(state)
                                else         -> {}
                            }
                        }
                    ) {
                        Text(text = "一阶导数算子边缘检测", color = Color.Unspecified)
                    }
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp),verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isSecondDerivativeOperator, onCheckedChange = {
                    CVState.isSecondDerivativeOperator = it

                    if (!CVState.isSecondDerivativeOperator) {
                        secondDerivativeOperatorSelectedOption.value = "Null"
                    } else {
                        CVState.isFirstDerivativeOperator = false
                        CVState.isCannyOperator = false
                        clearCannyParams()
                    }
                })
                Text("二阶导数算子", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                secondDerivativeOperatorTags.forEach {
                    RadioButton(
                        selected = (CVState.isSecondDerivativeOperator && it == secondDerivativeOperatorSelectedOption.value),
                        onClick = {
                            secondDerivativeOperatorSelectedOption.value = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }

                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center) {
                    Button(
                        onClick = experimentalViewClick(state) {
                            if (secondDerivativeOperatorSelectedOption.value == "Null") {
                                experimentalViewVerifyToast("请选择二阶导数算子类型")
                                return@experimentalViewClick
                            }

                            when(secondDerivativeOperatorSelectedOption.value) {
                                "Laplace算子" -> viewModel.laplace(state)
                                "LoG算子"     -> viewModel.log(state)
                                "DoG算子"     -> {
                                    val sigma1 = getValidateField(block = { sigma1Text.value.toDouble() } , failed = { experimentalViewVerifyToast("sigma1 需要 double 类型") }) ?: return@experimentalViewClick
                                    val sigma2 = getValidateField(block = { sigma2Text.value.toDouble() } , failed = { experimentalViewVerifyToast("sigma2 需要 double 类型") }) ?: return@experimentalViewClick
                                    val size = getValidateField(block = { sizeText.value.toInt() } , failed = { experimentalViewVerifyToast("size 需要 int 类型") }) ?: return@experimentalViewClick

                                    viewModel.dog(state, sigma1, sigma2, size)
                                }
                                else         -> {}
                            }
                        }
                    ) {
                        Text(text = "二阶导数算子边缘检测", color = Color.Unspecified)
                    }
                }
            }

            generateDoGParams(secondDerivativeOperatorSelectedOption, sigma1Text, sigma2Text, sizeText)

            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isCannyOperator, onCheckedChange = {
                    CVState.isCannyOperator = it

                    if (!CVState.isCannyOperator) {
                        clearCannyParams()
                    } else {
                        CVState.isFirstDerivativeOperator = false
                        CVState.isSecondDerivativeOperator = false
                        clearDoGParams()
                    }
                })
                Text("Canny算子", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                basicTextFieldWithTitle(titleText = "threshold1", threshold1Text.value) { str ->
                    threshold1Text.value = str
                }

                basicTextFieldWithTitle(titleText = "threshold2", threshold2Text.value) { str ->
                    threshold2Text.value = str
                }

                basicTextFieldWithTitle(titleText = "apertureSize", apertureSizeText.value) { str ->
                    apertureSizeText.value = str
                }
            }

            Button(
                modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
                onClick = experimentalViewClick(state) {
                    if(state.currentImage?.type != BufferedImage.TYPE_BYTE_BINARY) {
                        val threshold1 = getValidateField(block = { threshold1Text.value.toDouble() }, failed = { experimentalViewVerifyToast("threshold1 需要 double 类型") }) ?: return@experimentalViewClick
                        val threshold2 = getValidateField(block = { threshold2Text.value.toDouble() }, failed = { experimentalViewVerifyToast("threshold2 需要 double 类型") }) ?: return@experimentalViewClick
                        val apertureSize = getValidateField(block = { apertureSizeText.value.toInt() }, failed = { experimentalViewVerifyToast("apertureSize 需要 int 类型") }) ?: return@experimentalViewClick

                        viewModel.canny(state, threshold1, threshold2, apertureSize)
                    }
                }
            ) {
                Text(text = "Canny 边缘检测", color = Color.Unspecified)
            }
        }
    }
}

@Composable
private fun generateDoGParams(secondDerivativeOperatorSelectedOption:MutableState<String>,
                              sigma1Text:MutableState<String>,
                              sigma2Text:MutableState<String>,
                              sizeText:MutableState<String>) {
    if (CVState.isSecondDerivativeOperator && secondDerivativeOperatorSelectedOption.value == "DoG算子") {
        Row {
            basicTextFieldWithTitle(titleText = "sigma1", sigma1Text.value) { str ->
                if (CVState.isSecondDerivativeOperator) {
                    sigma1Text.value = str
                }
            }

            basicTextFieldWithTitle(titleText = "sigma2", sigma2Text.value) { str ->
                if (CVState.isSecondDerivativeOperator) {
                    sigma2Text.value = str
                }
            }

            basicTextFieldWithTitle(titleText = "size", sizeText.value) { str ->
                if (CVState.isSecondDerivativeOperator) {
                    sizeText.value = str
                }
            }
        }
    }
}