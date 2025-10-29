package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.domain.ContourDisplaySettings
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.domain.ContourFilterSettings
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ContourAnalysisViewModel
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
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ContourAnalysisView
 * @author: Tony Shen
 * @date: 2024/10/25 23:52
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

var contourFilterSettings:ContourFilterSettings = ContourFilterSettings()
var contourDisplaySettings:ContourDisplaySettings = ContourDisplaySettings()

@Composable
fun contourAnalysis(state: ApplicationState, title: String) {
    val i18nState = rememberI18nState()
    val viewModel: ContourAnalysisViewModel = koinInject()

    var minPerimeterText by remember { mutableStateOf("") }
    var maxPerimeterText by remember { mutableStateOf("") }

    var minAreaText by remember { mutableStateOf("") }
    var maxAreaText by remember { mutableStateOf("") }

    var minRoundnessText by remember { mutableStateOf("") }
    var maxRoundnessText by remember { mutableStateOf("") }

    var minAspectRatioText by remember { mutableStateOf("") }
    var maxAspectRatioText by remember { mutableStateOf("") }

    fun clearContourPerimeterParams() {
        minPerimeterText = ""
        maxPerimeterText = ""

        contourFilterSettings.minPerimeter = 0.0
        contourFilterSettings.maxPerimeter = 0.0
    }

    fun clearContourAreaParams() {
        minAreaText = ""
        maxAreaText = ""

        contourFilterSettings.minArea = 0.0
        contourFilterSettings.maxArea = 0.0
    }

    fun clearContourRoundnessParams() {
        minRoundnessText = ""
        maxRoundnessText = ""

        contourFilterSettings.minRoundness = 0.0
        contourFilterSettings.maxRoundness = 0.0
    }

    fun clearContourAspectRatioParams() {
        minAspectRatioText = ""
        maxAspectRatioText = ""

        contourFilterSettings.minAspectRatio = 0.0
        contourFilterSettings.maxAspectRatio = 0.0
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = title, color = Color.Black)

        Column{
            subTitleWithDivider(text = i18nState.getString("filter_settings"), color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle(i18nState.getString("perimeter"), Modifier.padding(end = 50.dp), checked = CVState.isContourPerimeter, onCheckedChange = {
                    CVState.isContourPerimeter = it

                    if (!CVState.isContourPerimeter) {
                        clearContourPerimeterParams()
                    }
                })

                basicTextFieldWithTitle(titleText = i18nState.getString("min_value"), minPerimeterText) { str ->
                    if (CVState.isContourPerimeter) {
                        minPerimeterText = str

                        contourFilterSettings.minPerimeter = getValidateField(block = { minPerimeterText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("perimeter_min_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("max_value"), maxPerimeterText) { str ->
                    if (CVState.isContourPerimeter) {
                        maxPerimeterText = str

                        contourFilterSettings.maxPerimeter = getValidateField(block = { maxPerimeterText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("perimeter_max_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle(i18nState.getString("area"), Modifier.padding(end = 50.dp), checked = CVState.isContourArea, onCheckedChange = {
                    CVState.isContourArea = it

                    if (!CVState.isContourArea) {
                        clearContourAreaParams()
                    }
                })

                basicTextFieldWithTitle(titleText = i18nState.getString("min_value"), minAreaText) { str ->
                    if (CVState.isContourArea) {
                        minAreaText = str

                        contourFilterSettings.minArea = getValidateField(block = { minAreaText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("area_min_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("max_value"), maxAreaText) { str ->
                    if (CVState.isContourArea) {
                        maxAreaText = str

                        contourFilterSettings.maxArea = getValidateField(block = { maxAreaText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("area_max_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle(i18nState.getString("roundness"), Modifier.padding(end = 50.dp), checked = CVState.isContourRoundness, onCheckedChange = {
                    CVState.isContourRoundness = it

                    if (!CVState.isContourRoundness) {
                        clearContourRoundnessParams()
                    }
                })

                basicTextFieldWithTitle(titleText = i18nState.getString("min_value"), minRoundnessText) { str ->
                    if (CVState.isContourRoundness) {
                        minRoundnessText = str

                        contourFilterSettings.minRoundness = getValidateField(block = { minRoundnessText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("roundness_min_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("max_value"), maxRoundnessText) { str ->
                    if (CVState.isContourRoundness) {
                        maxRoundnessText = str

                        contourFilterSettings.maxRoundness = getValidateField(block = { maxRoundnessText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("roundness_max_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle(i18nState.getString("aspect_ratio"), Modifier.padding(end = 35.dp), checked = CVState.isContourAspectRatio, onCheckedChange = {
                    CVState.isContourAspectRatio = it

                    if (!CVState.isContourAspectRatio) {
                        clearContourAspectRatioParams()
                    }
                })

                basicTextFieldWithTitle(titleText = i18nState.getString("min_value"), minAspectRatioText) { str ->
                    if (CVState.isContourAspectRatio) {
                        minAspectRatioText = str

                        contourFilterSettings.minAspectRatio = getValidateField(block = { minAspectRatioText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("aspect_ratio_min_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("max_value"), maxAspectRatioText) { str ->
                    if (CVState.isContourAspectRatio) {
                        maxAspectRatioText = str

                        contourFilterSettings.maxAspectRatio = getValidateField(block = { maxAspectRatioText.toDouble() } , failed = { 
                            val errorMsg = i18nState.getString("aspect_ratio_max_needs_double")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                        }) ?: return@basicTextFieldWithTitle
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("contour_display_settings"), color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle(i18nState.getString("show_original_image"), Modifier.padding(end = 50.dp), checked = CVState.showOriginalImage, onCheckedChange = {
                    contourDisplaySettings.showOriginalImage = it
                    CVState.showOriginalImage = it
                })

                checkBoxWithTitle(i18nState.getString("show_bounding_rect"), Modifier.padding(end = 50.dp), checked = CVState.showBoundingRect, onCheckedChange = {
                    contourDisplaySettings.showBoundingRect = it
                    CVState.showBoundingRect = it
                })

                checkBoxWithTitle(i18nState.getString("show_min_area_rect"),Modifier.padding(end = 50.dp), checked = CVState.showMinAreaRect, onCheckedChange = {
                    contourDisplaySettings.showMinAreaRect = it
                    CVState.showMinAreaRect = it
                })

                checkBoxWithTitle(i18nState.getString("show_center"),Modifier.padding(end = 50.dp), checked = CVState.showCenter, onCheckedChange = {
                    contourDisplaySettings.showCenter = it
                    CVState.showCenter = it
                })
            }
        }

        Button(
            modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
            onClick = experimentViewClick(state) {

                if(state.currentImage?.type == BufferedImage.TYPE_BYTE_BINARY) {
                    if (CVState.isContourPerimeter) {
                        if (contourFilterSettings.minPerimeter == 0.0 && contourFilterSettings.maxPerimeter == 0.0) {
                            val errorMsg = i18nState.getString("perimeter_at_least_one_value")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                            return@experimentViewClick
                        }
                    }

                    if (CVState.isContourArea) {
                        if (contourFilterSettings.minArea == 0.0 && contourFilterSettings.maxArea == 0.0) {
                            val errorMsg = i18nState.getString("area_at_least_one_value")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                            return@experimentViewClick
                        }
                    }

                    if (CVState.isContourRoundness) {
                        if (contourFilterSettings.minRoundness == 0.0 && contourFilterSettings.maxRoundness == 0.0) {
                            val errorMsg = i18nState.getString("roundness_at_least_one_value")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                            return@experimentViewClick
                        }
                    }

                    if (CVState.isContourAspectRatio) {
                        if (contourFilterSettings.minAspectRatio == 0.0 && contourFilterSettings.maxAspectRatio == 0.0) {
                            val errorMsg = i18nState.getString("aspect_ratio_at_least_one_value")
                            showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                            return@experimentViewClick
                        }
                    }

                    viewModel.contourAnalysis(state, contourFilterSettings, contourDisplaySettings)
                } else {
                    val errorMsg = i18nState.getString("please_binarize_image_first_for_contour")
                    showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                }
            }
        ) {
            Text(text = i18nState.getString("contour_analysis"), color = Color.Unspecified)
        }
    }
}