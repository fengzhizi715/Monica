package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
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
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.EdgeDetectionViewModel
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.i18n.LocalizationManager
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.getValidateField
import cn.netdiscovery.monica.exception.showError
import cn.netdiscovery.monica.exception.ErrorType
import cn.netdiscovery.monica.exception.ErrorSeverity
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

val firstDerivativeOperatorTags = arrayListOf(LocalizationManager.getString("roberts_operator"), LocalizationManager.getString("prewitt_operator"), LocalizationManager.getString("sobel_operator"))
val secondDerivativeOperatorTags = arrayListOf(LocalizationManager.getString("laplace_operator"), LocalizationManager.getString("log_operator"), LocalizationManager.getString("dog_operator"))

@Composable
fun edgeDetection(state: ApplicationState, title: String) {
    val i18nState = rememberI18nState()
    val viewModel: EdgeDetectionViewModel = koinInject()

    var firstDerivativeOperatorSelectedOption  by remember { mutableStateOf("Null") }
    var secondDerivativeOperatorSelectedOption by remember { mutableStateOf("Null") }

    var threshold1Text by remember { mutableStateOf("") }
    var threshold2Text by remember { mutableStateOf("") }
    var apertureSizeText by remember { mutableStateOf("3") }

    var sigma1Text by remember { mutableStateOf("") }
    var sigma2Text by remember { mutableStateOf("") }
    var sizeText by remember { mutableStateOf("") }

    fun clearCannyParams() {
        threshold1Text = ""
        threshold2Text = ""
        apertureSizeText = "3"
    }

    fun clearDoGParams() {
        sigma1Text = ""
        sigma2Text = ""
        sizeText = ""
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = title, color = Color.Black)

        Column{
            subTitleWithDivider(text = i18nState.getString("edge_detection_operator"), color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isFirstDerivativeOperator, onCheckedChange = {
                    CVState.isFirstDerivativeOperator = it

                    if (!CVState.isFirstDerivativeOperator) {
                        firstDerivativeOperatorSelectedOption = "Null"
                    } else {
                        CVState.isSecondDerivativeOperator = false
                        CVState.isCannyOperator = false
                        clearCannyParams()
                        clearDoGParams()
                    }
                })
                Text(i18nState.getString("first_derivative_operator"), modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                firstDerivativeOperatorTags.forEach {
                    RadioButton(
                        selected = (CVState.isFirstDerivativeOperator && it == firstDerivativeOperatorSelectedOption),
                        onClick = {
                            firstDerivativeOperatorSelectedOption = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }

                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center) {
                    Button(
                        onClick = experimentViewClick(state) {
                            if (firstDerivativeOperatorSelectedOption == "Null") {
                                val errorMsg = i18nState.getString("please_select_first_derivative_operator")
                                showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                                return@experimentViewClick
                            }

                            when(firstDerivativeOperatorSelectedOption) {
                                firstDerivativeOperatorTags[0] -> viewModel.roberts(state)
                                firstDerivativeOperatorTags[1] -> viewModel.prewitt(state)
                                firstDerivativeOperatorTags[2] -> viewModel.sobel(state)
                                else         -> {}
                            }
                        }
                    ) {
                        Text(text = i18nState.getString("first_derivative_edge_detection"), color = Color.Unspecified)
                    }
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp),verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isSecondDerivativeOperator, onCheckedChange = {
                    CVState.isSecondDerivativeOperator = it

                    if (!CVState.isSecondDerivativeOperator) {
                        secondDerivativeOperatorSelectedOption = "Null"
                    } else {
                        CVState.isFirstDerivativeOperator = false
                        CVState.isCannyOperator = false
                        clearCannyParams()
                    }
                })
                Text(i18nState.getString("second_derivative_operator"), modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                secondDerivativeOperatorTags.forEach {
                    RadioButton(
                        selected = (CVState.isSecondDerivativeOperator && it == secondDerivativeOperatorSelectedOption),
                        onClick = {
                            secondDerivativeOperatorSelectedOption = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }

                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center) {
                    Button(
                        onClick = experimentViewClick(state) {
                            if (secondDerivativeOperatorSelectedOption == "Null") {
                                val errorMsg = i18nState.getString("please_select_second_derivative_operator")
                                showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                                return@experimentViewClick
                            }

                            when(secondDerivativeOperatorSelectedOption) {
                                secondDerivativeOperatorTags[0] -> viewModel.laplace(state)
                                secondDerivativeOperatorTags[1] -> viewModel.log(state)
                                secondDerivativeOperatorTags[2] -> {
                                    val sigma1 = getValidateField(block = { sigma1Text.toDouble() } , failed = { 
                                        val errorMsg = i18nState.getString("sigma1_needs_double")
                                        showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                                    }) ?: return@experimentViewClick
                                    val sigma2 = getValidateField(block = { sigma2Text.toDouble() } , failed = { 
                                        val errorMsg = i18nState.getString("sigma2_needs_double")
                                        showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                                    }) ?: return@experimentViewClick
                                    val size = getValidateField(block = { sizeText.toInt() } , failed = { 
                                        val errorMsg = i18nState.getString("size_needs_int")
                                        showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                                    }) ?: return@experimentViewClick
                                    viewModel.dog(state, sigma1, sigma2, size)
                                }
                                else         -> {}
                            }
                        }
                    ) {
                        Text(text = i18nState.getString("second_derivative_edge_detection"), color = Color.Unspecified)
                    }
                }
            }

            if (CVState.isSecondDerivativeOperator && secondDerivativeOperatorSelectedOption == secondDerivativeOperatorTags[2]) {
                Row {
                    basicTextFieldWithTitle(titleText = "sigma1", sigma1Text) { str ->
                        if (CVState.isSecondDerivativeOperator) {
                            sigma1Text = str
                        }
                    }

                    basicTextFieldWithTitle(titleText = "sigma2", sigma2Text) { str ->
                        if (CVState.isSecondDerivativeOperator) {
                            sigma2Text = str
                        }
                    }

                    basicTextFieldWithTitle(titleText = "size", sizeText) { str ->
                        if (CVState.isSecondDerivativeOperator) {
                            sizeText = str
                        }
                    }
                }
            }

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
                Text(i18nState.getString("canny_operator"), modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "threshold1", threshold1Text) { str ->
                    threshold1Text = str
                }

                basicTextFieldWithTitle(titleText = "threshold2", threshold2Text) { str ->
                    threshold2Text = str
                }

                basicTextFieldWithTitle(titleText = "apertureSize", apertureSizeText) { str ->
                    apertureSizeText = str
                }
            }

            Button(
                modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
                onClick = experimentViewClick(state) {
                    if(state.currentImage?.type != BufferedImage.TYPE_BYTE_BINARY) {
                                        val threshold1 = getValidateField(block = { threshold1Text.toDouble() }, failed = { 
                                            val errorMsg = i18nState.getString("threshold1_needs_double")
                                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                                        }) ?: return@experimentViewClick
                val threshold2 = getValidateField(block = { threshold2Text.toDouble() }, failed = { 
                    val errorMsg = i18nState.getString("threshold2_needs_double")
                    showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                }) ?: return@experimentViewClick
                val apertureSize = getValidateField(block = { apertureSizeText.toInt() }, failed = { 
                    val errorMsg = i18nState.getString("aperture_size_needs_int")
                    showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                }) ?: return@experimentViewClick

                        viewModel.canny(state, threshold1, threshold2, apertureSize)
                    }
                }
            ) {
                Text(text = i18nState.getString("canny_edge_detection_full"), color = Color.Unspecified)
            }
        }
    }
}