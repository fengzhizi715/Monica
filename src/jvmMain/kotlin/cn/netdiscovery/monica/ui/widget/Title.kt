package cn.netdiscovery.monica.ui.widget

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import cn.netdiscovery.monica.config.subTitleTextSize
import cn.netdiscovery.monica.config.titleTextSize

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.Title
 * @author: Tony Shen
 * @date: 2024/10/2 22:22
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun title(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colors.primary,
    fontSize: TextUnit = titleTextSize,
    fontWeight: FontWeight? = null
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}

@Composable
fun subTitle(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colors.primary,
    fontSize: TextUnit = subTitleTextSize,
    fontWeight: FontWeight? = null
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}

@Composable
fun subTitleWithDivider(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colors.primary,
    fontSize: TextUnit = subTitleTextSize,
    fontWeight: FontWeight? = null
) {
    subTitle(modifier = modifier, text = text, color = color, fontSize = fontSize, fontWeight = fontWeight)
    divider()
}