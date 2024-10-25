package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.divider
import cn.netdiscovery.monica.ui.widget.subTitle
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

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 20.dp)) {
        Column{
            subTitle(text = "过滤设置", color = Color.Black)
            divider()
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitle(text = "显示设置", color = Color.Black)
            divider()
        }
    }
}