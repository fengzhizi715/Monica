package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.aiView
import cn.netdiscovery.monica.ui.controlpanel.filter.filterView
import cn.netdiscovery.monica.ui.widget.divider
import cn.netdiscovery.monica.utils.isMac
import cn.netdiscovery.monica.utils.isWindows

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
            basicView(state) // 基础功能

            divider()

            colorCorrectionView(state) // 图像调色

            divider()

            filterView(state) // 滤镜相关的内容

            divider()

            if(isMac || isWindows) {
                aiView(state) // AI 实验室
            }
        }
    }
}


