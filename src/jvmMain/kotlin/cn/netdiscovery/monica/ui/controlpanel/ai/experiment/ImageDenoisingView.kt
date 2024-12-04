package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ImageDenoisingView
 * @author: Tony Shen
 * @date: 2024/12/4 14:17
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun imageDenoising(state: ApplicationState, title: String) {

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "高斯滤波", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                }
            ) {
                Text(text = "高斯滤波", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "中值滤波", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                }
            ) {
                Text(text = "中值滤波", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "高斯双边滤波", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                }
            ) {
                Text(text = "高斯双边滤波", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "均值迁移滤波", color = Color.Black)

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                }
            ) {
                Text(text = "均值迁移滤波", color = Color.Unspecified)
            }
        }
    }
}