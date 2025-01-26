package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.TextFields
 * @author: Tony Shen
 * @date: 2024/10/17 11:17
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun basicTextFieldWithTitle(
    titleText:String,
    value: String,
    modifier:Modifier = Modifier,
    textModifier:Modifier = Modifier,
    width: Dp = 120.dp,
    onValueChange: (String) -> Unit) {
    Row {
        Text(text = titleText, modifier = textModifier)

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = modifier.padding(start = 10.dp, end = 10.dp).width(width).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )
    }
}