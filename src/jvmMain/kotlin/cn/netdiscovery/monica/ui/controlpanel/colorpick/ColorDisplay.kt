package cn.netdiscovery.monica.ui.controlpanel.colorpick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorData

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ColorDisplay
 * @author: Tony Shen
 * @date: 2024/6/14 11:58
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun ColorDisplay(
    modifier: Modifier = Modifier,
    colorData: ColorData
) {
    val color = colorData.color
    val colorName = colorData.name

    val lightness = color.toHSL()[2]
    val textColor = if (lightness < .6f) Color.White else Color.Black

    val hexText = colorData.hexText
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(10))
            .width(170.dp)
            .background(color = color)
            .padding(start = 16.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
    ) {

        Row {
            Column {
//                Text(text = colorName, fontSize = 10.sp, color = textColor)
                Text(text = hexText, fontSize = 20.sp, color = textColor)
            }
        }

        Column {
            Text(text = colorData.rgb, fontSize = 12.sp, color = textColor)
            Text(text = colorData.hslString, fontSize = 12.sp, color = textColor)
            Text(text = colorData.hsvString, fontSize = 12.sp, color = textColor)
        }
    }
}