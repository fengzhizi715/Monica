package cn.netdiscovery.monica.ui.controlpanel.colorpick

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ColorSelectionDrawing
 * @author: Tony Shen
 * @date: 2024/6/14 10:57
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
internal fun ColorSelectionDrawing(
    modifier: Modifier,
    thumbnailSize: Dp = defaultThumbnailSize,
    offset: Offset,
    thumbnailCenter: Offset,
    color: Color
) {
    Canvas(modifier = modifier.fillMaxSize()) {

        val canvasWidth = size.width
        val canvasHeight = size.height

        if (color != Color.Unspecified && offset.isSpecified && thumbnailCenter.isSpecified) {

            // Get thumb size as parameter but limit max size to minimum of canvasWidth and Height
            val imageThumbSize: Int =
                thumbnailSize.toPx()
                    .coerceAtMost(canvasWidth.coerceAtLeast(canvasHeight)).roundToInt()

            val radius: Float = 8.dp.toPx()

            // Draw touch position circle
            drawCircle(
                Color.Black,
                radius = radius * 1.4f,
                center = offset,
                style = Stroke(radius * 0.4f)
            )
            drawCircle(
                Color.White,
                radius = radius * 1.0f,
                center = offset,
                style = Stroke(radius * 0.4f)
            )

            // Draw thumbnail center circle
            drawCircle(
                color = Color.Black,
                radius = radius,
                center = thumbnailCenter,
                style = Stroke(radius * .5f)
            )
            drawCircle(
                color = Color.White,
                radius = radius,
                center = thumbnailCenter,
                style = Stroke(radius * .2f)
            )

            drawCircle(
                color = color,
                radius = imageThumbSize / 20f,
                center = thumbnailCenter,
            )
        }
    }
}