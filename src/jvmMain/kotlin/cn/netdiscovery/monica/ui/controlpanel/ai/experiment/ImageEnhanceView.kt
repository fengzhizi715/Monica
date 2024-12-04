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

                    val clipLimit = getValidateField(block = { clipLimitText.toDouble() } , failed = { experimentViewVerifyToast("clipLimit 需要 double 类型") }) ?: return@experimentViewClick
                    val size = getValidateField(block = { sizeText.toInt() } , failed = { experimentViewVerifyToast("size 需要 int 类型") }) ?: return@experimentViewClick
                    viewModel.clahe(state, clipLimit, size)
                }
            ) {
                Text(text = "clahe", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "gamma 变换", color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "gamma", gammaText) { str ->
                    gammaText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    val gamma = getValidateField(block = { gammaText.toFloat() } , failed = { experimentViewVerifyToast("gamma 需要 float 类型") }) ?: return@experimentViewClick
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

                    val radius = getValidateField(block = { radiusText.toInt() } , failed = { experimentViewVerifyToast("Radius 需要 int 类型") }) ?: return@experimentViewClick
                    val threshold = getValidateField(block = { thresholdText.toInt() } , failed = { experimentViewVerifyToast("Threshold 需要 int 类型") }) ?: return@experimentViewClick
                    val amount = getValidateField(block = { amountText.toInt() } , failed = { experimentViewVerifyToast("Amount 需要 int 类型") }) ?: return@experimentViewClick
                    viewModel.unsharpMask(state, radius, threshold, amount)
                }
            ) {
                Text(text = "USM 锐化", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "自动色彩均衡", color = Color.Black)

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

                    val ratio = getValidateField(block = { ratioText.toInt() } , failed = { experimentViewVerifyToast("Ratio 需要 int 类型") }) ?: return@experimentViewClick
                    val radius = getValidateField(block = { aceRadiusText.toInt() } , failed = { experimentViewVerifyToast("Radius 需要 int 类型") }) ?: return@experimentViewClick
                    viewModel.ace(state, ratio, radius)
                }
            ) {
                Text(text = "自动色彩均衡", color = Color.Unspecified)
            }
        }
    }
}