package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ShapeLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.animation.ShapeAnimationManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.coordinate.CoordinateConverter
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.handler.ShapeDrawingEventHandler
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state.ShapeDrawingState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.draggableTextField
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ShapeDrawingPropertiesMenuDialog
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.CanvasView
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.LayerPanel
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ControlPoint
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ControlPointType
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ImageLayerControlRenderer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ShapeInteractionHelper
import cn.netdiscovery.monica.ui.widget.color.ColorSelectionDialog
import cn.netdiscovery.monica.ui.widget.image.gesture.detectTransformGestures
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator
import cn.netdiscovery.monica.i18n.getCurrentStringResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.atan2

/**
 * 重构后的形状绘制视图
 * 通过模块化设计降低耦合度，提高可维护性
 * 实现模式一：绘制完成后颜色不变
 * 
 * @author Tony Shen
 * @date 2024/12/19
 * @version V1.0
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun shapeDrawing(state: ApplicationState) {
    val density = LocalDensity.current
    val i18nState = getCurrentStringResource()
    val editorController = remember { EditorController() }
    val focusRequester = remember { FocusRequester() }

    val drawingState = remember { ShapeDrawingState() }
    val animationManager = remember { ShapeAnimationManager() }
    
    // 观察激活图层状态
    val activeLayer by editorController.layerManager.activeLayer.collectAsState()

    val coordinateConverter = remember(state.currentImage, density.density) {
        val originalSize = ImageSizeCalculator.getImagePixelSize(state)
        val displaySize = ImageSizeCalculator.getImageDisplayPixelSize(state, density.density)
        val scaleX = if (originalSize != null && displaySize != null) {
            originalSize.first.toFloat() / displaySize.first.toFloat()
        } else 1f
        val scaleY = if (originalSize != null && displaySize != null) {
            originalSize.second.toFloat() / displaySize.second.toFloat()
        } else 1f
        CoordinateConverter(scaleX, scaleY)
    }

    val eventHandler = remember { ShapeDrawingEventHandler(drawingState, coordinateConverter) }

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showDraggableTextField by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var cropMode by remember { mutableStateOf(false) }

    val imageBitmap = state.currentImage?.toComposeImageBitmap() ?: run {
        logger.error("当前图像为空，无法进行绘制")
        return
    }

    fun syncShapeLayer() {
        editorController.replaceShapesInActiveLayer(
            drawingState.displayLines,
            drawingState.originalLines,
            drawingState.displayCircles,
            drawingState.originalCircles,
            drawingState.displayTriangles,
            drawingState.originalTriangles,
            drawingState.displayRectangles,
            drawingState.originalRectangles,
            drawingState.displayPolygons,
            drawingState.originalPolygons,
            drawingState.displayTexts,
            drawingState.originalTexts
        )
    }

    fun restoreDrawingStateFromLayers() {
        val shapeLayer = editorController.layerManager.layers.value.firstOrNull { it is ShapeLayer } as? ShapeLayer
        if (shapeLayer != null) {
            drawingState.replaceAllShapes(
                displayLines = shapeLayer.displayLines,
                originalLines = shapeLayer.originalLines,
                displayCircles = shapeLayer.displayCircles,
                originalCircles = shapeLayer.originalCircles,
                displayTriangles = shapeLayer.displayTriangles,
                originalTriangles = shapeLayer.originalTriangles,
                displayRectangles = shapeLayer.displayRectangles,
                originalRectangles = shapeLayer.originalRectangles,
                displayPolygons = shapeLayer.displayPolygons,
                originalPolygons = shapeLayer.originalPolygons,
                displayTexts = shapeLayer.displayTexts,
                originalTexts = shapeLayer.originalTexts
            )
        } else {
            drawingState.clearAllShapes()
        }
        animationManager.clearAllAnimations()
    }

    fun performUndo() {
        cropMode = false
        if (editorController.undo()) {
            restoreDrawingStateFromLayers()
        } else {
            state.showTray("没有可撤销的操作", "提示")
        }
    }

    fun performRedo() {
        cropMode = false
        if (editorController.redo()) {
            restoreDrawingStateFromLayers()
        } else {
            state.showTray("没有可重做的操作", "提示")
        }
    }

    fun deleteActiveLayer() {
        if (editorController.selectedLayerIds.value.isNotEmpty()) {
            if (!editorController.deleteSelectedLayers()) {
                state.showTray("当前没有可删除的选中图层", "提示")
            }
            return
        }
        val layer = activeLayer ?: return
        if (editorController.isBackgroundLayer(layer)) {
            state.showTray("无法删除背景图层", "提示")
            return
        }
        cropMode = false
        editorController.removeLayer(layer.id)
    }

    fun toggleActiveLayerLock() {
        val layer = activeLayer ?: return
        if (editorController.isBackgroundLayer(layer)) {
            state.showTray("背景图层不支持锁定切换", "提示")
            return
        }
        editorController.setLayerLocked(layer.id, !layer.locked)
    }

    fun moveActiveLayer(offset: Int) {
        val layer = activeLayer ?: return
        cropMode = false
        if (offset > 0) {
            editorController.moveLayerUp(layer.id)
        } else {
            editorController.moveLayerDown(layer.id)
        }
    }

    fun handleShortcut(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        val commandPressed = event.isMetaPressed || event.isCtrlPressed

        return when {
            commandPressed && event.key == Key.Z && event.isShiftPressed -> {
                performRedo()
                true
            }
            commandPressed && event.key == Key.Z -> {
                performUndo()
                true
            }
            commandPressed && event.key == Key.Y -> {
                performRedo()
                true
            }
            commandPressed && event.key == Key.L -> {
                toggleActiveLayerLock()
                true
            }
            commandPressed && event.key == Key.A -> {
                editorController.selectAllEditableLayers()
                true
            }
            commandPressed && event.key == Key.Escape -> {
                editorController.clearLayerSelection()
                true
            }
            commandPressed && event.key == Key.G -> {
                if (editorController.createGroupFromSelection() == null) {
                    state.showTray("请先至少选中 2 个图层", "提示")
                }
                true
            }
            commandPressed && event.isShiftPressed && event.key == Key.DirectionUp -> {
                moveActiveLayer(1)
                true
            }
            commandPressed && event.isShiftPressed && event.key == Key.DirectionDown -> {
                moveActiveLayer(-1)
                true
            }
            event.key == Key.Delete || event.key == Key.Backspace -> {
                deleteActiveLayer()
                true
            }
            else -> false
        }
    }

    LaunchedEffect(imageBitmap) {
        // 使用 EditorController 的统一方法管理背景层
        editorController.updateBackgroundLayer(imageBitmap)
    }

    LaunchedEffect(Unit) {
        editorController.ensureActiveShapeLayer()
        editorController.selectTool(EditorTool.SHAPE)
        syncShapeLayer()
        editorController.saveHistoryPoint()
        focusRequester.requestFocus()
    }

    val (width, height) = ImageSizeCalculator.calculateImageSize(state)
    val displaySize = ImageSizeCalculator.getImageDisplayPixelSize(state, density.density)
    val bitmapWidth = displaySize?.first ?: 0
    val bitmapHeight = displaySize?.second ?: 0

    if (bitmapWidth <= 0 || bitmapHeight <= 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material.Text(
                text = "请先加载图片",
                color = Color.Gray
            )
        }
        return
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent(::handleShortcut)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background,
                        MaterialTheme.colors.surface
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            LayerPanel(
                editorController = editorController,
                state = state,
                modifier = Modifier
                    .width(240.dp)
                    .fillMaxHeight()
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 图像层拖动状态
                    var imageLayerDragStart by remember { mutableStateOf<Offset?>(null) }
                    var imageLayerStartTranslation by remember { mutableStateOf<Offset>(Offset.Zero) }
                    
                    // 控制点交互状态
                    var activeControlPoint by remember { mutableStateOf<ControlPoint?>(null) }
                    var controlPointDragStart by remember { mutableStateOf<Offset?>(null) }
                    var initialTransform by remember { mutableStateOf<cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerTransform?>(null) }
                    var shapeDragLastPosition by remember { mutableStateOf<Offset?>(null) }
                    
                    val canvasModifier = Modifier
                        .width(width)
                        .height(height)
                        .padding(8.dp)
                        .shadow(1.dp)
                        .background(Color.White)
                        // 右键旋转（暂时移除滚轮缩放，后续可以添加）
                        .pointerInput(activeLayer?.id, bitmapWidth, bitmapHeight, width, height) {
                            val activeImageLayer = activeLayer as? ImageLayer
                            val canvasWidth = with(density) { width.toPx() }
                            val canvasHeight = with(density) { height.toPx() }
                            if (activeImageLayer != null && !activeImageLayer.locked && 
                                !editorController.isBackgroundLayer(activeImageLayer) &&
                                !cropMode) {
                                detectTransformGestures(
                                    panZoomLock = true, // 锁定平移和缩放，只允许旋转
                                    onGesture = { centroid, pan, zoom, rotation, mainPointer, _ ->
                                        // 检查是否是右键（secondary button）
                                        // 注意：PointerButton 在某些 Compose 版本中可能不可用，暂时允许所有旋转操作
                                        // TODO: 添加右键检测逻辑
                                        val currentTransform = activeImageLayer.transform
                                        val newRotation = currentTransform.rotation + rotation
                                        
                                        // 计算中心点作为 pivot
                                        val center = ImageLayerControlRenderer.calculateImageCenter(
                                            activeImageLayer,
                                            canvasWidth,
                                            canvasHeight
                                        ) ?: return@detectTransformGestures
                                        
                                        // pivot 应该在图像原始坐标系中，相对于图像中心
                                        // 图像中心在原始坐标系中是 (width/2, height/2)
                                        val imageBitmap = activeImageLayer.image
                                        val imageCenter = if (imageBitmap != null) {
                                            Offset(imageBitmap.width / 2f, imageBitmap.height / 2f)
                                        } else {
                                            Offset.Zero
                                        }
                                        
                                        editorController.updateImageLayerRotation(
                                            activeImageLayer.id,
                                            newRotation,
                                            imageCenter
                                        )
                                        mainPointer.consume()
                                    }
                                )
                            }
                        }
                        .dragMotionEvent(
                            onDragStart = { pointerInputChange ->
                                val activeImageLayer = activeLayer as? ImageLayer
                                
                                // 检查是否点击在控制点上
                                if (activeImageLayer != null && !activeImageLayer.locked && 
                                    !editorController.isBackgroundLayer(activeImageLayer)) {
                                    val canvasWidth = with(density) { width.toPx() }
                                    val canvasHeight = with(density) { height.toPx() }
                                    val hitControlPoint = ImageLayerControlRenderer.hitTestControlPoint(
                                        pointerInputChange.position,
                                        activeImageLayer,
                                        canvasWidth,
                                        canvasHeight,
                                        cropMode = cropMode
                                    )
                                    
                                    if (hitControlPoint != null) {
                                        if (cropMode && !ImageLayerControlRenderer.isCropControlPoint(hitControlPoint.type)) {
                                            return@dragMotionEvent
                                        }
                                        // 开始拖动控制点
                                        activeControlPoint = hitControlPoint
                                        controlPointDragStart = pointerInputChange.position
                                        initialTransform = activeImageLayer.transform.copy()
                                        pointerInputChange.consume()
                                        return@dragMotionEvent
                                    }
                                }
                                
                                // 如果激活图层是图像层且未锁定，则直接拖动图像层
                                if (activeImageLayer != null && !activeImageLayer.locked && !cropMode) {
                                    imageLayerDragStart = pointerInputChange.position
                                    imageLayerStartTranslation = activeImageLayer.transform.translation
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                
                                // 如果激活图层是形状层，检查是否锁定
                                if (!editorController.canDrawOnActiveShapeLayer()) {
                                    state.showTray("形状层已锁定，无法绘制", "提示")
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                val hitShape = ShapeInteractionHelper.findShapeAt(drawingState, pointerInputChange.position)
                                if (hitShape != null) {
                                    drawingState.selectExistingShape(hitShape.key, hitShape.type)
                                    shapeDragLastPosition = pointerInputChange.position
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                drawingState.clearSelectedShape()
                                eventHandler.handleMouseDown(pointerInputChange.position)
                                pointerInputChange.consume()
                            },
                            onDrag = { pointerInputChange ->
                                val activeImageLayer = activeLayer as? ImageLayer
                                
                                // 如果正在拖动控制点
                                if (activeImageLayer != null && activeControlPoint != null && controlPointDragStart != null && initialTransform != null) {
                                    val currentPos = pointerInputChange.position
                                    val startPos = controlPointDragStart!!
                                    val controlPoint = activeControlPoint!!
                                    
                                    when (controlPoint.type) {
                                        ControlPointType.ROTATION_HANDLE -> {
                                            // 拖动旋转手柄进行旋转
                                            val canvasWidth = with(density) { width.toPx() }
                                            val canvasHeight = with(density) { height.toPx() }
                                            val center = ImageLayerControlRenderer.calculateImageCenter(
                                                activeImageLayer,
                                                canvasWidth,
                                                canvasHeight
                                            ) ?: return@dragMotionEvent
                                            
                                            val startAngle = atan2(
                                                startPos.y - center.y,
                                                startPos.x - center.x
                                            )
                                            val currentAngle = atan2(
                                                currentPos.y - center.y,
                                                currentPos.x - center.x
                                            )
                                            val rotationDelta = Math.toDegrees((currentAngle - startAngle).toDouble()).toFloat()
                                            
                                            // pivot 应该在图像原始坐标系中，相对于图像中心
                                            // 图像中心在原始坐标系中是 (width/2, height/2)
                                            val imageBitmap = activeImageLayer.image
                                            val imageCenter = if (imageBitmap != null) {
                                                Offset(imageBitmap.width / 2f, imageBitmap.height / 2f)
                                            } else {
                                                Offset.Zero
                                            }
                                            val newRotation = initialTransform!!.rotation + rotationDelta
                                            
                                            editorController.updateImageLayerRotation(
                                                activeImageLayer.id,
                                                newRotation,
                                                imageCenter
                                            )
                                        }
                                        ControlPointType.CORNER_TOP_LEFT,
                                        ControlPointType.CORNER_TOP_RIGHT,
                                        ControlPointType.CORNER_BOTTOM_LEFT,
                                        ControlPointType.CORNER_BOTTOM_RIGHT -> {
                                            // 拖动角点进行缩放
                                            val canvasWidth = with(density) { width.toPx() }
                                            val canvasHeight = with(density) { height.toPx() }
                                            val center = ImageLayerControlRenderer.calculateImageCenter(
                                                activeImageLayer,
                                                canvasWidth,
                                                canvasHeight
                                            ) ?: return@dragMotionEvent
                                            
                                            val startDistance = (startPos - center).getDistance()
                                            val currentDistance = (currentPos - center).getDistance()
                                            
                                            if (startDistance > 0f) {
                                                val scaleFactor = currentDistance / startDistance
                                                val newScaleX = (initialTransform!!.scaleX * scaleFactor).coerceIn(0.1f, 10f)
                                                val newScaleY = (initialTransform!!.scaleY * scaleFactor).coerceIn(0.1f, 10f)
                                                
                                                // pivot 应该在图像原始坐标系中，相对于图像中心
                                                // 图像中心在原始坐标系中是 (width/2, height/2)
                                                val imageBitmap = activeImageLayer.image
                                                val imageCenter = if (imageBitmap != null) {
                                                    Offset(imageBitmap.width / 2f, imageBitmap.height / 2f)
                                                } else {
                                                    Offset.Zero
                                                }
                                                
                                                editorController.updateImageLayerScale(
                                                    activeImageLayer.id,
                                                    newScaleX,
                                                    newScaleY,
                                                    imageCenter
                                                )
                                            }
                                        }
                                        // 裁剪控制点暂时不处理，后续可以添加
                                        ControlPointType.CROP_TOP_LEFT,
                                        ControlPointType.CROP_TOP_RIGHT,
                                        ControlPointType.CROP_BOTTOM_LEFT,
                                        ControlPointType.CROP_BOTTOM_RIGHT,
                                        ControlPointType.CROP_TOP,
                                        ControlPointType.CROP_BOTTOM,
                                        ControlPointType.CROP_LEFT,
                                        ControlPointType.CROP_RIGHT -> {
                                            val canvasWidth = with(density) { width.toPx() }
                                            val canvasHeight = with(density) { height.toPx() }
                                            val backgroundSize = editorController.getBackgroundSize()
                                            val updatedCropRect = ImageLayerControlRenderer.updateCropRectFromControlPoint(
                                                layer = activeImageLayer,
                                                controlPointType = controlPoint.type,
                                                canvasPoint = currentPos,
                                                canvasWidth = canvasWidth,
                                                canvasHeight = canvasHeight,
                                                backgroundSize = backgroundSize
                                            )
                                            if (updatedCropRect != null) {
                                                editorController.updateImageLayerCrop(
                                                    activeImageLayer.id,
                                                    updatedCropRect,
                                                    recordHistory = false
                                                )
                                            }
                                        }
                                    }
                                    
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                
                                // 如果正在拖动图像层
                                if (activeImageLayer != null && imageLayerDragStart != null && !activeImageLayer.locked) {
                                    val canvasWidth = with(density) { width.toPx() }
                                    val canvasHeight = with(density) { height.toPx() }
                                    // 使用新的方法，传入画布坐标和尺寸
                                    editorController.updateImageLayerPosition(
                                        activeImageLayer.id,
                                        pointerInputChange.position,
                                        canvasWidth,
                                        canvasHeight
                                    )
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }

                                if (shapeDragLastPosition != null) {
                                    val previousPosition = shapeDragLastPosition ?: pointerInputChange.position
                                    val displayDelta = pointerInputChange.position - previousPosition
                                    val originalDelta = coordinateConverter.displayDeltaToOriginal(displayDelta)
                                    if (drawingState.moveSelectedShape(displayDelta, originalDelta)) {
                                        syncShapeLayer()
                                    }
                                    shapeDragLastPosition = pointerInputChange.position
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                
                                // 否则，处理形状绘制（仅在形状层激活时）
                                if (!editorController.canDrawOnActiveShapeLayer()) {
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                val currentShapes = eventHandler.handleMouseMove(pointerInputChange.position)
                                currentShapes.forEach { (key, shape) ->
                                    when (shape) {
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Line -> drawingState.displayLines[key] = shape
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Circle -> drawingState.displayCircles[key] = shape
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Triangle -> drawingState.displayTriangles[key] = shape
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Rectangle -> drawingState.displayRectangles[key] = shape
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Polygon -> drawingState.displayPolygons[key] = shape
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Text -> drawingState.displayTexts[key] = shape
                                    }
                                }
                                syncShapeLayer()
                                pointerInputChange.consume()
                            },
                            onDragEnd = { pointerInputChange ->
                                val activeImageLayer = activeLayer as? ImageLayer
                                
                                // 如果正在拖动控制点，结束拖动
                                if (activeImageLayer != null && activeControlPoint != null) {
                                    activeControlPoint = null
                                    controlPointDragStart = null
                                    initialTransform = null
                                    editorController.saveHistoryPoint()
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                
                                // 如果正在拖动图像层，结束拖动
                                if (activeImageLayer != null && imageLayerDragStart != null) {
                                    imageLayerDragStart = null
                                    editorController.saveHistoryPoint()
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }

                                if (shapeDragLastPosition != null) {
                                    shapeDragLastPosition = null
                                    syncShapeLayer()
                                    editorController.saveHistoryPoint()
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                
                                // 否则，处理形状绘制结束
                                if (!editorController.canDrawOnActiveShapeLayer()) {
                                    pointerInputChange.consume()
                                    return@dragMotionEvent
                                }
                                val result = eventHandler.handleMouseUp(pointerInputChange.position, bitmapWidth, bitmapHeight)
                                result?.let { (key, shape) ->
                                    val shapeType = when (shape) {
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Line -> "Line"
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Circle -> "Circle"
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Triangle -> "Triangle"
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Rectangle -> "Rectangle"
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Polygon -> "Polygon"
                                        is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Text -> "Text"
                                        else -> "Unknown"
                                    }
                                    animationManager.addAnimatedShape(shapeType, key)
                                }
                                syncShapeLayer()
                                editorController.saveHistoryPoint()
                                pointerInputChange.consume()
                            }
                        )

                    CanvasView(
                        editorController = editorController,
                        drawingState = drawingState,
                        animationManager = animationManager,
                        modifier = canvasModifier,
                        cropMode = cropMode,
                        overlay = {
                            ShapeInteractionHelper.drawSelection(this, drawingState)
                        }
                    )
                }
                
                // 将 TextInputDialog 放在画布所在的 Box 中，使其相对于画布居中
                if (showDraggableTextField) {
                    // 计算画布的实际显示尺寸（像素），应该等于 bitmapWidth 和 bitmapHeight
                    val canvasDisplayWidthPx = bitmapWidth.toFloat()
                    val canvasDisplayHeightPx = bitmapHeight.toFloat()
                    
                    TextInputDialog(
                        modifier = Modifier.width(250.dp).height(130.dp),
                        canvasWidthPx = canvasDisplayWidthPx,
                        canvasHeightPx = canvasDisplayHeightPx,
                        density = density,
                        currentText = drawingState.currentText,
                        currentShapeProperty = drawingState.currentShapeProperty,
                        onTextChanged = { drawingState.updateTextState(it) },
                        onDragged = { offset ->
                            // offset 是相对于画布中心的偏移（像素）
                            // 由于画布显示尺寸等于图像显示尺寸，offset 可以直接使用
                            val textPosition = CoordinateSystem.calculateTextPosition(
                                dragOffset = offset,
                                imageWidth = bitmapWidth,
                                imageHeight = bitmapHeight,
                                density = density,
                                textFieldWidth = 250f,
                                textFieldHeight = 130f,
                                fontSize = drawingState.currentShapeProperty.fontSize
                            )

                            val textValidation = CoordinateSystem.validateOffset(textPosition, bitmapWidth, bitmapHeight)
                            if (textValidation.isValid) {
                                val displayText = cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Text(
                                    textPosition,
                                    drawingState.currentText,
                                    drawingState.currentShapeProperty
                                )
                                val originalText = coordinateConverter.convertTextToOriginal(displayText)
                                drawingState.addShape(textPosition, displayText, originalText)
                                drawingState.recordLastDrawnShape(textPosition, "Text")
                                logger.info("添加文字: '${drawingState.currentText}' 在位置 $textPosition")

                                syncShapeLayer()
                                drawingState.updateTextState("")
                            } else {
                                logger.warn("文本位置无效: ${textValidation.message}")
                            }
                            showDraggableTextField = false
                        }
                    )
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {
            
            toolTipButton(
                text = i18nState.get("select_color"),
                painter = painterResource("images/doodle/color.png"),
                onClick = { showColorDialog = true }
            )

            toolTipButton(
                text = i18nState.get("change_properties"),
                painter = painterResource("images/doodle/brush.png"),
                onClick = { showPropertiesDialog = true }
            )

            toolTipButton(
                text = if (cropMode) "退出裁剪" else "裁剪图层",
                painter = painterResource("images/controlpanel/crop.png"),
                onClick = {
                    val activeImageLayer = activeLayer as? ImageLayer
                    if (activeImageLayer == null || editorController.isBackgroundLayer(activeImageLayer)) {
                        state.showTray("请先选中一个可编辑的图像层", "提示")
                        return@toolTipButton
                    }
                    if (cropMode) {
                        cropMode = false
                        editorController.saveHistoryPoint()
                    } else {
                        ImageLayerControlRenderer.initializeCropRect(activeImageLayer)
                        cropMode = true
                    }
                }
            )

            ShapeSelectionButtons(drawingState)

            toolTipButton(
                text = i18nState.get("add_text"),
                painter = painterResource("images/shapedrawing/text.png"),
                onClick = { showDraggableTextField = true }
            )

            toolTipButton(
                text = "撤销",
                painter = painterResource("images/doodle/previous_step.png"),
                onClick = ::performUndo
            )

            toolTipButton(
                text = "重做",
                painter = painterResource("images/doodle/revoke.png"),
                onClick = ::performRedo
            )

            toolTipButton(
                text = i18nState.get("clear"),
                painter = painterResource("images/doodle/clear.png"),
                onClick = {
                    cropMode = false
                    drawingState.clearAllShapes()
                    animationManager.clearAllAnimations()
                    syncShapeLayer()
                    editorController.saveHistoryPoint()
                }
            )

            toolTipButton(
                text = i18nState.get("save"),
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    showSaveDialog = true
                }
            )
        }

        if (showColorDialog) {
            ColorSelectionDialog(
                drawingState.currentShapeProperty.color,
                onDismiss = { showColorDialog = false },
                onNegativeClick = { showColorDialog = false },
                onPositiveClick = { color: Color ->
                    showColorDialog = false
                    drawingState.updateColor(color)
                    logger.info("颜色已更改: ${color} (仅影响新绘制的形状)")
                }
            )
        }


        if (showPropertiesDialog) {
            ShapeDrawingPropertiesMenuDialog(drawingState.currentShapeProperty) { updatedProperties ->
                drawingState.updateShapeProperty(updatedProperties)
                logger.info("属性已更新: fontSize=${updatedProperties.fontSize}, alpha=${updatedProperties.alpha} (仅影响新绘制的形状)")
                showPropertiesDialog = false
            }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { androidx.compose.material.Text("导出方式") },
                text = {
                    androidx.compose.material.Text("选择导出当前显示尺寸，或导出原始图片分辨率。原始分辨率导出会优先使用形状的原始坐标。")
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                val displaySize = ImageSizeCalculator.getImageDisplayPixelSize(state, density.density)
                                val current = state.currentImage
                                if (displaySize != null && current != null) {
                                    cropMode = false
                                    val paddingPx = with(density) { (8.dp * 2).toPx() }
                                    val actualCanvasWidth = (displaySize.first - paddingPx).toInt().coerceAtLeast(1)
                                    val actualCanvasHeight = (displaySize.second - paddingPx).toInt().coerceAtLeast(1)
                                    val flattened = editorController.exportBufferedImage(
                                        width = actualCanvasWidth,
                                        height = actualCanvasHeight,
                                        density = density
                                    )
                                    state.addQueue(current)
                                    state.currentImage = flattened
                                    state.closePreviewWindow()
                                } else {
                                    logger.warn("当前无法导出显示尺寸：缺少有效图像")
                                }
                                showSaveDialog = false
                            }
                        ) {
                            androidx.compose.material.Text("显示尺寸")
                        }
                        TextButton(
                            onClick = {
                                val pixelSize = ImageSizeCalculator.getImagePixelSize(state)
                                val current = state.currentImage
                                if (pixelSize != null && current != null) {
                                    cropMode = false
                                    val flattened = editorController.exportBufferedImage(
                                        width = pixelSize.first,
                                        height = pixelSize.second,
                                        density = density,
                                        useOriginalShapeCoordinates = true
                                    )
                                    state.addQueue(current)
                                    state.currentImage = flattened
                                    state.closePreviewWindow()
                                } else {
                                    logger.warn("当前无法导出原始尺寸：缺少有效图像")
                                }
                                showSaveDialog = false
                            }
                        ) {
                            androidx.compose.material.Text("原始尺寸")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        androidx.compose.material.Text("取消")
                    }
                }
            )
        }
    }
}

/**
 * 形状选择按钮组
 */
@Composable
private fun ShapeSelectionButtons(drawingState: ShapeDrawingState) {
    val i18nState = getCurrentStringResource()
    val shapes = listOf(
        Triple(ShapeEnum.Line, "images/shapedrawing/line.png", i18nState.get("line")),
        Triple(ShapeEnum.Circle, "images/shapedrawing/circle.png", i18nState.get("circle")),
        Triple(ShapeEnum.Triangle, "images/shapedrawing/triangle.png", i18nState.get("triangle")),
        Triple(ShapeEnum.Rectangle, "images/shapedrawing/rectangle.png", i18nState.get("rectangle")),
        Triple(ShapeEnum.Polygon, "images/shapedrawing/polygon.png", i18nState.get("polygon"))
    )
    
    shapes.forEach { (shape, icon, text) ->
        toolTipButton(
            text = text,
            painter = painterResource(icon),
            onClick = {
                drawingState.selectShape(shape)
            }
        )
    }
}

/**
 * 文字输入对话框组件
 */
@Composable
private fun TextInputDialog(
    modifier: Modifier,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    density: androidx.compose.ui.unit.Density,
    currentText: String,
    currentShapeProperty: ShapeProperties,
    onTextChanged: (String) -> Unit,
    onDragged: (Offset) -> Unit
) {
    draggableTextField(
        modifier = modifier,
        canvasWidthPx = canvasWidthPx,
        canvasHeightPx = canvasHeightPx,
        density = density,
        text = currentText,
        onTextChanged = onTextChanged,
        onDragged = onDragged
    )
}
