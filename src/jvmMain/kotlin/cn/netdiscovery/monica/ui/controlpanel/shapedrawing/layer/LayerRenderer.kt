package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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
    private var cachedLayerSignature: List<Pair<UUID, Long>> = emptyList()
    private var cachedAdjustmentFilters: Map<UUID, ColorFilter?> = emptyMap()

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
        val adjustmentFilters = resolveAdjustmentFilters(layers)
        layers.forEach { layer ->
            if (!layer.visible || layer.opacity <= 0f || layer is AdjustmentLayer) return@forEach
            drawLayer(drawScope, layer, adjustmentFilters[layer.id])
        }
    }

    /**
     * 绘制单个图层
     * 
     * 注意：此方法应该是 internal 或 public，以便在 Compose 层面使用
     */
    fun drawLayer(drawScope: DrawScope, layer: Layer, adjustmentFilter: ColorFilter? = null) {
        val resolvedAdjustmentFilter = adjustmentFilter ?: resolveAdjustmentFilters(layerManager.layers.value)[layer.id]
        drawScope.drawIntoCanvas { canvas ->
            val bounds = Rect(Offset.Zero, drawScope.size)
            opacityPaint.alpha = layer.opacity.coerceIn(0f, 1f)
            opacityPaint.colorFilter = resolvedAdjustmentFilter
            opacityPaint.blendMode = when (layer.blendMode) {
                LayerBlendMode.NORMAL -> BlendMode.SrcOver
                LayerBlendMode.MULTIPLY -> BlendMode.Multiply
                LayerBlendMode.SCREEN -> BlendMode.Screen
                LayerBlendMode.OVERLAY -> BlendMode.Overlay
            }
            canvas.saveLayer(bounds, opacityPaint)
            try {
                when (layer) {
                    is ImageLayer -> {
                        // 获取背景图尺寸（如果存在）
                        val backgroundLayer = layerManager.layers.value
                            .firstOrNull { 
                                it.name == cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController.BACKGROUND_LAYER_NAME 
                                && it is ImageLayer 
                            } as? ImageLayer
                        
                        val backgroundSize = backgroundLayer?.image?.let { 
                            Pair(it.width.toFloat(), it.height.toFloat()) 
                        }
                        
                        layer.render(drawScope, backgroundSize)
                    }
                    is ShapeLayer -> drawShapeLayer(drawScope, layer)
                    else -> layer.render(drawScope)
                }
            } finally {
                opacityPaint.colorFilter = null
                canvas.restore()
            }
        }
    }

    private fun resolveAdjustmentFilters(layers: List<Layer>): Map<UUID, ColorFilter?> {
        val signature = layers.map { it.id to it.version }
        if (signature == cachedLayerSignature) return cachedAdjustmentFilters

        val computed = layers
            .filterNot { it is AdjustmentLayer }
            .associate { layer ->
                layer.id to buildColorFilter(
                    layers
                        .dropWhile { it.id != layer.id }
                        .drop(1)
                        .filterIsInstance<AdjustmentLayer>()
                        .filter { adjustmentLayer ->
                            adjustmentLayer.visible &&
                                adjustmentLayer.opacity > 0f &&
                                (adjustmentLayer.targetLayerIds.isEmpty() || layer.id in adjustmentLayer.targetLayerIds)
                        }
                        .map { it.adjustment }
                )
            }

        cachedLayerSignature = signature
        cachedAdjustmentFilters = computed
        return computed
    }

    private fun buildColorFilter(adjustments: List<LayerAdjustment>): ColorFilter? {
        if (adjustments.isEmpty()) return null
        val combined = ColorMatrix()
        adjustments.forEach { adjustment ->
            combined.timesAssign(brightnessMatrix(adjustment.brightness))
            combined.timesAssign(contrastMatrix(adjustment.contrast))
            combined.timesAssign(saturationMatrix(adjustment.saturation))
        }
        return ColorFilter.colorMatrix(combined)
    }

    private fun brightnessMatrix(brightness: Float): ColorMatrix {
        val shift = brightness.coerceIn(-1f, 1f) * 255f
        return ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, shift,
                0f, 1f, 0f, 0f, shift,
                0f, 0f, 1f, 0f, shift,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    private fun contrastMatrix(contrast: Float): ColorMatrix {
        val scale = contrast.coerceIn(0f, 3f)
        val translate = 128f * (1f - scale)
        return ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    private fun saturationMatrix(saturation: Float): ColorMatrix {
        return ColorMatrix().apply {
            setToSaturation(saturation.coerceIn(0f, 3f))
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
