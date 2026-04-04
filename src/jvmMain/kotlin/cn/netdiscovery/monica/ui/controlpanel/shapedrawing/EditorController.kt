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
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.AdjustmentLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayerMask
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayerMaskShape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.Layer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerAdjustment
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerGroup
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerRenderer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerTransform
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerType
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerBlendMode
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ShapeLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ShapeLayer.ShapeLayerSnapshot
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.SpecialLayerHelper
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import java.awt.image.BufferedImage
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    
    // 使用 SpecialLayerHelper 来管理背景层，集中处理背景层相关逻辑
    private val specialLayerHelper = SpecialLayerHelper(layerManager, BACKGROUND_LAYER_NAME)
    private val undoStack = mutableListOf<EditorSnapshot>()
    private val redoStack = mutableListOf<EditorSnapshot>()
    private val _selectedLayerIds = MutableStateFlow<Set<UUID>>(emptySet())
    val selectedLayerIds: StateFlow<Set<UUID>> = _selectedLayerIds.asStateFlow()
    private val _layerGroups = MutableStateFlow<List<LayerGroup>>(emptyList())
    val layerGroups: StateFlow<List<LayerGroup>> = _layerGroups.asStateFlow()

    init {
        saveHistoryPoint()
    }

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

    fun saveHistoryPoint() {
        val snapshot = captureSnapshot()
        if (undoStack.lastOrNull() == snapshot) return
        undoStack.add(snapshot)
        if (undoStack.size > 100) {
            undoStack.removeAt(0)
        }
        redoStack.clear()
    }

    fun canUndo(): Boolean = undoStack.size > 1

    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun undo(): Boolean {
        if (!canUndo()) return false
        val current = undoStack.removeLast()
        redoStack.add(current)
        restoreSnapshot(undoStack.last())
        return true
    }

    fun redo(): Boolean {
        val snapshot = redoStack.removeLastOrNull() ?: return false
        restoreSnapshot(snapshot)
        undoStack.add(snapshot)
        return true
    }

    fun addLayer(layer: Layer, index: Int? = null) {
        if (isBackgroundLayer(layer)) {
            val existingBackground = getBackgroundLayer()
            when {
                existingBackground == null -> layerManager.addLayer(layer, index = 0)
                existingBackground.id == layer.id -> layerManager.moveLayerTo(layer.id, 0)
                layer is ImageLayer -> existingBackground.updateImage(layer.image)
            }
            saveHistoryPoint()
            return
        }

        val insertIndex = normalizeNonBackgroundInsertIndex(index)
        layerManager.addLayer(layer, insertIndex)
        saveHistoryPoint()
    }

    fun createImageLayer(
        name: String,
        image: ImageBitmap?,
        index: Int? = null
    ): ImageLayer {
        if (name == BACKGROUND_LAYER_NAME && image != null) {
            return getOrCreateBackgroundLayer(image).also { updateBackgroundLayer(image) }
        }

        val layer = ImageLayer(name = name, image = image)
        layerManager.addLayer(layer, normalizeNonBackgroundInsertIndex(index))
        saveHistoryPoint()
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
        if (layerManager.removeLayer(id) != null) {
            _selectedLayerIds.value = _selectedLayerIds.value - id
            pruneEmptyGroups()
            saveHistoryPoint()
        }
    }

    fun renameLayer(id: UUID, newName: String): Boolean {
        if (isBackgroundLayer(id)) {
            logger.warn("尝试重命名背景层，操作被阻止")
            return false
        }
        if (newName == BACKGROUND_LAYER_NAME) {
            logger.warn("尝试将普通图层重命名为背景图层，操作被阻止")
            return false
        }
        val updated = layerManager.renameLayer(id, newName)
        if (updated) saveHistoryPoint()
        return updated
    }

    fun setLayerVisibility(id: UUID, visible: Boolean): Boolean {
        val updated = layerManager.setLayerVisibility(id, visible)
        if (updated) saveHistoryPoint()
        return updated
    }

    fun setLayerLocked(id: UUID, locked: Boolean): Boolean {
        val updated = layerManager.setLayerLocked(id, locked)
        if (updated) saveHistoryPoint()
        return updated
    }

    fun setLayerOpacity(id: UUID, opacity: Float): Boolean {
        val updated = layerManager.setLayerOpacity(id, opacity)
        if (updated) saveHistoryPoint()
        return updated
    }

    fun setLayerBlendMode(id: UUID, blendMode: LayerBlendMode): Boolean {
        val updated = layerManager.setLayerBlendMode(id, blendMode)
        if (updated) saveHistoryPoint()
        return updated
    }

    fun clearLayers() {
        layerManager.clear()
        clearLayerSelection()
        _layerGroups.value = emptyList()
        saveHistoryPoint()
    }

    fun setActiveLayer(id: UUID?) {
        layerManager.setActiveLayer(id)
    }

    fun toggleLayerSelection(id: UUID) {
        _selectedLayerIds.value = _selectedLayerIds.value.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
    }

    fun clearLayerSelection() {
        _selectedLayerIds.value = emptySet()
    }

    fun selectAllEditableLayers() {
        _selectedLayerIds.value = layerManager.layers.value
            .filterNot(::isBackgroundLayer)
            .mapTo(linkedSetOf()) { it.id }
    }

    fun isLayerSelected(id: UUID): Boolean = id in _selectedLayerIds.value

    fun getSelectedLayers(): List<Layer> {
        val selected = _selectedLayerIds.value
        return layerManager.layers.value.filter { it.id in selected }
    }

    fun deleteSelectedLayers(): Boolean {
        val selectedIds = _selectedLayerIds.value.filterNot(::isBackgroundLayer)
        if (selectedIds.isEmpty()) return false
        var removed = false
        selectedIds.forEach { id ->
            removed = layerManager.removeLayer(id) != null || removed
        }
        if (removed) {
            clearLayerSelection()
            saveHistoryPoint()
        }
        return removed
    }

    fun setSelectedLayersVisibility(visible: Boolean): Boolean =
        updateSelectedLayers { setLayerVisibility(it.id, visible) }

    fun setSelectedLayersLocked(locked: Boolean): Boolean =
        updateSelectedLayers { setLayerLocked(it.id, locked) }

    fun createAdjustmentLayer(
        name: String = "调整层",
        targetLayerIds: Set<UUID> = deriveAdjustmentTargets()
    ): AdjustmentLayer {
        val layer = AdjustmentLayer(name = name, targetLayerIds = targetLayerIds)
        layerManager.addLayer(layer, normalizeNonBackgroundInsertIndex(null))
        saveHistoryPoint()
        return layer
    }

    fun updateAdjustmentLayer(layerId: UUID, adjustment: LayerAdjustment): Boolean {
        val layer = layerManager.getLayerById(layerId) as? AdjustmentLayer ?: return false
        layer.updateAdjustment(adjustment)
        saveHistoryPoint()
        return true
    }

    fun updateAdjustmentLayerTargets(layerId: UUID, targetLayerIds: Set<UUID>): Boolean {
        val layer = layerManager.getLayerById(layerId) as? AdjustmentLayer ?: return false
        layer.updateTargets(targetLayerIds.filterNot(::isBackgroundLayer).toSet())
        saveHistoryPoint()
        return true
    }

    fun createGroupFromSelection(name: String = nextGroupName()): LayerGroup? {
        val selectedLayers = getSelectedLayers()
        if (selectedLayers.size < 2) return null

        val group = LayerGroup(name = name)
        _layerGroups.value = _layerGroups.value + group
        selectedLayers.forEach { layerManager.setLayerGroup(it.id, group.id) }
        moveGroupMembersTogether(group.id)
        saveHistoryPoint()
        return group
    }

    fun ungroup(groupId: UUID): Boolean {
        val members = getGroupLayers(groupId)
        if (members.isEmpty()) return false
        members.forEach { layerManager.setLayerGroup(it.id, null) }
        _layerGroups.value = _layerGroups.value.filterNot { it.id == groupId }
        saveHistoryPoint()
        return true
    }

    fun renameGroup(groupId: UUID, name: String): Boolean {
        val current = _layerGroups.value.firstOrNull { it.id == groupId } ?: return false
        if (current.name == name) return false
        _layerGroups.value = _layerGroups.value.map { if (it.id == groupId) it.copy(name = name) else it }
        saveHistoryPoint()
        return true
    }

    fun getGroup(groupId: UUID?): LayerGroup? {
        if (groupId == null) return null
        return _layerGroups.value.firstOrNull { it.id == groupId }
    }

    fun getGroupLayers(groupId: UUID): List<Layer> =
        layerManager.layers.value.filter { it.groupId == groupId }

    fun setGroupVisibility(groupId: UUID, visible: Boolean): Boolean =
        updateLayersWithoutHistory(getGroupLayers(groupId)) {
            layerManager.setLayerVisibility(it.id, visible)
        }.also { if (it) saveHistoryPoint() }

    fun setGroupLocked(groupId: UUID, locked: Boolean): Boolean =
        updateLayersWithoutHistory(getGroupLayers(groupId)) {
            layerManager.setLayerLocked(it.id, locked)
        }.also { if (it) saveHistoryPoint() }

    fun moveGroupUp(groupId: UUID): Boolean = moveGroup(groupId, up = true)

    fun moveGroupDown(groupId: UUID): Boolean = moveGroup(groupId, up = false)

    fun setImageLayerMask(layerId: UUID, mask: ImageLayerMask?, recordHistory: Boolean = true): Boolean {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer ?: return false
        layer.updateTransform(layer.transform.copy(mask = mask))
        if (recordHistory) saveHistoryPoint()
        return true
    }

    fun createCenteredMask(layerId: UUID, shape: ImageLayerMaskShape): Boolean {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer ?: return false
        val bitmap = layer.image ?: return false
        val insetX = bitmap.width * 0.1f
        val insetY = bitmap.height * 0.1f
        return setImageLayerMask(
            layerId,
            ImageLayerMask(
                rect = Rect(
                    left = insetX,
                    top = insetY,
                    right = bitmap.width.toFloat() - insetX,
                    bottom = bitmap.height.toFloat() - insetY
                ),
                shape = shape
            )
        )
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
        layers: List<Layer> = layerManager.layers.value,
        useOriginalShapeCoordinates: Boolean = false
    ): ImageBitmap {
        val bitmap = ImageBitmap(width, height)
        val canvas = Canvas(bitmap)
        val drawScope = CanvasDrawScope()
        val size = Size(width.toFloat(), height.toFloat())
        val exportLayers = if (useOriginalShapeCoordinates) {
            layers.map(::cloneLayerWithOriginalShapes)
        } else {
            layers
        }

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
            layerRenderer.drawAll(this, exportLayers)
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
        layers: List<Layer> = layerManager.layers.value,
        useOriginalShapeCoordinates: Boolean = false
    ): BufferedImage {
        val bitmap = exportImageBitmap(
            width = width,
            height = height,
            density = density,
            backgroundColor = backgroundColor,
            layers = layers,
            useOriginalShapeCoordinates = useOriginalShapeCoordinates
        )
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
    fun updateImageLayerCrop(layerId: UUID, cropRect: Rect?, recordHistory: Boolean = true) {
        val layer = layerManager.getLayerById(layerId) as? ImageLayer
        layer?.let {
            val currentTransform = it.transform
            it.updateTransform(
                currentTransform.copy(cropRect = cropRect)
            )
            if (recordHistory) {
                saveHistoryPoint()
            }
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
     * 获取背景层，如果不存在则返回 null
     */
    fun getBackgroundLayer(): ImageLayer? {
        return specialLayerHelper.getBackgroundLayer()
    }
    
    /**
     * 获取或创建背景层
     * 如果不存在则创建新的背景层并添加到索引 0
     */
    fun getOrCreateBackgroundLayer(image: ImageBitmap): ImageLayer {
        return specialLayerHelper.getOrCreateBackgroundLayer(image)
    }
    
    /**
     * 更新背景层图像
     * 如果背景层不存在，则创建新的
     */
    fun updateBackgroundLayer(image: ImageBitmap) {
        specialLayerHelper.updateBackgroundLayer(image)
        saveHistoryPoint()
    }
    
    /**
     * 检查是否存在背景层
     */
    fun hasBackgroundLayer(): Boolean {
        return specialLayerHelper.hasBackgroundLayer()
    }
    
    /**
     * 移除背景层（谨慎使用，通常不应该删除背景层）
     * 此方法主要用于清理或重置场景
     */
    fun removeBackgroundLayer(): Boolean {
        val removed = specialLayerHelper.removeBackgroundLayer()
        if (removed) saveHistoryPoint()
        return removed
    }
    
    /**
     * 获取背景层的尺寸（如果存在）
     */
    fun getBackgroundSize(): Pair<Float, Float>? {
        return specialLayerHelper.getBackgroundSize()
    }
    
    /**
     * 将图层上移一层（防止移动背景层）
     */
    fun moveLayerUp(layerId: UUID): Boolean {
        val currentIndex = layerManager.layers.value.indexOfFirst { it.id == layerId }
        if (currentIndex == -1) return false
        return moveLayerTo(layerId, currentIndex + 1)
    }
    
    /**
     * 将图层下移一层（防止移动背景层）
     */
    fun moveLayerDown(layerId: UUID): Boolean {
        val currentIndex = layerManager.layers.value.indexOfFirst { it.id == layerId }
        if (currentIndex == -1) return false
        return moveLayerTo(layerId, currentIndex - 1)
    }

    fun moveLayerTo(layerId: UUID, index: Int): Boolean {
        if (isBackgroundLayer(layerId)) {
            logger.warn("尝试移动背景层，操作被阻止")
            return false
        }

        val currentLayers = layerManager.layers.value
        val currentIndex = currentLayers.indexOfFirst { it.id == layerId }
        if (currentIndex == -1) return false

        val minAllowedIndex = if (hasBackgroundLayer()) 1 else 0
        val targetIndex = index.coerceIn(minAllowedIndex, currentLayers.lastIndex.coerceAtLeast(minAllowedIndex))
        val moved = layerManager.moveLayerTo(layerId, targetIndex)
        if (moved) saveHistoryPoint()
        return moved
    }

    private fun normalizeNonBackgroundInsertIndex(index: Int?): Int? {
        val backgroundOffset = if (hasBackgroundLayer()) 1 else 0
        return index?.coerceAtLeast(backgroundOffset)
    }

    private inline fun updateSelectedLayers(action: (Layer) -> Boolean): Boolean {
        val selectedLayers = getSelectedLayers().filterNot(::isBackgroundLayer)
        if (selectedLayers.isEmpty()) return false
        val changed = updateLayersWithoutHistory(selectedLayers, action)
        if (changed) saveHistoryPoint()
        return changed
    }

    private inline fun updateLayersWithoutHistory(
        layers: List<Layer>,
        action: (Layer) -> Boolean
    ): Boolean {
        var changed = false
        layers.forEach { layer ->
            changed = action(layer) || changed
        }
        return changed
    }

    private fun deriveAdjustmentTargets(): Set<UUID> {
        val selectedTargets = getSelectedLayers()
            .filter { it.type != LayerType.ADJUSTMENT }
            .map { it.id }
            .toSet()
        if (selectedTargets.isNotEmpty()) return selectedTargets

        val activeTarget = layerManager.activeLayer.value
            ?.takeIf { it.type != LayerType.ADJUSTMENT && !isBackgroundLayer(it) }
            ?.id
        return activeTarget?.let(::setOf) ?: emptySet()
    }

    private fun moveGroupMembersTogether(groupId: UUID) {
        val currentLayers = layerManager.layers.value
        val memberIds = currentLayers.filter { it.groupId == groupId }.map { it.id }.toSet()
        if (memberIds.size < 2) return
        val insertAfter = currentLayers.indexOfLast { it.id in memberIds }
        val memberLayers = currentLayers.filter { it.id in memberIds }
        val remaining = currentLayers.filterNot { it.id in memberIds }.toMutableList()
        val normalizedInsertIndex = (insertAfter - memberLayers.size + 1).coerceIn(0, remaining.size)
        remaining.addAll(normalizedInsertIndex, memberLayers)
        layerManager.replaceLayers(remaining, layerManager.activeLayer.value?.id)
    }

    private fun moveGroup(groupId: UUID, up: Boolean): Boolean {
        val currentLayers = layerManager.layers.value
        val memberIds = currentLayers.filter { it.groupId == groupId }.map { it.id }.toSet()
        if (memberIds.isEmpty()) return false

        val memberLayers = currentLayers.filter { it.id in memberIds }
        val remaining = currentLayers.filterNot { it.id in memberIds }.toMutableList()
        val firstIndex = currentLayers.indexOfFirst { it.id in memberIds }
        val lastIndex = currentLayers.indexOfLast { it.id in memberIds }

        val insertIndex = if (up) {
            val nextNonMember = currentLayers.drop(lastIndex + 1).indexOfFirst { it.id !in memberIds }
            if (nextNonMember == -1) return false
            val absolute = lastIndex + 1 + nextNonMember
            remaining.indexOfFirst { it.id == currentLayers[absolute].id } + 1
        } else {
            val previousNonMember = currentLayers.take(firstIndex).indexOfLast { it.id !in memberIds }
            if (previousNonMember == -1) return false
            remaining.indexOfFirst { it.id == currentLayers[previousNonMember].id }
        }

        remaining.addAll(insertIndex.coerceIn(0, remaining.size), memberLayers)
        layerManager.replaceLayers(remaining, layerManager.activeLayer.value?.id)
        saveHistoryPoint()
        return true
    }

    private fun pruneEmptyGroups() {
        val currentGroupIds = layerManager.layers.value.mapNotNull { it.groupId }.toSet()
        if (_layerGroups.value.any { it.id !in currentGroupIds }) {
            _layerGroups.value = _layerGroups.value.filter { it.id in currentGroupIds }
        }
    }

    private fun nextGroupName(): String = "图层组 ${_layerGroups.value.size + 1}"

    private fun captureSnapshot(): EditorSnapshot {
        return EditorSnapshot(
            layers = layerManager.layers.value.map(::captureLayerSnapshot),
            groups = _layerGroups.value,
            activeLayerId = layerManager.activeLayer.value?.id
        )
    }

    private fun cloneLayerWithOriginalShapes(layer: Layer): Layer = when (layer) {
        is ShapeLayer -> ShapeLayer(
            name = layer.name,
            blendMode = layer.blendMode,
            groupId = layer.groupId,
            id = layer.id,
            visible = layer.visible,
            opacity = layer.opacity,
            locked = layer.locked
        ).apply {
            replaceAll(
                displayLines = layer.originalLines.toMap(),
                originalLines = layer.originalLines.toMap(),
                displayCircles = layer.originalCircles.toMap(),
                originalCircles = layer.originalCircles.toMap(),
                displayTriangles = layer.originalTriangles.toMap(),
                originalTriangles = layer.originalTriangles.toMap(),
                displayRectangles = layer.originalRectangles.toMap(),
                originalRectangles = layer.originalRectangles.toMap(),
                displayPolygons = layer.originalPolygons.toMap(),
                originalPolygons = layer.originalPolygons.toMap(),
                displayTexts = layer.originalTexts.toMap(),
                originalTexts = layer.originalTexts.toMap()
            )
        }
        else -> layer
    }

    private fun captureLayerSnapshot(layer: Layer): LayerSnapshot = when (layer) {
        is ImageLayer -> LayerSnapshot.Image(
            id = layer.id,
            name = layer.name,
            visible = layer.visible,
            opacity = layer.opacity,
            locked = layer.locked,
            blendMode = layer.blendMode,
            groupId = layer.groupId,
            image = layer.image,
            transform = layer.transform
        )
        is ShapeLayer -> LayerSnapshot.Shape(
            id = layer.id,
            name = layer.name,
            visible = layer.visible,
            opacity = layer.opacity,
            locked = layer.locked,
            blendMode = layer.blendMode,
            groupId = layer.groupId,
            snapshot = layer.snapshot()
        )
        is AdjustmentLayer -> LayerSnapshot.Adjustment(
            id = layer.id,
            name = layer.name,
            visible = layer.visible,
            opacity = layer.opacity,
            locked = layer.locked,
            blendMode = layer.blendMode,
            groupId = layer.groupId,
            adjustment = layer.adjustment,
            targetLayerIds = layer.targetLayerIds
        )
        else -> error("Unsupported layer type: ${layer::class.qualifiedName}")
    }

    private fun restoreSnapshot(snapshot: EditorSnapshot) {
        val restoredLayers = snapshot.layers.map { saved ->
            when (saved) {
                is LayerSnapshot.Image -> ImageLayer(
                    id = saved.id,
                    name = saved.name,
                    image = saved.image,
                    transform = saved.transform,
                    visible = saved.visible,
                    opacity = saved.opacity,
                    locked = saved.locked
                    ,
                    blendMode = saved.blendMode,
                    groupId = saved.groupId
                )
                is LayerSnapshot.Shape -> ShapeLayer(
                    name = saved.name,
                    id = saved.id,
                    visible = saved.visible,
                    opacity = saved.opacity,
                    locked = saved.locked,
                    blendMode = saved.blendMode,
                    groupId = saved.groupId
                ).apply {
                    restore(saved.snapshot)
                }
                is LayerSnapshot.Adjustment -> AdjustmentLayer(
                    name = saved.name,
                    id = saved.id,
                    visible = saved.visible,
                    opacity = saved.opacity,
                    locked = saved.locked,
                    blendMode = saved.blendMode,
                    groupId = saved.groupId,
                    adjustment = saved.adjustment,
                    targetLayerIds = saved.targetLayerIds
                )
            }
        }
        _layerGroups.value = snapshot.groups
        clearLayerSelection()
        layerManager.replaceLayers(restoredLayers, snapshot.activeLayerId)
        pruneEmptyGroups()
    }

    private data class EditorSnapshot(
        val layers: List<LayerSnapshot>,
        val groups: List<LayerGroup>,
        val activeLayerId: UUID?
    )

    private sealed interface LayerSnapshot {
        val id: UUID
        val name: String
        val visible: Boolean
        val opacity: Float
        val locked: Boolean
        val blendMode: LayerBlendMode
        val groupId: UUID?

        data class Image(
            override val id: UUID,
            override val name: String,
            override val visible: Boolean,
            override val opacity: Float,
            override val locked: Boolean,
            override val blendMode: LayerBlendMode,
            override val groupId: UUID?,
            val image: ImageBitmap?,
            val transform: LayerTransform
        ) : LayerSnapshot

        data class Shape(
            override val id: UUID,
            override val name: String,
            override val visible: Boolean,
            override val opacity: Float,
            override val locked: Boolean,
            override val blendMode: LayerBlendMode,
            override val groupId: UUID?,
            val snapshot: ShapeLayerSnapshot
        ) : LayerSnapshot

        data class Adjustment(
            override val id: UUID,
            override val name: String,
            override val visible: Boolean,
            override val opacity: Float,
            override val locked: Boolean,
            override val blendMode: LayerBlendMode,
            override val groupId: UUID?,
            val adjustment: LayerAdjustment,
            val targetLayerIds: Set<UUID>
        ) : LayerSnapshot
    }
}

enum class EditorTool {
    SELECTION,
    SHAPE,
    IMAGE,
    MOVE
}
