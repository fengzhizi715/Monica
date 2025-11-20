package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.ShapeDrawingViewModel
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer
import java.util.UUID

/**
 * 负责任务图层绘制与合成的渲染器。
 * 
 * 使用图层版本号优化渲染，只有版本变化的图层才会重新渲染。
 */
class LayerRenderer(
    private val layerManager: LayerManager
) {

    private val opacityPaint = Paint().apply {
        isAntiAlias = true
    }

    private val shapeRenderer by lazy { ShapeDrawingViewModel() }

    /**
     * 将当前 LayerManager 中的图层全部绘制到给定的 [DrawScope]。
     */
    fun drawAll(drawScope: DrawScope) {
        drawAll(drawScope, layerManager.layers.value)
    }

    /**
     * 将指定的图层列表绘制到给定的 [DrawScope]。
     * 
     * 注意：由于 DrawScope 的限制，无法在此层面实现真正的缓存优化。
     * 图层的 version 字段已准备好，可用于将来在 Compose 层面通过 
     * remember、key() 和 Modifier.drawWithCache 实现真正的缓存优化。
     */
    fun drawAll(drawScope: DrawScope, layers: List<Layer>) {
        if (layers.isEmpty()) return
        layers.forEach { layer ->
            if (!layer.visible || layer.opacity <= 0f) return@forEach
            drawLayer(drawScope, layer)
        }
    }

    /**
     * 绘制单个图层
     */
    private fun drawLayer(drawScope: DrawScope, layer: Layer) {
        drawScope.drawIntoCanvas { canvas ->
            val bounds = Rect(Offset.Zero, drawScope.size)
            opacityPaint.alpha = layer.opacity.coerceIn(0f, 1f)
            canvas.saveLayer(bounds, opacityPaint)
            try {
                when (layer) {
                    is ImageLayer -> layer.render(drawScope)
                    is ShapeLayer -> drawShapeLayer(drawScope, layer)
                    else -> layer.render(drawScope)
                }
            } finally {
                canvas.restore()
            }
        }
    }

    private fun drawShapeLayer(drawScope: DrawScope, shapeLayer: ShapeLayer) {
        if (shapeLayer.isEmpty()) return
        val canvasDrawer = CanvasDrawer(TextDrawer, drawScope.drawContext.canvas)
        shapeRenderer.drawShape(
            canvasDrawer = canvasDrawer,
            lines = shapeLayer.displayLines,
            circles = shapeLayer.displayCircles,
            triangles = shapeLayer.displayTriangles,
            rectangles = shapeLayer.displayRectangles,
            polygons = shapeLayer.displayPolygons,
            texts = shapeLayer.displayTexts,
            saveFlag = false
        )
    }
}

