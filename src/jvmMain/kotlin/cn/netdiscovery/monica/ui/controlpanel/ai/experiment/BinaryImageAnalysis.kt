package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.ui.controlpanel.divider

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
        Column(modifier = Modifier.padding(top = 20.dp).weight(0.2f)) {
            Text("灰度图像",fontSize = 24.sp)
            divider()
        }

        Column(modifier = Modifier.weight(0.3f)) {
            Text("阈值分割", fontSize = 20.sp)
            divider()
        }

        Column(modifier = Modifier.weight(0.5f)) {
            Text("边缘检测算子", fontSize = 20.sp)
            divider()
        }
    }
}