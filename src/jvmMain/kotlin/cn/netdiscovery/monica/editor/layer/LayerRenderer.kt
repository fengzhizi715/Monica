package cn.netdiscovery.monica.editor.layer

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.ShapeDrawingViewModel
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer

/**
 * 负责任务图层绘制与合成的渲染器。
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
     */
    fun drawAll(drawScope: DrawScope, layers: List<Layer>) {
        if (layers.isEmpty()) return
        layers.forEach { layer ->
            if (!layer.visible || layer.opacity <= 0f) return@forEach
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

