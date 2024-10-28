package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.Checkboxs
 * @author: Tony Shen
 * @date: 2024/10/28 13:50
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun checkBoxWithTitle(text: String,
                      checked: Boolean,
                      onCheckedChange: ((Boolean) -> Unit)?,
                      ) {

    Row {
        Checkbox(checked, onCheckedChange = {
            onCheckedChange?.invoke(it)
        })

        Text(text, modifier = Modifier.align(Alignment.CenterVertically).padding(end = 50.dp))
    }

}