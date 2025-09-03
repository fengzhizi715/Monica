package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ImageEnhanceViewModel
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
import cn.netdiscovery.monica.utils.getValidateField
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ImageEnhanceView
 * @author: Tony Shen
 * @date: 2024/12/3 19:44
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)


@Composable
fun imageEnhance(state: ApplicationState, title: String) {
    val i18nState = rememberI18nState()
    val viewModel: ImageEnhanceViewModel = koinInject()

    var clipLimitText by remember { mutableStateOf("4") }
    var sizeText by remember { mutableStateOf("10") }

    var gammaText by remember { mutableStateOf("1.0") }

    var amountText by remember { mutableStateOf("25") }
    var thresholdText by remember { mutableStateOf("0") }
    var radiusText by remember { mutableStateOf("50") }

    var ratioText by remember { mutableStateOf("4") }
    var aceRadiusText by remember { mutableStateOf("1") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = i18nState.getString("histogram_equalization"), color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    viewModel.equalizeHist(state)
                }
            ) {
                Text(text = i18nState.getString("histogram_equalization_button"), color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("clahe"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "clipLimit", clipLimitText) { str ->
                    clipLimitText = str
                }

                basicTextFieldWithTitle(titleText = "size", sizeText) { str ->
                    sizeText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val clipLimit = getValidateField(block = { clipLimitText.toDouble() } , failed = { experimentViewVerifyToast(i18nState.getString("clip_limit_needs_double")) }) ?: return@experimentViewClick
                    val size = getValidateField(block = { sizeText.toInt() } , failed = { experimentViewVerifyToast(i18nState.getString("size_needs_int_for_enhance")) }) ?: return@experimentViewClick
                    viewModel.clahe(state, clipLimit, size)
                }
            ) {
                Text(text = i18nState.getString("clahe_button"), color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("gamma_transform"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "gamma", gammaText) { str ->
                    gammaText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val gamma = getValidateField(block = { gammaText.toFloat() } , failed = { experimentViewVerifyToast(i18nState.getString("gamma_needs_float")) }) ?: return@experimentViewClick
                    viewModel.gammaCorrection(state, gamma)
                }
            ) {
                Text(text = i18nState.getString("gamma_transform_button"), color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("laplace_sharpening"), color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    viewModel.laplaceSharpening(state)
                }
            ) {
                Text(text = i18nState.getString("laplace_sharpen_button"), color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("usm_sharpening"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "Radius", radiusText) { str ->
                    radiusText = str
                }

                basicTextFieldWithTitle(titleText = "Threshold", thresholdText) { str ->
                    thresholdText = str
                }

                basicTextFieldWithTitle(titleText = "Amount", amountText) { str ->
                    amountText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val radius = getValidateField(block = { radiusText.toInt() } , failed = { experimentViewVerifyToast(i18nState.getString("radius_needs_int")) }) ?: return@experimentViewClick
                    val threshold = getValidateField(block = { thresholdText.toInt() } , failed = { experimentViewVerifyToast(i18nState.getString("threshold_needs_int")) }) ?: return@experimentViewClick
                    val amount = getValidateField(block = { amountText.toInt() } , failed = { experimentViewVerifyToast(i18nState.getString("amount_needs_int")) }) ?: return@experimentViewClick
                    viewModel.unsharpMask(state, radius, threshold, amount)
                }
            ) {
                Text(text = i18nState.getString("usm_sharpen_button"), color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("automatic_color_balance"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "Ratio", ratioText) { str ->
                    ratioText = str
                }

                basicTextFieldWithTitle(titleText = "Radius", aceRadiusText) { str ->
                    aceRadiusText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val ratio = getValidateField(block = { ratioText.toInt() } , failed = { experimentViewVerifyToast(i18nState.getString("ratio_needs_int")) }) ?: return@experimentViewClick
                    val radius = getValidateField(block = { aceRadiusText.toInt() } , failed = { experimentViewVerifyToast(i18nState.getString("radius_needs_int")) }) ?: return@experimentViewClick
                    viewModel.ace(state, ratio, radius)
                }
            ) {
                Text(text = i18nState.getString("auto_color_balance_button"), color = Color.Unspecified)
            }
        }
    }
}