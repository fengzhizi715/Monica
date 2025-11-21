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
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerTransform
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerType
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ShapeLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import java.awt.image.BufferedImage
import java.util.UUID
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * EditorController 负责协调 LayerManager、LayerRenderer 以及导出逻辑，
 * 同时记录当前工具与激活图层信息，供 UI 直接调用。
 */
class EditorController(
    val layerManager: LayerManager = LayerManager()
) {
    
    private val logger: Logger = LoggerFactory.getLogger(EditorController::class.java)
    
    // 缓存背景层引用，避免每次查找都遍历列表
    private var cachedBackgroundLayer: ImageLayer? = null
    private var cachedBackgroundLayerId: UUID? = null

    companion object {
        /**
         * 限制最多创建的形状层数量
         * 方案一（简化设计）：限制为1个形状层，保留多个图像层
         */
        private const val MAX_SHAPE_LAYERS = 1
        
        /**
         * 背景图层名称常量，统一管理，避免硬编码
         */
        const val BACKGROUND_LAYER_NAME = "背景图层"
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
        if (isBackgroundLayer(id)) {
            // 背景层不应该被删除，记录警告但不执行删除
            logger.warn("尝试删除背景层，操作被阻止")
            return
        }
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
    /**
     * 更新图像层的位置
     * @param layerId 图层ID
     * @param canvasPosition 画布坐标位置（用户拖动的目标位置）
     * @param canvasWidth 画布宽度
     * @param canvasHeight 画布高度
     */
    fun updateImageLayerPosition(layerId: UUID, canvasPosition: Offset, canvasWidth: Float, canvasHeight: Float) {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer
        layer?.let {
            val bitmap = it.image ?: return
            
            // 计算适应和居中后的尺寸
            val scaleX = canvasWidth / bitmap.width
            val scaleY = canvasHeight / bitmap.height
            val fitScale = minOf(scaleX, scaleY).coerceAtMost(1f)
            
            val scaledWidth = bitmap.width * fitScale
            val scaledHeight = bitmap.height * fitScale
            
            val centerOffsetX = (canvasWidth - scaledWidth) / 2f
            val centerOffsetY = (canvasHeight - scaledHeight) / 2f
            
            // 适应后图像的中心点（在画布坐标系中，不考虑用户平移）
            val adaptedImageCenter = Offset(
                centerOffsetX + scaledWidth / 2f,
                centerOffsetY + scaledHeight / 2f
            )
            
            // 计算画布坐标中的偏移（用户想要移动到的位置相对于适应后图像中心的偏移）
            val canvasOffset = canvasPosition - adaptedImageCenter
            
            // 将画布坐标的偏移转换为图像原始坐标系中的偏移
            // 因为在 withTransform 中，translation 是在 fitScale 之前应用的
            // 所以 translation 应该在图像原始坐标系中
            // 变换顺序：用户平移(translation) -> fitScale -> centerOffset
            // 因此：canvasOffset = translation * fitScale
            // 所以：translation = canvasOffset / fitScale
            val translation = Offset(
                canvasOffset.x / fitScale,
                canvasOffset.y / fitScale
            )
            
            val currentTransform = it.transform
            it.updateTransform(
                currentTransform.copy(translation = translation)
            )
        }
    }
    
    /**
     * 更新图像层的位置（使用相对偏移）
     * @param layerId 图层ID
     * @param translation 相对于适应后图像中心的偏移（在适应后的坐标系中）
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
     * 更新图像层的旋转角度
     */
    fun updateImageLayerRotation(layerId: UUID, rotation: Float, pivot: Offset) {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer
        layer?.let {
            val currentTransform = it.transform
            it.updateTransform(
                currentTransform.copy(rotation = rotation, pivot = pivot)
            )
        }
    }

    /**
     * 更新图像层的缩放比例
     */
    fun updateImageLayerScale(layerId: UUID, scaleX: Float, scaleY: Float, pivot: Offset) {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer
        layer?.let {
            val currentTransform = it.transform
            it.updateTransform(
                currentTransform.copy(scaleX = scaleX, scaleY = scaleY, pivot = pivot)
            )
        }
    }

    /**
     * 更新图像层的完整变换
     */
    fun updateImageLayerTransform(layerId: UUID, transform: LayerTransform) {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer
        layer?.let {
            it.updateTransform(transform)
        }
    }
    
    /**
     * 更新图像层的裁剪区域
     * @param layerId 图层ID
     * @param cropRect 裁剪区域（在图像坐标系中，null表示取消裁剪）
     */
    fun updateImageLayerCrop(layerId: UUID, cropRect: Rect?) {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer
        layer?.let {
            val currentTransform = it.transform
            it.updateTransform(
                currentTransform.copy(cropRect = cropRect)
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
    
    // ==================== 背景层管理方法 ====================
    
    /**
     * 检查指定图层是否为背景层
     */
    fun isBackgroundLayer(layer: Layer): Boolean {
        return layer.name == BACKGROUND_LAYER_NAME && layer is ImageLayer
    }
    
    /**
     * 检查指定图层是否为背景层（通过 ID）
     */
    fun isBackgroundLayer(layerId: UUID): Boolean {
        val layer = layerManager.getLayerById(layerId)
        return layer != null && isBackgroundLayer(layer)
    }
    
    /**
     * 清除背景层缓存（当图层被删除或修改时调用）
     */
    private fun clearBackgroundLayerCache() {
        cachedBackgroundLayer = null
        cachedBackgroundLayerId = null
    }
    
    /**
     * 验证并更新背景层缓存
     * 在获取背景层时自动调用，确保缓存有效性
     */
    private fun validateBackgroundLayerCache(): ImageLayer? {
        val currentId = cachedBackgroundLayerId
        if (currentId != null) {
            val currentLayer = layerManager.getLayerById(currentId)
            if (currentLayer is ImageLayer && isBackgroundLayer(currentLayer)) {
                return currentLayer
            } else {
                // 缓存失效，清除
                clearBackgroundLayerCache()
            }
        }
        return null
    }
    
    /**
     * 获取背景层，如果不存在则返回 null
     * 使用缓存机制优化性能，自动验证缓存有效性
     */
    fun getBackgroundLayer(): ImageLayer? {
        // 先验证缓存是否有效
        val cached = validateBackgroundLayerCache()
        if (cached != null) {
            return cached
        }
        
        // 缓存失效或不存在，重新查找
        val found = layerManager.layers.value
            .firstOrNull { isBackgroundLayer(it) } as? ImageLayer
        
        cachedBackgroundLayer = found
        cachedBackgroundLayerId = found?.id
        return found
    }
    
    /**
     * 获取或创建背景层
     * 如果不存在则创建新的背景层并添加到索引 0
     */
    fun getOrCreateBackgroundLayer(image: ImageBitmap): ImageLayer {
        val existing = getBackgroundLayer()
        if (existing != null) {
            return existing
        }
        
        val newLayer = ImageLayer(BACKGROUND_LAYER_NAME, image)
        layerManager.addLayer(newLayer, index = 0)
        cachedBackgroundLayer = newLayer
        cachedBackgroundLayerId = newLayer.id
        return newLayer
    }
    
    /**
     * 更新背景层图像
     * 如果背景层不存在，则创建新的
     */
    fun updateBackgroundLayer(image: ImageBitmap) {
        val layer = getOrCreateBackgroundLayer(image)
        layer.updateImage(image)
    }
    
    /**
     * 检查是否存在背景层
     */
    fun hasBackgroundLayer(): Boolean {
        return getBackgroundLayer() != null
    }
    
    /**
     * 移除背景层（谨慎使用，通常不应该删除背景层）
     * 此方法主要用于清理或重置场景
     */
    fun removeBackgroundLayer(): Boolean {
        val backgroundLayer = getBackgroundLayer() ?: return false
        val removed = layerManager.removeLayer(backgroundLayer.id)
        if (removed != null) {
            clearBackgroundLayerCache()
            return true
        }
        return false
    }
    
    /**
     * 将图层上移一层（防止移动背景层）
     */
    fun moveLayerUp(layerId: UUID): Boolean {
        if (isBackgroundLayer(layerId)) {
            logger.warn("尝试移动背景层，操作被阻止")
            return false
        }
        return layerManager.moveLayerUp(layerId)
    }
    
    /**
     * 将图层下移一层（防止移动背景层）
     */
    fun moveLayerDown(layerId: UUID): Boolean {
        if (isBackgroundLayer(layerId)) {
            logger.warn("尝试移动背景层，操作被阻止")
            return false
        }
        return layerManager.moveLayerDown(layerId)
    }
}

enum class EditorTool {
    SELECTION,
    SHAPE,
    IMAGE,
    MOVE
}