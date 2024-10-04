package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.divider
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.utils.composeClick
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.BinaryImageAnalysis
 * @author: Tony Shen
 * @date:  2024/10/2 15:03
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun binaryImageAnalysis(state: ApplicationState) {

    val typeSelectTags = arrayListOf("THRESH_BINARY", "THRESH_BINARY_INV")
    val typeSelectedOption = remember { mutableStateOf("Null") }

    val thresholdSelectTags = arrayListOf("THRESH_OTSU", "THRESH_TRIANGLE")
    val thresholdSelectedOption = remember { mutableStateOf("Null") }

    val adaptiveMethodSelectTags = arrayListOf("ADAPTIVE_THRESH_MEAN_C", "ADAPTIVE_THRESH_GAUSSIAN_C")
    val adaptiveMethodSelectedOption = remember { mutableStateOf("Null") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp)) {
        Column(modifier = Modifier.padding(top = 20.dp).weight(0.15f)) {
            subTitle(text = "灰度图像", color = Color.Black)
            divider()

            Button(
                modifier = Modifier,
                onClick = composeClick {

                }
            ) {
                Text(text = "图像灰度化", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.weight(0.35f)) {
            subTitle(text = "阈值分割", color = Color.Black)
            divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshType, onCheckedChange = {
                    state.isThreshType = it

                    if (!state.isThreshType) {
                        typeSelectedOption.value = "Null"
                    }
                })
                Text("阈值化类型", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                typeSelectTags.forEach {
                    RadioButton(
                        selected = (it == typeSelectedOption.value),
                        onClick = {
                            typeSelectedOption.value = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isThreshSegment, onCheckedChange = {
                    state.isThreshSegment = it

                    if (!state.isThreshSegment) {
                        thresholdSelectedOption.value = "Null"
                    }
                })
                Text("全局阈值分割", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                thresholdSelectTags.forEach {
                    RadioButton(
                        selected = (it == thresholdSelectedOption.value),
                        onClick = {
                            thresholdSelectedOption.value = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isAdaptiveThresh, onCheckedChange = {
                    state.isAdaptiveThresh = it

                    if (!state.isAdaptiveThresh) {
                        adaptiveMethodSelectedOption.value = "Null"
                    }
                })
                Text("自适应阈值分割", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                Text("自适应阈值算法", modifier = Modifier.align(Alignment.CenterVertically))

                adaptiveMethodSelectTags.forEach {
                    RadioButton(
                        selected = (it == adaptiveMethodSelectedOption.value),
                        onClick = {
                            adaptiveMethodSelectedOption.value = it
                        }
                    )

                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
        }

        Column(modifier = Modifier.weight(0.5f)) {
            subTitle(text = "边缘检测算子", color = Color.Black)
            divider()
        }
    }
}