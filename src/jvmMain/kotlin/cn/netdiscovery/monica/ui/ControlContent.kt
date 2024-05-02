package cn.netdiscovery.monica.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.showFileSelector
import javax.swing.JFileChooser

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ControlContent
 * @author: Tony Shen
 * @date: 2024/4/26 11:10
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun divider() {
    Row {
        Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).height(1.dp).weight(1.0f).background(color = Color.LightGray))
    }
}

@Composable
fun ControlContent(
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
            basicContent(state)

            divider()

            imageProcessContent(state)

            divider()

            // 滤镜相关的内容
            filterContent(state)
        }
    }
}
