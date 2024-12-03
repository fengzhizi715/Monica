package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.getValidateField
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.BinaryImageAnalysisView
 * @author: Tony Shen
 * @date:  2024/10/2 15:03
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val typeSelectTags = arrayListOf("THRESH_BINARY", "THRESH_BINARY_INV")
val thresholdSelectTags = arrayListOf("THRESH_OTSU", "THRESH_TRIANGLE")
val adaptiveMethodSelectTags = arrayListOf("ADAPTIVE_THRESH_MEAN_C", "ADAPTIVE_THRESH_GAUSSIAN_C")

@Composable
fun binaryImage(state: ApplicationState, title: String) {
    val viewModel: BinaryImageViewModel = koinInject()
    val edgeDetectionViewModel: EdgeDetectionViewModel = koinInject()

    var typeSelectedOption = remember { mutableStateOf("Null") }
    var thresholdSelectedOption = remember { mutableStateOf("Null") }
    var adaptiveMethodSelectedOption = remember { mutableStateOf("Null") }

    var blockSizeText = remember { mutableStateOf("") }
    var cText = remember { mutableStateOf("") }

    var threshold1Text = remember { mutableStateOf("") }
    var threshold2Text = remember { mutableStateOf("") }
    var apertureSizeText = remember { mutableStateOf("3") }

    var hminText = remember { mutableStateOf("") }
    var sminText = remember { mutableStateOf("") }
    var vminText = remember { mutableStateOf("") }
    var hmaxText = remember { mutableStateOf("") }
    var smaxText = remember { mutableStateOf("") }
    var vmaxText = remember { mutableStateOf("") }

    fun clearAdaptiveThreshParams() {
        blockSizeText.value = ""
        cText.value = ""
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "灰度图像", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                    if(state.currentImage!= null && state.currentImage?.type != BufferedImage.TYPE_BYTE_GRAY) {
                        viewModel.cvtGray(state)
                    }
                }
            ) {
                Text(text = "图像灰度化", color = Color.Unspecified)
            }
        }

        Column {
            subTitleWithDivider(text = "阈值分割", color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("阈值化类型", checked = CVState.isThreshType, onCheckedChange = {
                    CVState.isThreshType = it

                    if (!CVState.isThreshType) {
                        typeSelectedOption.value = "Null"
                        logger.info("取消了阈值化类型")
                    } else {
                        logger.info("勾选了阈值化类型")
                    }
                })
            }

            Row {
                typeSelectTags.forEach {
                    RadioButton(
                        selected = (CVState.isThreshType && it == typeSelectedOption.value),
                        onClick = {
                            typeSelectedOption.value = it
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("全局阈值分割", checked = CVState.isThreshSegment, onCheckedChange = {
                    CVState.isThreshSegment = it

                    if (!CVState.isThreshSegment) {
                        thresholdSelectedOption.value = "Null"
                        logger.info("取消了全局阈值分割")
                    } else {
                        CVState.isAdaptiveThresh = false
                        adaptiveMethodSelectedOption.value = "Null"
                        clearAdaptiveThreshParams()
                        logger.info("勾选了全局阈值分割")
                    }
                })
            }

            Row {
                thresholdSelectTags.forEach {
                    RadioButton(
                        selected = (CVState.isThreshSegment && it == thresholdSelectedOption.value),
                        onClick = {
                            thresholdSelectedOption.value = it
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                checkBoxWithTitle("自适应阈值分割", Modifier, checked = CVState.isAdaptiveThresh, onCheckedChange = {
                    CVState.isAdaptiveThresh = it

                    if (!CVState.isAdaptiveThresh) {
                        adaptiveMethodSelectedOption.value = "Null"
                        clearAdaptiveThreshParams()
                        logger.info("取消了自适应阈值分割")
                    } else {
                        CVState.isThreshSegment = false
                        thresholdSelectedOption.value = "Null"
                        logger.info("勾选了自适应阈值分割")
                    }
                })
            }

            Row {
                Text("自适应阈值算法", modifier = Modifier.align(Alignment.CenterVertically))

                adaptiveMethodSelectTags.forEach {
                    RadioButton(
                        selected = (CVState.isAdaptiveThresh && it == adaptiveMethodSelectedOption.value),
                        onClick = {
                            adaptiveMethodSelectedOption.value = it
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row {
                basicTextFieldWithTitle(titleText = "blockSize", blockSizeText.value) { str ->
                    if (CVState.isAdaptiveThresh) {
                        blockSizeText.value = str
                    }
                }

                basicTextFieldWithTitle(titleText = "c", cText.value) { str ->
                    if (CVState.isAdaptiveThresh) {
                        cText.value = str
                    }
                }
            }

            Button(
                modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
                onClick = experimentViewClick(state) {
                    if(state.currentImage?.type != BufferedImage.TYPE_BYTE_BINARY) {

                        if (CVState.isThreshType && CVState.isThreshSegment) {

                            if (typeSelectedOption.value == "Null") {
                                experimentViewVerifyToast("请选择阈值化类型")
                                return@experimentViewClick
                            }

                            if (thresholdSelectedOption.value == "Null") {
                                experimentViewVerifyToast("请选择全局阈值分割类型")
                                return@experimentViewClick
                            }

                            viewModel.threshold(state, typeSelectedOption.value, thresholdSelectedOption.value)
                        } else if (CVState.isThreshType && CVState.isAdaptiveThresh) {
                            if (typeSelectedOption.value == "Null") {
                                experimentViewVerifyToast("请选择阈值化类型")
                                return@experimentViewClick
                            }

                            if (adaptiveMethodSelectedOption.value == "Null") {
                                experimentViewVerifyToast("请选择自适应阈值算法类型")
                                return@experimentViewClick
                            }

                            val blockSize = getValidateField(block = { blockSizeText.value.toInt() } , failed = { experimentViewVerifyToast("blockSize 需要 int 类型") })?: return@experimentViewClick

                            val c = getValidateField(block = { cText.value.toInt() } , failed = { experimentViewVerifyToast("c 需要 int 类型") })?: return@experimentViewClick

                            viewModel.adaptiveThreshold(state, adaptiveMethodSelectedOption.value, typeSelectedOption.value, blockSize, c)
                        } else {
                            experimentViewVerifyToast("请选择阈值化类型以及全局阈值分割 或 自适应阈值分割")
                        }
                    }
                }
            ) {
                Text(text = "阈值分割", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "Canny 边缘检测", color = Color.Black)

            Row {
                basicTextFieldWithTitle(titleText = "threshold1", threshold1Text.value) { str ->
                    threshold1Text.value = str
                }

                basicTextFieldWithTitle(titleText = "threshold2", threshold2Text.value) { str ->
                    threshold2Text.value = str
                }

                basicTextFieldWithTitle(titleText = "apertureSize", apertureSizeText.value) { str ->
                    apertureSizeText.value = str
                }
            }

            Button(
                modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
                onClick = experimentViewClick(state) {
                    if(state.currentImage?.type != BufferedImage.TYPE_BYTE_BINARY) {
                        val threshold1 = getValidateField(block = { threshold1Text.value.toDouble() } , failed = { experimentViewVerifyToast("threshold1 需要 double 类型") })?: return@experimentViewClick
                        val threshold2 = getValidateField(block = { threshold2Text.value.toDouble() } , failed = { experimentViewVerifyToast("threshold2 需要 double 类型") })?: return@experimentViewClick
                        val apertureSize = getValidateField(block = { apertureSizeText.value.toInt() } , failed = { experimentViewVerifyToast("apertureSize 需要 int 类型") })?: return@experimentViewClick

                        edgeDetectionViewModel.canny(state, threshold1, threshold2, apertureSize)
                    }
                }
            ) {
                Text(text = "Canny 边缘检测", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "彩色图像分割", color = Color.Black)

            Row {
                basicTextFieldWithTitle(titleText = "hmin", hminText.value) { str ->
                    hminText.value = str
                }

                basicTextFieldWithTitle(titleText = "smin", sminText.value) { str ->
                    sminText.value = str
                }

                basicTextFieldWithTitle(titleText = "vmin", vminText.value) { str ->
                    vminText.value = str
                }
            }

            Row(modifier = Modifier.padding(top = 10.dp)){
                basicTextFieldWithTitle(titleText = "hmax", hmaxText.value) { str ->
                    hmaxText.value = str
                }

                basicTextFieldWithTitle(titleText = "smax", smaxText.value) { str ->
                    smaxText.value = str
                }

                basicTextFieldWithTitle(titleText = "vmax", vmaxText.value) { str ->
                    vmaxText.value = str
                }
            }

            Button(
                modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
                onClick = experimentViewClick(state) {
                    if(state.currentImage?.type!! in 1..9) {
                        val hmin = getValidateField(block = { hminText.value.toInt() } , failed = { experimentViewVerifyToast("hmin 需要 int 类型") })?: return@experimentViewClick
                        val smin = getValidateField(block = { sminText.value.toInt() } , failed = { experimentViewVerifyToast("smin 需要 int 类型") })?: return@experimentViewClick
                        val vmin = getValidateField(block = { vminText.value.toInt() } , failed = { experimentViewVerifyToast("vmin 需要 int 类型") })?: return@experimentViewClick

                        val hmax = getValidateField(block = { hmaxText.value.toInt() } , failed = { experimentViewVerifyToast("hmax 需要 int 类型") })?: return@experimentViewClick
                        val smax = getValidateField(block = { smaxText.value.toInt() } , failed = { experimentViewVerifyToast("smax 需要 int 类型") })?: return@experimentViewClick
                        val vmax = getValidateField(block = { vmaxText.value.toInt() } , failed = { experimentViewVerifyToast("vmax 需要 int 类型") })?: return@experimentViewClick

                        viewModel.inRange(state, hmin, smin, vmin, hmax, smax, vmax)
                    }
                }
            ) {
                Text(text = "彩色图像分割", color = Color.Unspecified)
            }
        }
    }
}