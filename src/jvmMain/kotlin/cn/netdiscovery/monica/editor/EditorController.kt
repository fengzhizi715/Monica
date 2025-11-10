package cn.netdiscovery.monica.editor

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.editor.layer.ImageLayer
import cn.netdiscovery.monica.editor.layer.Layer
import cn.netdiscovery.monica.editor.layer.LayerManager
import cn.netdiscovery.monica.editor.layer.LayerRenderer
import cn.netdiscovery.monica.editor.layer.LayerType
import cn.netdiscovery.monica.editor.layer.ShapeLayer
import cn.netdiscovery.monica.export.ExportManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
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
    val exportManager = ExportManager(layerManager, layerRenderer)

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
        image: androidx.compose.ui.graphics.ImageBitmap?,
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

    fun addShapeToActiveLayer(key: Offset, displayShape: Shape, originalShape: Shape) {
        val shapeLayer = ensureActiveShapeLayer()
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

    fun exportImageBitmap(
        width: Int,
        height: Int,
        density: androidx.compose.ui.unit.Density,
        backgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
        layers: List<Layer> = layerManager.layers.value
    ) = exportManager.flattenToBitmap(width, height, density, backgroundColor, layers)

    fun exportBufferedImage(
        width: Int,
        height: Int,
        density: androidx.compose.ui.unit.Density,
        backgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
        layers: List<Layer> = layerManager.layers.value
    ) = exportManager.flattenToBufferedImage(width, height, density, backgroundColor, layers)
}

enum class EditorTool {
    SELECTION,
    SHAPE,
    IMAGE,
    MOVE
}

