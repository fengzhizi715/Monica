package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.config.subTitleTextSize
import cn.netdiscovery.monica.state.BlurStatus
import cn.netdiscovery.monica.ui.widget.divider
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.composeClick

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.BinaryImageAnalysis
 * @author: Tony Shen
 * @date:  2024/10/2 15:03
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun binaryImageAnalysis() {

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
            val typeSelectedOption = remember { mutableStateOf(false) }
            val thresholdSelectedOption = remember { mutableStateOf(false) }

            Row {
                Text("阈值化类型: ", modifier = Modifier.align(Alignment.CenterVertically))

                RadioButton(
                    selected = !typeSelectedOption.value,
                    onClick = { typeSelectedOption.value = false }
                )
                Text("THRESH_BINARY", modifier = Modifier.align(Alignment.CenterVertically))

                RadioButton(
                    selected = typeSelectedOption.value,
                    onClick = { typeSelectedOption.value = true }
                )

                Text("THRESH_BINARY_INV", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                Text("全局阈值分割: ", modifier = Modifier.align(Alignment.CenterVertically))

                RadioButton(
                    selected = !thresholdSelectedOption.value,
                    onClick = { thresholdSelectedOption.value = false }
                )
                Text("THRESH_OTSU", modifier = Modifier.align(Alignment.CenterVertically))

                RadioButton(
                    selected = thresholdSelectedOption.value,
                    onClick = { thresholdSelectedOption.value = true }
                )
                Text("THRESH_TRIANGLE", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }

        Column(modifier = Modifier.weight(0.5f)) {
            subTitle(text = "边缘检测算子", color = Color.Black)
            divider()
        }
    }
}