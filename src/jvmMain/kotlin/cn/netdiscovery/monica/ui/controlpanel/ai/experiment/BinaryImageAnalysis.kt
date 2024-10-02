package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.config.subTitleTextSize
import cn.netdiscovery.monica.ui.widget.divider
import cn.netdiscovery.monica.ui.widget.subTitle

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
            subTitle(text = "灰度图像", color = Color.Black)
            divider()
        }

        Column(modifier = Modifier.weight(0.3f)) {
            subTitle(text = "阈值分割", color = Color.Black)
            divider()
        }

        Column(modifier = Modifier.weight(0.5f)) {
            subTitle(text = "边缘检测算子", color = Color.Black)
            divider()
        }
    }
}