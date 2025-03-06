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
import cn.netdiscovery.monica.state.FilterStatus
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.utils.composeClick
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.FilterView
 * @author: Tony Shen
 * @date: 2025/3/6 14:25
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun filterView(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isFilter, onCheckedChange = {
            state.isFilter = it

            if (!state.isFilter) {
                logger.info("取消了滤镜")
            } else {
                logger.info("勾选了滤镜")

                state.isGeneralSettings = false
                state.isBasic = false
                state.isColorCorrection = false
                state.isAI = false
            }
        })
        subTitle(text = "滤镜", color = Color.Black)
    }
    Column(modifier = Modifier.fillMaxWidth()){
        Button(
            modifier = Modifier.align(Alignment.End).padding(start = 15.dp),
            enabled = state.isFilter,
            onClick = composeClick {
                state.togglePreviewWindowAndUpdateStatus(FilterStatus)
            }
        ) {
            Text(text = "进入滤镜界面", color = Color.Unspecified)
        }
    }
}