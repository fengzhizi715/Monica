package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.ui.widget.title

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ExperimentHome
 * @author: Tony Shen
 * @date: 2024/11/2 00:27
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun experimentHome() {

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally) , text = "首页", color = Color.Black)

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(painter = painterResource("images/ai/OpenCV_Logo.png"),
                contentDescription = null,
                modifier = Modifier)

            subTitle(modifier = Modifier.padding(top = 20.dp),
                    text = "本模块的算法使用 OpenCV C++ 实现，目前只适用于一些简单 CV 算法的快速验证")

        }

    }
}