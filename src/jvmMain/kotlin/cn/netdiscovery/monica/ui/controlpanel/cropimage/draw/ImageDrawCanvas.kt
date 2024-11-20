package cn.netdiscovery.monica.ui.controlpanel.cropimage.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.cropimage.draw.ImageDrawCanvas
 * @author: Tony Shen
 * @date: 2024/5/26 15:37
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
internal fun ImageDrawCanvas(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    imageWidth: Int,
    imageHeight: Int
) {
    Canvas(modifier = modifier) {

        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        drawImage(
            image = imageBitmap,
            srcSize = IntSize(imageBitmap.width, imageBitmap.height),
            dstSize = IntSize(imageWidth, imageHeight),
            dstOffset = IntOffset(
                x = (canvasWidth - imageWidth) / 2,
                y = (canvasHeight - imageHeight) / 2
            )
        )
    }
}