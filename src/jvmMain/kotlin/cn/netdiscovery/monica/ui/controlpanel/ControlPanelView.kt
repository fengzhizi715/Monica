package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.crop.cropView
import cn.netdiscovery.monica.ui.controlpanel.filter.filterView

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ControlPanelView
 * @author: Tony Shen
 * @date: 2024/4/26 11:10
 * @version: V1.0 控制面板
 */

@Composable
fun controlPanel(
    state: ApplicationState,
    modifier: Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            basicView(state)

            divider()

            cropView(state)

            divider()

            imageProcessView(state)

            divider()

            // 滤镜相关的内容
            filterView(state)
        }
    }
}

@Composable
fun divider() {
    Row {
        Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).height(1.dp).weight(1.0f).background(color = Color.LightGray))
    }
}
