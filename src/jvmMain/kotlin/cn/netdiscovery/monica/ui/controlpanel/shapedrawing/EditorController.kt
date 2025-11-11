package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.Layer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerRenderer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerType
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ShapeLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import java.awt.image.BufferedImage
import java.util.UUID

/**
 * EditorController 负责协调 LayerManager、LayerRenderer 以及导出逻辑，
 * 同时记录当前工具与激活图层信息，供 UI 直接调用。
 */
class EditorController(
    val layerManager: LayerManager = LayerManager()
) {

    companion object {
        /**
         * 限制最多创建的形状层数量
         * 方案一（简化设计）：限制为1个形状层，保留多个图像层
         */
        private const val MAX_SHAPE_LAYERS = 1
    }

    val layerRenderer = LayerRenderer(layerManager)

    private val _currentTool = mutableStateOf(EditorTool.SELECTION)
    val currentTool get() = _currentTool.value

    fun selectTool(tool: EditorTool) {
        _currentTool.value = tool
    }

    fun addLayer(layer: Layer, index: Int? = null) {
        layerManager.addLayer(layer, index)
    }

    fun createImageLayer(
        name: String,
        image: ImageBitmap?,
        index: Int? = null
    ): ImageLayer {
        val layer = ImageLayer(name = name, image = image)
        layerManager.addLayer(layer, index)
        return layer
    }

    /**
     * 添加形状层，但限制最多只能创建 MAX_SHAPE_LAYERS 个形状层
     * 如果已达上限，返回现有的第一个形状层并激活它
     */
    fun addShapeLayer(name: String = "形状图层"): ShapeLayer? {
        val existingShapeLayers = layerManager.layers.value.filter { it.type == LayerType.SHAPE }

        if (existingShapeLayers.size >= MAX_SHAPE_LAYERS) {
            // 如果已达上限，返回现有的第一个形状层并激活它
            val existing = existingShapeLayers.firstOrNull() as? ShapeLayer
            existing?.let { layerManager.setActiveLayer(it.id) }
            return existing
        }

        val layer = ShapeLayer(name)
        layerManager.addLayer(layer)
        return layer
    }

    /**
     * 获取当前形状层数量
     */
    fun getShapeLayerCount(): Int {
        return layerManager.layers.value.count { it.type == LayerType.SHAPE }
    }

    /**
     * 检查是否可以添加更多形状层
     */
    fun canAddShapeLayer(): Boolean {
        return getShapeLayerCount() < MAX_SHAPE_LAYERS
    }

    fun removeLayer(id: UUID) {
        layerManager.removeLayer(id)
    }

    fun clearLayers() {
        layerManager.clear()
    }

    fun setActiveLayer(id: UUID?) {
        layerManager.setActiveLayer(id)
    }

    fun ensureActiveShapeLayer(): ShapeLayer {
        val active = layerManager.activeLayer.value
        if (active is ShapeLayer) return active

        val existing = layerManager.layers.value.firstOrNull { it.type == LayerType.SHAPE } as? ShapeLayer
        if (existing != null) {
            layerManager.setActiveLayer(existing.id)
            return existing
        }

        val newLayer = ShapeLayer("Shape Layer")
        layerManager.addLayer(newLayer)
        return newLayer
    }

    /**
     * 检查是否可以在当前激活的形状层上绘制
     * 返回 true 表示可以绘制，false 表示图层锁定或不是形状层
     * 注意：此方法会先确保存在一个激活的形状层，然后再检查锁定状态
     */
    fun canDrawOnActiveShapeLayer(): Boolean {
        // 先确保有一个激活的形状层
        val shapeLayer = try {
            ensureActiveShapeLayer()
        } catch (e: Exception) {
            return false
        }
        // 检查是否锁定
        return !shapeLayer.locked
    }

    fun addShapeToActiveLayer(key: Offset, displayShape: Shape, originalShape: Shape) {
        val shapeLayer = ensureActiveShapeLayer()
        // 如果形状层已锁定，不允许添加形状
        if (shapeLayer.locked) {
            return
        }
        shapeLayer.addShape(key, displayShape, originalShape)
    }

    fun replaceShapesInActiveLayer(
        displayLines: SnapshotStateMap<Offset, Shape.Line>,
        originalLines: SnapshotStateMap<Offset, Shape.Line>,
        displayCircles: SnapshotStateMap<Offset, Shape.Circle>,
        originalCircles: SnapshotStateMap<Offset, Shape.Circle>,
        displayTriangles: SnapshotStateMap<Offset, Shape.Triangle>,
        originalTriangles: SnapshotStateMap<Offset, Shape.Triangle>,
        displayRectangles: SnapshotStateMap<Offset, Shape.Rectangle>,
        originalRectangles: SnapshotStateMap<Offset, Shape.Rectangle>,
        displayPolygons: SnapshotStateMap<Offset, Shape.Polygon>,
        originalPolygons: SnapshotStateMap<Offset, Shape.Polygon>,
        displayTexts: SnapshotStateMap<Offset, Shape.Text>,
        originalTexts: SnapshotStateMap<Offset, Shape.Text>
    ) {
        val shapeLayer = ensureActiveShapeLayer()
        // 如果形状层已锁定，不允许替换形状
        if (shapeLayer.locked) {
            return
        }
        shapeLayer.replaceAll(
            displayLines,
            originalLines,
            displayCircles,
            originalCircles,
            displayTriangles,
            originalTriangles,
            displayRectangles,
            originalRectangles,
            displayPolygons,
            originalPolygons,
            displayTexts,
            originalTexts
        )
    }

    /**
     * 将当前所有图层合成为 [androidx.compose.ui.graphics.ImageBitmap]。
     *
     * @param width 导出宽度（像素）
     * @param height 导出高度（像素）
     * @param density 当前绘制使用的密度
     * @param backgroundColor 可选背景色，默认为透明
     * @param layers 指定要导出的图层集合，默认为 LayerManager 当前图层快照
     */
    fun exportImageBitmap(
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
                val paint = Paint().apply {
                    color = backgroundColor
                }
                drawContext.canvas.drawRect(rect, paint)
            }
            layerRenderer.drawAll(this, layers)
        }

        return bitmap
    }

    /**
     * 将当前所有图层合成为 [java.awt.image.BufferedImage]。
     *
     * @param width 导出宽度（像素）
     * @param height 导出高度（像素）
     * @param density 当前绘制使用的密度
     * @param backgroundColor 可选背景色，默认为透明
     * @param layers 指定要导出的图层集合，默认为 LayerManager 当前图层快照
     */
    fun exportBufferedImage(
        width: Int,
        height: Int,
        density: Density,
        backgroundColor: Color = Color.Transparent,
        layers: List<Layer> = layerManager.layers.value
    ): BufferedImage {
        val bitmap = exportImageBitmap(width, height, density, backgroundColor, layers)
        return bitmap.toAwtImage()
    }

    /**
     * 更新图像层的位置（拖动）
     */
    fun updateImageLayerPosition(layerId: UUID, translation: Offset) {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer
        layer?.let {
            val currentTransform = it.transform
            it.updateTransform(
                currentTransform.copy(translation = translation)
            )
        }
    }

    /**
     * 获取当前激活的图像层
     */
    fun getActiveImageLayer(): ImageLayer? {
        val active = layerManager.activeLayer.value
        return active as? ImageLayer
    }
}

enum class EditorTool {
    SELECTION,
    SHAPE,
    IMAGE,
    MOVE
}