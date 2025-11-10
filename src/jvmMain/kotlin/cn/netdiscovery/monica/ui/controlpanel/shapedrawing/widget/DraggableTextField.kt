package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.widget.confirmButton
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.DraggableTextField
 * @author: Tony Shen
 * @date: 2024/11/26 10:28
 * @version: V1.0 <描述当前版本功能>
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun draggableTextField(
    modifier: Modifier = Modifier,
    text: String,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    density: Density,
    onTextChanged: (String) -> Unit,
    onDragged: (Offset) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    // 计算画布中心（相对于屏幕中心）
    val halfCanvasWidthPx = canvasWidthPx / 2f
    val halfCanvasHeightPx = canvasHeightPx / 2f
    
    // 文本输入框的尺寸（像素）
    val textFieldWidthPx = with(density) { 250.dp.toPx() }
    val textFieldHeightPx = with(density) { 130.dp.toPx() }
    val halfTextFieldWidthPx = textFieldWidthPx / 2f
    val halfTextFieldHeightPx = textFieldHeightPx / 2f

    Box(
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change ->
                    offset += change
                    // 限制拖拽范围在画布区域内
                    // offset 是相对于屏幕中心的偏移，需要确保文本输入框不超出画布边界
                    if (abs(offset.x) > halfCanvasWidthPx - halfTextFieldWidthPx || 
                        abs(offset.y) > halfCanvasHeightPx - halfTextFieldHeightPx) {
                        offset -= change
                        return@detectDragGestures
                    }
                }
            }
            .shadow(8.dp)
            .background(Color.White)
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column {
            TextField (
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier.width(220.dp)
            )

            confirmButton(true, modifier = Modifier.align(Alignment.End).padding(top = 5.dp)) {
                onDragged.invoke(offset)
            }
        }
    }
}