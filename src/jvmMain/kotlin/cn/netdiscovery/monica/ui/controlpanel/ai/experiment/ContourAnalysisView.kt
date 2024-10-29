package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
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

    fun clearContourPerimeterParams() {
        minPerimeterText.value = ""
        maxPerimeterText.value = ""
    }

    fun clearContourAreaParams() {
        minAreaText.value = ""
        maxAreaText.value = ""
    }

    fun clearContourRoundnessParams() {
        minRoundnessText.value = ""
        maxRoundnessText.value = ""
    }

    fun clearContourAspectRatioParams() {
        minAspectRatioText.value = ""
        maxAspectRatioText.value = ""
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = "轮廓分析", color = Color.Black)

        Column{
            subTitleWithDivider(text = "过滤设置", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("周长", Modifier.padding(end = 50.dp), checked = CVState.isContourPerimeter, onCheckedChange = {
                    CVState.isContourPerimeter = it

                    if (!CVState.isContourPerimeter) {
                        clearContourPerimeterParams()
                    }
                })

                basicTextFieldWithTitle(titleText = "最小值", minPerimeterText.value) { str ->
                    if (CVState.isContourPerimeter) {
                        minPerimeterText.value = str
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxPerimeterText.value) { str ->
                    if (CVState.isContourPerimeter) {
                        maxPerimeterText.value = str
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("面积", Modifier.padding(end = 50.dp), checked = CVState.isContourArea, onCheckedChange = {
                    CVState.isContourArea = it

                    if (!CVState.isContourArea) {
                        clearContourAreaParams()
                    }
                })

                basicTextFieldWithTitle(titleText = "最小值", minAreaText.value) { str ->
                    if (CVState.isContourArea) {
                        minAreaText.value = str
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxAreaText.value) { str ->
                    if (CVState.isContourArea) {
                        maxAreaText.value = str
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("圆度", Modifier.padding(end = 50.dp), checked = CVState.isContourRoundness, onCheckedChange = {
                    CVState.isContourRoundness = it

                    if (!CVState.isContourRoundness) {
                        clearContourRoundnessParams()
                    }
                })

                basicTextFieldWithTitle(titleText = "最小值", minRoundnessText.value) { str ->
                    if (CVState.isContourRoundness) {
                        minRoundnessText.value = str
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxRoundnessText.value) { str ->
                    if (CVState.isContourRoundness) {
                        maxRoundnessText.value = str
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("长宽比", Modifier.padding(end = 35.dp), checked = CVState.isContourAspectRatio, onCheckedChange = {
                    CVState.isContourAspectRatio = it

                    if (!CVState.isContourAspectRatio) {
                        clearContourAspectRatioParams()
                    }
                })

                basicTextFieldWithTitle(titleText = "最小值", minAspectRatioText.value) { str ->
                    if (CVState.isContourAspectRatio) {
                        minAspectRatioText.value = str
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxAspectRatioText.value) { str ->
                    if (CVState.isContourAspectRatio) {
                        maxAspectRatioText.value = str
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "显示设置", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("原图显示", Modifier.padding(end = 50.dp), checked = true, onCheckedChange = {

                })

                checkBoxWithTitle("外接矩形", Modifier.padding(end = 50.dp), checked = true, onCheckedChange = {

                })

                checkBoxWithTitle("最小外接矩形",Modifier.padding(end = 50.dp), checked =true, onCheckedChange = {
                })
            }
        }

        Button(
            modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
            onClick = experimentViewClick(state) {
                viewModel.findContours(state)
            }
        ) {
            Text(text = "轮廓分析", color = Color.Unspecified)
        }
    }
}