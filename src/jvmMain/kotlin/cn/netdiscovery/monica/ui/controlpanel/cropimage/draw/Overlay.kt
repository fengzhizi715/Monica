package cn.netdiscovery.monica.ui.controlpanel.cropimage.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropImageMask
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropOutline
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropPath
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropShape
import cn.netdiscovery.monica.ui.controlpanel.cropimage.utils.drawGrid
import cn.netdiscovery.monica.ui.controlpanel.cropimage.utils.scaleAndTranslatePath
import cn.netdiscovery.monica.utils.extensions.drawWithLayer

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.cropimage.draw.Overlay
 * @author: Tony Shen
 * @date: 2024/5/26 15:38
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
internal fun DrawingOverlay(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    cropOutline: CropOutline,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Dp,
    drawHandles: Boolean,
    handleSize: Float,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?
) {
    val density = LocalDensity.current
    val layoutDirection: LayoutDirection = LocalLayoutDirection.current

    val strokeWidthPx = LocalDensity.current.run { strokeWidth.toPx() }

    val pathHandles = remember {
        Path()
    }

    when (cropOutline) {
        is CropShape -> {

            val outline = remember(rect, cropOutline) {
                cropOutline.shape.createOutline(rect.size, layoutDirection, density)
            }

            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSize,
                pathHandles = pathHandles,
                outline = outline,
                onDrawGrid = onDrawGrid,
            )
        }
        is CropPath -> {
            val path = remember(rect, cropOutline) {
                Path().apply {
                    addPath(cropOutline.path)
                    scaleAndTranslatePath(rect.width, rect.height)
                }
            }


            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSize,
                pathHandles = pathHandles,
                path = path,
                onDrawGrid = onDrawGrid,
            )
        }
        is CropImageMask -> {
            val imageBitmap = cropOutline.image

            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSize,
                pathHandles = pathHandles,
                image = imageBitmap,
                onDrawGrid = onDrawGrid,
            )
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    outline: Outline,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay,
            rect,
            drawGrid,
            transparentColor,
            overlayColor,
            handleColor,
            strokeWidth,
            drawHandles,
            handleSize,
            pathHandles,
            onDrawGrid,
        ) {
            drawCropOutline(outline = outline)
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    path: Path,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay,
            rect,
            drawGrid,
            transparentColor,
            overlayColor,
            handleColor,
            strokeWidth,
            drawHandles,
            handleSize,
            pathHandles,
            onDrawGrid,
        ) {
            drawCropPath(path)
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    image: ImageBitmap,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay,
            rect,
            drawGrid,
            transparentColor,
            overlayColor,
            handleColor,
            strokeWidth,
            drawHandles,
            handleSize,
            pathHandles,
            onDrawGrid,
        ) {
            drawCropImage(rect, image)
        }
    }
}

private fun DrawScope.drawOverlay(
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
    drawBlock: DrawScope.() -> Unit,
) {
    drawWithLayer {

        // Destination
        drawRect(transparentColor)

        // Source
        translate(left = rect.left, top = rect.top) {
            drawBlock()
        }

        if (drawGrid) {
            if (onDrawGrid != null) {
                onDrawGrid(rect, strokeWidth, overlayColor)
            } else {
                drawGrid(
                    rect = rect,
                    strokeWidth = strokeWidth,
                    color = overlayColor,
                )
            }
        }
    }

    if (drawOverlay) {
        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = overlayColor,
            style = Stroke(width = strokeWidth)
        )

        if (drawHandles) {
            pathHandles.apply {
                reset()
                updateHandlePath(rect, handleSize)
            }

            drawPath(
                path = pathHandles,
                color = handleColor,
                style = Stroke(
                    width = strokeWidth * 2,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun DrawScope.drawCropImage(
    rect: Rect,
    imageBitmap: ImageBitmap,
    blendMode: BlendMode = BlendMode.DstOut
) {
    drawImage(
        image = imageBitmap,
        dstSize = IntSize(rect.size.width.toInt(), rect.size.height.toInt()),
        blendMode = blendMode
    )
}

private fun DrawScope.drawCropOutline(
    outline: Outline,
    blendMode: BlendMode = BlendMode.SrcOut
) {
    drawOutline(
        outline = outline,
        color = Color.Transparent,
        blendMode = blendMode
    )
}

private fun DrawScope.drawCropPath(
    path: Path,
    blendMode: BlendMode = BlendMode.SrcOut
) {
    drawPath(
        path = path,
        color = Color.Transparent,
        blendMode = blendMode
    )
}

private fun Path.updateHandlePath(
    rect: Rect,
    handleSize: Float
) {
    if (rect != Rect.Zero) {
        // Top left lines
        moveTo(rect.topLeft.x, rect.topLeft.y + handleSize)
        lineTo(rect.topLeft.x, rect.topLeft.y)
        lineTo(rect.topLeft.x + handleSize, rect.topLeft.y)

        // Top right lines
        moveTo(rect.topRight.x - handleSize, rect.topRight.y)
        lineTo(rect.topRight.x, rect.topRight.y)
        lineTo(rect.topRight.x, rect.topRight.y + handleSize)

        // Bottom right lines
        moveTo(rect.bottomRight.x, rect.bottomRight.y - handleSize)
        lineTo(rect.bottomRight.x, rect.bottomRight.y)
        lineTo(rect.bottomRight.x - handleSize, rect.bottomRight.y)

        // Bottom left lines
        moveTo(rect.bottomLeft.x + handleSize, rect.bottomLeft.y)
        lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
        lineTo(rect.bottomLeft.x, rect.bottomLeft.y - handleSize)
    }
}
