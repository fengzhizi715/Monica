package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.material.MaterialTheme

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.Checkboxs
 * @author: Tony Shen
 * @date: 2024/10/28 13:50
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun checkBoxWithTitle(
    text: String,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    fontSize: TextUnit = MaterialTheme.typography.body1.fontSize,
    fontWeight: FontWeight? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange?.invoke(it) }
        )
        Text(
            text = text,
            modifier = textModifier,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}