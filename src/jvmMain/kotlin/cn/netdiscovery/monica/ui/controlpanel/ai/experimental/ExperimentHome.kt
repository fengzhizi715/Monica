package cn.netdiscovery.monica.ui.controlpanel.ai.experimental

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    }
}