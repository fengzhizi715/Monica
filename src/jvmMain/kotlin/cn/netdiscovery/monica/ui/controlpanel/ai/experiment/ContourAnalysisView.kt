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
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.ContourDisplaySettings
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.ContourFilterSettings
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ContourAnalysisViewModel
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.getValidateField
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
            subTitleWithDivider(text = "过滤设置", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("周长", Modifier.padding(end = 50.dp), checked = CVState.isContourPerimeter, onCheckedChange = {
                    CVState.isContourPerimeter = it

                    if (!CVState.isContourPerimeter) {
                        clearContourPerimeterParams()
                    }
                })

                basicTextFieldWithTitle(titleText = "最小值", minPerimeterText) { str ->
                    if (CVState.isContourPerimeter) {
                        minPerimeterText = str

                        contourFilterSettings.minPerimeter = getValidateField(block = { minPerimeterText.toDouble() } , failed = { experimentViewVerifyToast("周长最小值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxPerimeterText) { str ->
                    if (CVState.isContourPerimeter) {
                        maxPerimeterText = str

                        contourFilterSettings.maxPerimeter = getValidateField(block = { maxPerimeterText.toDouble() } , failed = { experimentViewVerifyToast("周长最大值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
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

                basicTextFieldWithTitle(titleText = "最小值", minAreaText) { str ->
                    if (CVState.isContourArea) {
                        minAreaText = str

                        contourFilterSettings.minArea = getValidateField(block = { minAreaText.toDouble() } , failed = { experimentViewVerifyToast("面积最小值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxAreaText) { str ->
                    if (CVState.isContourArea) {
                        maxAreaText = str

                        contourFilterSettings.maxArea = getValidateField(block = { maxAreaText.toDouble() } , failed = { experimentViewVerifyToast("面积最大值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
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

                basicTextFieldWithTitle(titleText = "最小值", minRoundnessText) { str ->
                    if (CVState.isContourRoundness) {
                        minRoundnessText = str

                        contourFilterSettings.minRoundness = getValidateField(block = { minRoundnessText.toDouble() } , failed = { experimentViewVerifyToast("圆度最小值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxRoundnessText) { str ->
                    if (CVState.isContourRoundness) {
                        maxRoundnessText = str

                        contourFilterSettings.maxRoundness = getValidateField(block = { maxRoundnessText.toDouble() } , failed = { experimentViewVerifyToast("圆度最大值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
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

                basicTextFieldWithTitle(titleText = "最小值", minAspectRatioText) { str ->
                    if (CVState.isContourAspectRatio) {
                        minAspectRatioText = str

                        contourFilterSettings.minAspectRatio = getValidateField(block = { minAspectRatioText.toDouble() } , failed = { experimentViewVerifyToast("长宽比最小值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
                    }
                }

                basicTextFieldWithTitle(titleText = "最大值", maxAspectRatioText) { str ->
                    if (CVState.isContourAspectRatio) {
                        maxAspectRatioText = str

                        contourFilterSettings.maxAspectRatio = getValidateField(block = { maxAspectRatioText.toDouble() } , failed = { experimentViewVerifyToast("长宽比最大值需要 double 类型") })
                            ?: return@basicTextFieldWithTitle
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "显示设置", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("原图显示", Modifier.padding(end = 50.dp), checked = CVState.showOriginalImage, onCheckedChange = {
                    contourDisplaySettings.showOriginalImage = it
                    CVState.showOriginalImage = it
                })

                checkBoxWithTitle("外接矩形", Modifier.padding(end = 50.dp), checked = CVState.showBoundingRect, onCheckedChange = {
                    contourDisplaySettings.showBoundingRect = it
                    CVState.showBoundingRect = it
                })

                checkBoxWithTitle("最小外接矩形",Modifier.padding(end = 50.dp), checked = CVState.showMinAreaRect, onCheckedChange = {
                    contourDisplaySettings.showMinAreaRect = it
                    CVState.showMinAreaRect = it
                })

                checkBoxWithTitle("质心",Modifier.padding(end = 50.dp), checked = CVState.showCenter, onCheckedChange = {
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
                            experimentViewVerifyToast("周长至少输入一个最小值或最大值")
                            return@experimentViewClick
                        }
                    }

                    if (CVState.isContourArea) {
                        if (contourFilterSettings.minArea == 0.0 && contourFilterSettings.maxArea == 0.0) {
                            experimentViewVerifyToast("面积至少输入一个最小值或最大值")
                            return@experimentViewClick
                        }
                    }

                    if (CVState.isContourRoundness) {
                        if (contourFilterSettings.minRoundness == 0.0 && contourFilterSettings.maxRoundness == 0.0) {
                            experimentViewVerifyToast("圆度至少输入一个最小值或最大值")
                            return@experimentViewClick
                        }
                    }

                    if (CVState.isContourAspectRatio) {
                        if (contourFilterSettings.minAspectRatio == 0.0 && contourFilterSettings.maxAspectRatio == 0.0) {
                            experimentViewVerifyToast("长宽比至少输入一个最小值或最大值")
                            return@experimentViewClick
                        }
                    }

                    viewModel.contourAnalysis(state, contourFilterSettings, contourDisplaySettings)
                } else {
                    experimentViewVerifyToast("请先将当前图像进行二值化")
                }
            }
        ) {
            Text(text = "轮廓分析", color = Color.Unspecified)
        }
    }
}