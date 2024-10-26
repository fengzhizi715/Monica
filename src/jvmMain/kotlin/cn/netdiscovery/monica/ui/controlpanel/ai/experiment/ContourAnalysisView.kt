package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.basicTextField
import cn.netdiscovery.monica.ui.widget.divider
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.ui.widget.title
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ContourAnalysisView
 * @author: Tony Shen
 * @date: 2024/10/25 23:52
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun contourAnalysis(state: ApplicationState) {
    val viewModel: ContourAnalysisViewModel = koinInject()

    var sigma1Text = remember { mutableStateOf("") }
    var sigma2Text = remember { mutableStateOf("") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = "轮廓分析", color = Color.Black)

        Column{
            subTitle(text = "过滤设置", color = Color.Black)
            divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {

                    } else {

                    }
                })

                Text("周长", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Text(text = "最小值")

                basicTextField(sigma1Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma1Text.value = str
                    }
                }

                Text(text = "最大值")

                basicTextField(sigma2Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma2Text.value = str
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {

                    } else {

                    }
                })

                Text("面积", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Text(text = "最小值")

                basicTextField(sigma1Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma1Text.value = str
                    }
                }

                Text(text = "最大值")

                basicTextField(sigma2Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma2Text.value = str
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {

                    } else {

                    }
                })

                Text("圆度", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Text(text = "最小值")

                basicTextField(sigma1Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma1Text.value = str
                    }
                }

                Text(text = "最大值")

                basicTextField(sigma2Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma2Text.value = str
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {

                    } else {

                    }
                })

                Text("长宽比", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 35.dp))

                Text(text = "最小值")

                basicTextField(sigma1Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma1Text.value = str
                    }
                }

                Text(text = "最大值")

                basicTextField(sigma2Text.value) { str ->
                    if (state.isSecondDerivativeOperator) {
                        sigma2Text.value = str
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitle(text = "显示设置", color = Color.Black)
            divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {

                    } else {

                    }
                })

                Text("外接矩形", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {

                    } else {

                    }
                })

                Text("最小外接矩形", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))
            }
        }
    }
}