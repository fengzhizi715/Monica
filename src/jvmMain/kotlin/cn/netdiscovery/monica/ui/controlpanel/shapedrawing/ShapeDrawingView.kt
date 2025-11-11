package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.editor.EditorController
import cn.netdiscovery.monica.editor.EditorTool
import cn.netdiscovery.monica.editor.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.animation.ShapeAnimationManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.coordinate.CoordinateConverter
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.handler.ShapeDrawingEventHandler
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state.ShapeDrawingState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.draggableTextField
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ShapeDrawingPropertiesMenuDialog
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.canvas.CanvasView
import cn.netdiscovery.monica.ui.layer.LayerPanel
import cn.netdiscovery.monica.ui.widget.color.ColorSelectionDialog
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator
import cn.netdiscovery.monica.i18n.getCurrentStringResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

    val drawingState = remember { ShapeDrawingState() }
    val animationManager = remember { ShapeAnimationManager() }
    
    // 观察激活图层状态
    val activeLayer by editorController.layerManager.activeLayer.collectAsState()

    val coordinateConverter = remember {
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

    val imageBitmap = state.currentImage?.toComposeImageBitmap() ?: run {
        logger.error("当前图像为空，无法进行绘制")
        return
    }

    val backgroundLayer = remember { mutableStateOf<ImageLayer?>(null) }

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

    LaunchedEffect(imageBitmap) {
        val layer = backgroundLayer.value
        if (layer == null) {
            backgroundLayer.value = editorController.createImageLayer("背景图层", imageBitmap, index = 0)
        } else {
            layer.updateImage(imageBitmap)
        }
    }

    LaunchedEffect(Unit) {
        editorController.ensureActiveShapeLayer()
        editorController.selectTool(EditorTool.SHAPE)
        syncShapeLayer()
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
                    
                    val canvasModifier = Modifier
                        .width(width)
                        .height(height)
                        .padding(8.dp)
                        .shadow(1.dp)
                        .background(Color.White)
                        .dragMotionEvent(
                            onDragStart = { pointerInputChange ->
                                val activeImageLayer = activeLayer as? cn.netdiscovery.monica.editor.layer.ImageLayer
                                
                                // 如果激活图层是图像层且未锁定，则直接拖动图像层
                                if (activeImageLayer != null && !activeImageLayer.locked) {
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
                                eventHandler.handleMouseDown(pointerInputChange.position)
                                pointerInputChange.consume()
                            },
                            onDrag = { pointerInputChange ->
                                val activeImageLayer = activeLayer as? cn.netdiscovery.monica.editor.layer.ImageLayer
                                
                                // 如果正在拖动图像层
                                if (activeImageLayer != null && imageLayerDragStart != null && !activeImageLayer.locked) {
                                    val dragOffset = pointerInputChange.position - imageLayerDragStart!!
                                    val newTranslation = imageLayerStartTranslation + dragOffset
                                    editorController.updateImageLayerPosition(activeImageLayer.id, newTranslation)
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
                                
                                // 如果正在拖动图像层，结束拖动
                                if (activeImageLayer != null && imageLayerDragStart != null) {
                                    imageLayerDragStart = null
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
                                pointerInputChange.consume()
                            }
                        )

                    CanvasView(
                        editorController = editorController,
                        drawingState = drawingState,
                        animationManager = animationManager,
                        modifier = canvasModifier
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

            ShapeSelectionButtons(drawingState)

            toolTipButton(
                text = i18nState.get("add_text"),
                painter = painterResource("images/shapedrawing/text.png"),
                onClick = { showDraggableTextField = true }
            )

            toolTipButton(
                text = i18nState.get("clear"),
                painter = painterResource("images/doodle/clear.png"),
                onClick = {
                    drawingState.clearAllShapes()
                    animationManager.clearAllAnimations()
                    syncShapeLayer()
                }
            )

            toolTipButton(
                text = i18nState.get("save"),
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    // 使用显示尺寸而不是原始像素尺寸，确保导出和显示一致
                    // 注意：Canvas 有 padding(8.dp)，所以实际绘制区域需要减去 padding
                    val displaySize = ImageSizeCalculator.getImageDisplayPixelSize(state, density.density)
                    val current = state.currentImage
                    if (displaySize == null || current == null) {
                        logger.warn("当前无法导出：缺少有效图像")
                        return@toolTipButton
                    }
                    // 计算减去 padding 后的实际绘制区域尺寸（Canvas 内部 drawScope.size）
                    val paddingPx = with(density) { (8.dp * 2).toPx() } // 左右各 8.dp，上下各 8.dp
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