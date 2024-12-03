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
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.confirmButton
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
    val viewModel: ImageEnhanceViewModel = koinInject()

    var clipLimitText = remember { mutableStateOf("4") }
    var sizeText = remember { mutableStateOf("10") }

    var gammaText = remember { mutableStateOf("1.0") }

    var amountText = remember { mutableStateOf("25") }
    var thresholdText = remember { mutableStateOf("0") }
    var radiusText = remember { mutableStateOf("50") }

    var ratioText = remember { mutableStateOf("4") }
    var aceRadiusText = remember { mutableStateOf("1") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "直方图均衡化", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    viewModel.equalizeHist(state)
                }
            ) {
                Text(text = "直方图均衡化", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "限制对比度自适应直方图均衡(clahe)", color = Color.Black)

            Row {
                basicTextFieldWithTitle(titleText = "clipLimit", clipLimitText.value) { str ->
                    clipLimitText.value = str
                }

                basicTextFieldWithTitle(titleText = "size", sizeText.value) { str ->
                    sizeText.value = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val clipLimit = getValidateField(block = { clipLimitText.value.toDouble() } , failed = { experimentViewVerifyToast("clipLimit 需要 double 类型") }) ?: return@experimentViewClick
                    val size = getValidateField(block = { sizeText.value.toInt() } , failed = { experimentViewVerifyToast("size 需要 int 类型") }) ?: return@experimentViewClick
                    viewModel.clahe(state, clipLimit, size)
                }
            ) {
                Text(text = "clahe", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "gamma 变换", color = Color.Black)

            Row{
                basicTextFieldWithTitle(titleText = "gamma", gammaText.value) { str ->
                    gammaText.value = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val gamma = getValidateField(block = { gammaText.value.toFloat() } , failed = { experimentViewVerifyToast("gamma 需要 float 类型") }) ?: return@experimentViewClick
                    viewModel.gammaCorrection(state, gamma)
                }
            ) {
                Text(text = "gamma 变换", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "Laplace 锐化", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    viewModel.laplaceSharpening(state)
                }
            ) {
                Text(text = "Laplace 锐化", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "USM 锐化", color = Color.Black)

            Row {
                basicTextFieldWithTitle(titleText = "Radius", radiusText.value) { str ->
                    radiusText.value = str
                }

                basicTextFieldWithTitle(titleText = "Threshold", thresholdText.value) { str ->
                    thresholdText.value = str
                }

                basicTextFieldWithTitle(titleText = "Amount", amountText.value) { str ->
                    amountText.value = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val radius = getValidateField(block = { radiusText.value.toInt() } , failed = { experimentViewVerifyToast("Radius 需要 int 类型") }) ?: return@experimentViewClick
                    val threshold = getValidateField(block = { thresholdText.value.toInt() } , failed = { experimentViewVerifyToast("Threshold 需要 int 类型") }) ?: return@experimentViewClick
                    val amount = getValidateField(block = { amountText.value.toInt() } , failed = { experimentViewVerifyToast("Amount 需要 int 类型") }) ?: return@experimentViewClick
                    viewModel.unsharpMask(state, radius, threshold, amount)
                }
            ) {
                Text(text = "USM 锐化", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "自动色彩均衡", color = Color.Black)

            Row {
                basicTextFieldWithTitle(titleText = "Ratio", ratioText.value) { str ->
                    ratioText.value = str
                }

                basicTextFieldWithTitle(titleText = "Radius", aceRadiusText.value) { str ->
                    aceRadiusText.value = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val ratio = getValidateField(block = { ratioText.value.toInt() } , failed = { experimentViewVerifyToast("Ratio 需要 int 类型") }) ?: return@experimentViewClick
                    val radius = getValidateField(block = { aceRadiusText.value.toInt() } , failed = { experimentViewVerifyToast("Radius 需要 int 类型") }) ?: return@experimentViewClick
                    viewModel.ace(state, ratio, radius)
                }
            ) {
                Text(text = "自动色彩均衡", color = Color.Unspecified)
            }
        }
    }
}