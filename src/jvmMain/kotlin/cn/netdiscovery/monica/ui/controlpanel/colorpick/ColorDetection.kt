package cn.netdiscovery.monica.ui.controlpanel.colorpick

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import org.jetbrains.skia.Bitmap

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ColorDetection
 * @author: Tony Shen
 * @date: 2024/6/13 22:03
 * @version: V1.0 <描述当前版本功能>
 */

fun calculateColorInPixel(
    offsetX: Float,
    offsetY: Float,
    startImageX: Float = 0f,
    startImageY: Float = 0f,
    rect: IntRect,
    width: Float,
    height: Float,
    bitmap: Bitmap,
): Color {

    val bitmapWidth = bitmap.width
    val bitmapHeight = bitmap.height

    if (bitmapWidth == 0 || bitmapHeight == 0) return Color.Unspecified

    // End positions, this might be less than Image dimensions if bitmap doesn't fit Image
    val endImageX = width - startImageX
    val endImageY = height - startImageY

    val scaledX = scale(
        start1 = startImageX,
        end1 = endImageX,
        pos = offsetX,
        start2 = rect.left.toFloat(),
        end2 = rect.right.toFloat()
    ).toInt().coerceIn(0, bitmapWidth - 1)

    val scaledY = scale(
        start1 = startImageY,
        end1 = endImageY,
        pos = offsetY,
        start2 = rect.top.toFloat(),
        end2 = rect.bottom.toFloat()
    ).toInt().coerceIn(0, bitmapHeight - 1)

    val pixel: Int = bitmap.getColor(scaledX,scaledY)

    val red = pixel shr 16 and 0xFF
    val green = pixel shr 8 and 0xFF
    val blue = pixel and 0xFF

    return Color(red, green, blue)
}

/**
 * 线性插值
 */
private fun lerp(start: Float, end: Float, amount: Float): Float {
    return (1 - amount) * start + amount * end
}

/**
 * Scale x1 from start1..end1 range to start2..end2 range
 */
private fun scale(start1: Float, end1: Float, pos: Float, start2: Float, end2: Float) =
    lerp(start2, end2, calculateFraction(start1, end1, pos))

private fun calculateFraction(start: Float, end: Float, pos: Float) =
    (if (end - start == 0f) 0f else (pos - start) / (end - start)).coerceIn(0f, 1f)