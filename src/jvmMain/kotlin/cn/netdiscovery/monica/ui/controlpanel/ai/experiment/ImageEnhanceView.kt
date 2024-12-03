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

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "直方图均衡化", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    if(state.currentImage!= null) {
                        viewModel.equalizeHist(state)
                    }
                }
            ) {
                Text(text = "直方图均衡化", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "限制对比度自适应直方图均衡化", color = Color.Black)

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

                    if(state.currentImage!= null) {
                        val clipLimit = getValidateField(block = { clipLimitText.value.toDouble() } , failed = { experimentViewVerifyToast("clipLimit 需要 double 类型") }) ?: return@experimentViewClick
                        val size = getValidateField(block = { sizeText.value.toInt() } , failed = { experimentViewVerifyToast("size 需要 int 类型") }) ?: return@experimentViewClick
                        viewModel.clahe(state, clipLimit, size)
                    }
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

                    if(state.currentImage!= null) {
                        val gamma = getValidateField(block = { gammaText.value.toFloat() } , failed = { experimentViewVerifyToast("gamma 需要 float 类型") }) ?: return@experimentViewClick
                        viewModel.gammaCorrection(state, gamma)
                    }
                }
            ) {
                Text(text = "gamma 变换", color = Color.Unspecified)
            }
        }
    }
}