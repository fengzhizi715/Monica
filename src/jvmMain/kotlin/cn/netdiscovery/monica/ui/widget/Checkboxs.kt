package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.Checkboxs
 * @author: Tony Shen
 * @date: 2024/10/28 13:50
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 封装 Checkbox
 */
@Composable
fun checkBoxWithTitle(text: String,
                      textModify:Modifier = Modifier,
                      color: Color = Color.Unspecified,
                      checked: Boolean,
                      onCheckedChange: ((Boolean) -> Unit)?,
                      ) {

    Row {
        Checkbox(checked, onCheckedChange = {
            onCheckedChange?.invoke(it)
        })

        Text(text, modifier = textModify.align(Alignment.CenterVertically), color = color)
    }

}