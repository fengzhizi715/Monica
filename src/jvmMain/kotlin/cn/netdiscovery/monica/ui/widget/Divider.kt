package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.Divider
 * @author: Tony Shen
 * @date: 2024/10/2 22:13
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun divider() {
    Row {
        Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).height(1.dp).weight(1.0f).background(color = Color.LightGray))
    }
}