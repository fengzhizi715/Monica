package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.RightSideMenuBar
 * @author: Tony Shen
 * @date: 2024/10/5 14:22
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun rightSideMenuBar(modifier: Modifier,
                     backgroundColor:Color = Color.LightGray,
                     percent:Int = 15,
                     content: @Composable ColumnScope.() -> Unit) {

    Row(modifier = modifier
        .padding(start = 10.dp, end = 10.dp)
        .background(color = backgroundColor, shape = RoundedCornerShape(percent))) {
        Column(
            Modifier.padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            content.invoke(this)
        }
    }
}