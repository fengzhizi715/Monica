package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ClickableImage
 * @author: Tony Shen
 * @date: 2025/8/5 13:54
 * @version: V1.0 <描述当前版本功能>
 */
data class ClickPoint(
    val x: Float,
    val y: Float,
    val label: Int // 1 = 前景, 0 = 背景
)

@Composable
fun ClickableImage(
    imageBitmap: ImageBitmap,
    originalSize: IntSize,
    clickPoints: List<ClickPoint>,
    currentLabel: Int, // 1 = 前景, 0 = 背景
    onAddClickPoint: (ClickPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageSize by remember { mutableStateOf(IntSize(1, 1)) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                imageSize = coordinates.size
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val scaleX = originalSize.width.toFloat() / imageSize.width
                    val scaleY = originalSize.height.toFloat() / imageSize.height
                    val mappedOffset = Offset(offset.x * scaleX, offset.y * scaleY)
                    onAddClickPoint(ClickPoint(mappedOffset.x, mappedOffset.y, currentLabel))
                }
            }
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / originalSize.width
            val scaleY = size.height / originalSize.height

            clickPoints.forEach { point ->
                val displayX = point.x * scaleX
                val displayY = point.y * scaleY
                drawCircle(
                    color = if (point.label == 1) Color.Green else Color.Red,
                    radius = 4.dp.toPx(),
                    center = Offset(displayX, displayY)
                )
            }
        }
    }
}
