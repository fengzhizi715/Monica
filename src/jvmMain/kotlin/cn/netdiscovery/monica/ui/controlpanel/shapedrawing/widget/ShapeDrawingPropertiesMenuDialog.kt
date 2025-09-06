package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.widget.properties.ExposedSelectionMenu

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ShapeDrawingPropertiesMenuDialog
 * @author: Tony Shen
 * @date: 2024/11/26 10:33
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun ShapeDrawingPropertiesMenuDialog(
    shapeProperties: ShapeProperties, 
    onDismiss: (ShapeProperties) -> Unit
) {

    var alpha    by remember { mutableStateOf(shapeProperties.alpha) }
    var fontSize by remember { mutableStateOf(shapeProperties.fontSize) }
    var fill     by remember { mutableStateOf(shapeProperties.fill) }
    var border   by remember { mutableStateOf(shapeProperties.border) }

    Dialog(onDismissRequest = {
        // 返回更新后的属性
        val updatedProperties = shapeProperties.copy(
            alpha = alpha,
            fontSize = fontSize,
            fill = fill,
            border = border
        )
        onDismiss(updatedProperties)
    }) {

        Card(
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "alpha: ${alpha}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Slider(
                    value = alpha,
                    onValueChange = {
                        alpha = it
                    },
                    valueRange = 0f..1f,
                    onValueChangeFinished = {},
                    colors = SliderDefaults.colors()
                )

                Text(
                    text = "fontSize: ${fontSize.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Slider(
                    value = fontSize,
                    onValueChange = {
                        fontSize = it
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {},
                    colors = SliderDefaults.colors()
                )

                ExposedSelectionMenu(title = "fill",
                    index = when (fill) {
                        false -> 0
                        true -> 1
                    },
                    options = listOf("False", "True"),
                    onSelected = {
                        fill = when (it) {
                            0 -> false
                            1 -> true
                            else -> false
                        }
                    }
                )

                ExposedSelectionMenu(title = "border",
                    index = when (border) {
                        Border.No      -> 0
                        Border.Dot     -> 1
                        Border.Dash    -> 2
                        Border.DashDot -> 3
                        Border.Line    -> 4
                    },
                    options = listOf("No", "Dot", "Dash", "DashDot", "Line"),
                    onSelected = {
                        border = when (it) {
                            0 -> Border.No
                            1 -> Border.Dot
                            2 -> Border.Dash
                            3 -> Border.DashDot
                            4 -> Border.Line
                            else -> Border.No
                        }
                    }
                )
            }
        }
    }
}