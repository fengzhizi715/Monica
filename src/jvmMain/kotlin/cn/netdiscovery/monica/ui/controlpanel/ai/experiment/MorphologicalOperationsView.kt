package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.domain.MorphologicalOperationSettings
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.i18n.LocalizationManager
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MorphologicalOperationsViewModel
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
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
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MorphologicalOperationsView
 * @author: Tony Shen
 * @date: 2024/12/21 20:16
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val operatingElementsTag = arrayListOf(
    LocalizationManager.getString("erosion"),
    LocalizationManager.getString("dilation"),
    LocalizationManager.getString("opening"),
    LocalizationManager.getString("closing"),
    LocalizationManager.getString("morphological_gradient"),
    LocalizationManager.getString("top_hat"),
    LocalizationManager.getString("black_hat"),
    LocalizationManager.getString("hit_miss")
)
val structuralElementsTag = arrayListOf(
    LocalizationManager.getString("rectangle"),
    LocalizationManager.getString("cross"),
    LocalizationManager.getString("ellipse")
)
val tagList1 = operatingElementsTag.take(4)
val tagList2 = operatingElementsTag.takeLast(4)

var morphologicalOperationSettings: MorphologicalOperationSettings = MorphologicalOperationSettings()

@Composable
fun morphologicalOperations(state: ApplicationState, title: String) {
    val i18nState = rememberI18nState()
    val viewModel: MorphologicalOperationsViewModel = koinInject()

    var operatingElementOption by remember { mutableStateOf("Null") }
    var structuralElementOption by remember { mutableStateOf(LocalizationManager.getString("rectangle")) }

    var widthText by remember { mutableStateOf("3") }
    var heightText by remember { mutableStateOf("3") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = i18nState.getString("operation_element"), color = Color.Black)

            Row {
                tagList1.forEach {
                    RadioButton(
                        selected = (it == operatingElementOption),
                        onClick = {
                            operatingElementOption = it
                            val index = operatingElementsTag.indexOf(it)
                            morphologicalOperationSettings = morphologicalOperationSettings.copy(op = index)
                        }
                    )
                    Text(text = it, modifier = Modifier.width(120.dp).align(Alignment.CenterVertically))
                }
            }

            Row {
                tagList2.forEach {

                    RadioButton(
                        selected = (it == operatingElementOption),
                        onClick = {
                            operatingElementOption = it
                            val index = operatingElementsTag.indexOf(it)
                            morphologicalOperationSettings = morphologicalOperationSettings.copy(op = index)
                        }
                    )
                    Text(text = it, modifier = Modifier.width(120.dp).align(Alignment.CenterVertically))
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("structural_element"), color = Color.Black)

            Row {
                structuralElementsTag.forEach {

                    RadioButton(
                        selected = (it == structuralElementOption),
                        onClick = {
                            structuralElementOption = it
                            val index = structuralElementsTag.indexOf(it)
                            morphologicalOperationSettings = morphologicalOperationSettings.copy(shape = index)
                        }
                    )
                    Text(text = it, modifier = Modifier.width(120.dp).align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(modifier = Modifier.width(70.dp), text = i18nState.getString("structural_element") + "：", color = Color.Unspecified)

                basicTextFieldWithTitle(titleText = i18nState.getString("width"), widthText) { str ->
                    widthText = str
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("height"), heightText) { str ->
                    heightText = str
                }
            }
        }

        Button(
            modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
            onClick = experimentViewClick(state) {

                if(state.currentImage?.type == BufferedImage.TYPE_BYTE_BINARY) {
                    val width = getValidateField(block = { widthText.toInt() } , failed = { 
                        val errorMsg = i18nState.getString("width_needs_int_for_morph")
                        showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                    }) ?: return@experimentViewClick
                    val height = getValidateField(block = { heightText.toInt() } , failed = { 
                        val errorMsg = i18nState.getString("height_needs_int_for_morph")
                        showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                    }) ?: return@experimentViewClick

                    morphologicalOperationSettings = morphologicalOperationSettings.copy(width = width, height = height)

                    viewModel.morphologyEx(state, morphologicalOperationSettings)
                } else {
                    val errorMsg = i18nState.getString("please_binarize_image_first")
                    showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.MEDIUM, errorMsg, errorMsg)
                }
            }
        ) {
            Text(text = i18nState.getString("morphological_operations"), color = Color.Unspecified)
        }
    }
}