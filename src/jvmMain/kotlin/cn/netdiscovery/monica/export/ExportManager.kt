package cn.netdiscovery.monica.export

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.editor.layer.Layer
import cn.netdiscovery.monica.editor.layer.LayerManager
import cn.netdiscovery.monica.editor.layer.LayerRenderer
import java.awt.image.BufferedImage

/**
 * 导出管理器，负责将当前图层序列合成为位图或 BufferedImage。
 */
class ExportManager(
    private val layerManager: LayerManager,
    private val layerRenderer: LayerRenderer
) {

    /**
     * 将当前所有图层合成为 [ImageBitmap]。
     *
     * @param width 导出宽度（像素）
     * @param height 导出高度（像素）
     * @param density 当前绘制使用的密度
     * @param backgroundColor 可选背景色，默认为透明
     * @param layers 指定要导出的图层集合，默认为 LayerManager 当前图层快照
     */
    fun flattenToBitmap(
        width: Int,
        height: Int,
        density: Density,
        backgroundColor: Color = Color.Transparent,
        layers: List<Layer> = layerManager.layers.value
    ): ImageBitmap {
        val bitmap = ImageBitmap(width, height)
        val canvas = Canvas(bitmap)
        val drawScope = CanvasDrawScope()
        val size = Size(width.toFloat(), height.toFloat())

        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size
        ) {
            if (backgroundColor.alpha > 0f) {
                val rect = Rect(Offset.Zero, size)
                val paint = androidx.compose.ui.graphics.Paint().apply {
                    color = backgroundColor
                }
                drawContext.canvas.drawRect(rect, paint)
            }
            layerRenderer.drawAll(this, layers)
        }

        return bitmap
    }

    /**
     * 将当前所有图层合成为 [BufferedImage]。
     */
    fun flattenToBufferedImage(
        width: Int,
        height: Int,
        density: Density,
        backgroundColor: Color = Color.Transparent,
        layers: List<Layer> = layerManager.layers.value
    ): BufferedImage {
        val bitmap = flattenToBitmap(width, height, density, backgroundColor, layers)
        return bitmap.toAwtImage()
    }
}

