package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ShapeDrawingPropertiesMenuDialog
 * @author: Tony Shen
 * @date: 2024/11/26 10:33
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun ShapeDrawingPropertiesMenuDialog(shapeProperties: ShapeProperties, onDismiss: () -> Unit) {

    var strokeWidth by remember { mutableStateOf(shapeProperties.fontSize) }

    Dialog(onDismissRequest = {
        onDismiss.invoke()
    }) {

        Card(
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Stroke Width ${strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        shapeProperties.fontSize = strokeWidth
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {}
                )
            }
        }
    }
}