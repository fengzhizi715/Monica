package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.utils.composeClick
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.GeneralSettingsView
 * @author: Tony Shen
 * @date: 2025/1/24 11:59
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun generalSettingsView(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isGeneralSettings, onCheckedChange = {
            state.isGeneralSettings = it

            if (!state.isGeneralSettings) {
                state.resetCurrentStatus()
                logger.info("取消了通用设置")
            } else {
                logger.info("勾选了通用设置")
            }
        })
        subTitle(text = "通用设置", color = Color.Black)
    }

    Column(modifier = Modifier.fillMaxWidth()){
        Button(
            modifier = Modifier.align(Alignment.End).padding(start = 15.dp),
            enabled = state.isGeneralSettings,
            onClick = composeClick {

            }
        ) {
            Text(text = "设置", color = Color.Unspecified)
        }
    }
}