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
import cn.netdiscovery.monica.ui.widget.*
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

    var minPerimeterText = remember { mutableStateOf("") }
    var maxPerimeterText = remember { mutableStateOf("") }

    var minAreaText = remember { mutableStateOf("") }
    var maxAreaText = remember { mutableStateOf("") }

    var minRoundnessText = remember { mutableStateOf("") }
    var maxRoundnessText = remember { mutableStateOf("") }

    var minAspectRatioText = remember { mutableStateOf("") }
    var maxAspectRatioText = remember { mutableStateOf("") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = "轮廓分析", color = Color.Black)

        Column{
            subTitleWithDivider(text = "过滤设置", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isContourPerimeter, onCheckedChange = {
                    CVState.isContourPerimeter = it

                })

                Text("周长", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Text(text = "最小值")

                basicTextField(minPerimeterText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma1Text.value = str
//                    }
                }

                Text(text = "最大值")

                basicTextField(maxPerimeterText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma2Text.value = str
//                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isThreshType, onCheckedChange = {
//                    state.isThreshType = it
//
//                    if (!state.isThreshType) {
//
//                    } else {
//
//                    }
                })

                Text("面积", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Text(text = "最小值")

                basicTextField(minAreaText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma1Text.value = str
//                    }
                }

                Text(text = "最大值")

                basicTextField(maxAreaText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma2Text.value = str
//                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isThreshType, onCheckedChange = {
//                    state.isThreshType = it
//
//                    if (!state.isThreshType) {
//
//                    } else {
//
//                    }
                })

                Text("圆度", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Text(text = "最小值")

                basicTextField(minRoundnessText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma1Text.value = str
//                    }
                }

                Text(text = "最大值")

                basicTextField(maxRoundnessText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma2Text.value = str
//                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isThreshType, onCheckedChange = {
//                    state.isThreshType = it
//
//                    if (!state.isThreshType) {
//
//                    } else {
//
//                    }
                })

                Text("长宽比", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 35.dp))

                Text(text = "最小值")

                basicTextField(minAspectRatioText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma1Text.value = str
//                    }
                }

                Text(text = "最大值")

                basicTextField(maxAspectRatioText.value) { str ->
//                    if (state.isSecondDerivativeOperator) {
//                        sigma2Text.value = str
//                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "显示设置", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(CVState.isThreshType, onCheckedChange = {
//                    state.isThreshType = it
//
//                    if (!state.isThreshType) {
//
//                    } else {
//
//                    }
                })

                Text("外接矩形", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))

                Checkbox(CVState.isThreshType, onCheckedChange = {
//                    state.isThreshType = it
//
//                    if (!state.isThreshType) {
//
//                    } else {
//
//                    }
                })

                Text("最小外接矩形", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))
            }
        }
    }
}