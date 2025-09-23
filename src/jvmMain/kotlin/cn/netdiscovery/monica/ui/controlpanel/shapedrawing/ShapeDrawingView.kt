package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.animation.ShapeAnimationManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.coordinate.CoordinateConverter
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.handler.ShapeDrawingEventHandler
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state.ShapeDrawingState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.draggableTextField
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ShapeDrawingPropertiesMenuDialog
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer
import cn.netdiscovery.monica.ui.widget.color.ColorSelectionDialog
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.extensions.drawWithLayer
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator
import cn.netdiscovery.monica.i18n.getCurrentStringResource
import org.koin.compose.koinInject
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
    val viewModel: ShapeDrawingViewModel = koinInject()
    val density = LocalDensity.current
    val i18nState = getCurrentStringResource()

    // 状态管理
    val drawingState = remember { ShapeDrawingState() }
    
    // 动画管理
    val animationManager = remember { ShapeAnimationManager() }
    
    // 坐标转换器
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
    
    // 事件处理器
    val eventHandler = remember { ShapeDrawingEventHandler(drawingState, coordinateConverter) }
    
    // 对话框状态
    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showDraggableTextField by remember { mutableStateOf(false) }
    
    // 安全处理图像
    val image = state.currentImage?.toComposeImageBitmap() ?: run {
        logger.error("当前图像为空，无法进行绘制")
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background,
                        MaterialTheme.colors.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 使用统一的图片尺寸计算
        val (width, height) = ImageSizeCalculator.calculateImageSize(state)
        
        // 获取显示像素尺寸用于坐标验证
        val displaySize = ImageSizeCalculator.getImageDisplayPixelSize(state, density.density)
        val bitmapWidth = displaySize?.first ?: 0
        val bitmapHeight = displaySize?.second ?: 0
        
        // 如果无法获取有效尺寸，显示提示信息
        if (bitmapWidth <= 0 || bitmapHeight <= 0) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.Text(
                    text = "请先加载图片",
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
            return
        }

        Column(
            modifier = Modifier.align(Alignment.Center).width(width).height(height),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val drawModifier = Modifier
                .padding(8.dp)
                .shadow(1.dp)
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .dragMotionEvent(
                    onDragStart = { pointerInputChange ->
                        eventHandler.handleMouseDown(pointerInputChange.position)
                        pointerInputChange.consume()
                    },
                    onDrag = { pointerInputChange ->
                        val currentShapes = eventHandler.handleMouseMove(pointerInputChange.position)
                        // 更新当前绘制的形状（用于实时显示）
                        currentShapes.forEach { (key, shape) ->
                            // 临时添加到显示集合中用于实时显示
                            when (shape) {
                                is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Line -> drawingState.displayLines[key] = shape
                                is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Circle -> drawingState.displayCircles[key] = shape
                                is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Triangle -> drawingState.displayTriangles[key] = shape
                                is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Rectangle -> drawingState.displayRectangles[key] = shape
                                is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Polygon -> drawingState.displayPolygons[key] = shape
                                is cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Text -> drawingState.displayTexts[key] = shape
                            }
                        }
                        pointerInputChange.consume()
                    },
                    onDragEnd = { pointerInputChange ->
                        val result = eventHandler.handleMouseUp(pointerInputChange.position, bitmapWidth, bitmapHeight)
                        result?.let { (key, shape) ->
                            // 添加动画效果
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
                        pointerInputChange.consume()
                    }
                )

            Canvas(modifier = drawModifier) {
                val canvasDrawer = CanvasDrawer(TextDrawer, drawContext.canvas)

                // 绘制背景图片
                this.drawImage(
                    image = image,
                    dstSize = IntSize(width.toPx().toInt(), height.toPx().toInt())
                )

                drawWithLayer {
                    // 绘制所有已完成的形状
                    viewModel.drawShape(
                        canvasDrawer, 
                        drawingState.displayLines, 
                        drawingState.displayCircles, 
                        drawingState.displayTriangles, 
                        drawingState.displayRectangles, 
                        drawingState.displayPolygons, 
                        drawingState.displayTexts
                    )
                    
                    // 绘制动画效果
                    drawAllAnimations(
                        animationManager = animationManager,
                        displayLines = drawingState.displayLines,
                        displayCircles = drawingState.displayCircles,
                        displayTriangles = drawingState.displayTriangles,
                        displayRectangles = drawingState.displayRectangles,
                        displayPolygons = drawingState.displayPolygons
                    )
                }
            }
        }

        // 右侧菜单栏
        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {
            
            // 选择颜色
            toolTipButton(
                text = i18nState.get("select_color"),
                painter = painterResource("images/doodle/color.png"),
                onClick = { showColorDialog = true }
            )

            // 属性更改
            toolTipButton(
                text = i18nState.get("change_properties"),
                painter = painterResource("images/doodle/brush.png"),
                onClick = { showPropertiesDialog = true }
            )

            // 多个形状选择按钮
            ShapeSelectionButtons(drawingState)

            // 添加文字
            toolTipButton(
                text = i18nState.get("add_text"),
                painter = painterResource("images/shapedrawing/text.png"),
                onClick = { showDraggableTextField = true }
            )

            // 清除所有
            toolTipButton(
                text = i18nState.get("clear"),
                painter = painterResource("images/doodle/clear.png"),
                onClick = { 
                    drawingState.clearAllShapes()
                    animationManager.clearAllAnimations()
                }
            )

            // 保存
            toolTipButton(
                text = i18nState.get("save"),
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    viewModel.saveCanvasToBitmap(
                        density, 
                        drawingState.originalLines, 
                        drawingState.originalCircles, 
                        drawingState.originalTriangles, 
                        drawingState.originalRectangles, 
                        drawingState.originalPolygons, 
                        drawingState.originalTexts, 
                        image, 
                        state
                    )
                }
            )
        }

        // 颜色选择对话框
        if (showColorDialog) {
            ColorSelectionDialog(
                drawingState.currentShapeProperty.color,
                onDismiss = { showColorDialog = false },
                onNegativeClick = { showColorDialog = false },
                onPositiveClick = { color: Color ->
                    showColorDialog = false
                    // 只更新当前选择的颜色，不影响已绘制的形状
                    drawingState.updateColor(color)
                    logger.info("颜色已更改: ${color} (仅影响新绘制的形状)")
                }
            )
        }

        // 文字输入对话框
        if (showDraggableTextField) {
            TextInputDialog(
                modifier = Modifier.width(250.dp).height(130.dp),
                bitmapWidth = bitmapWidth,
                bitmapHeight = bitmapHeight,
                density = density,
                currentText = drawingState.currentText,
                currentShapeProperty = drawingState.currentShapeProperty,
                onTextChanged = { drawingState.updateTextState(it) },
                onDragged = { offset ->
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
                        
                        drawingState.updateTextState("") // 清空文本框
                    } else {
                        logger.warn("文本位置无效: ${textValidation.message}")
                    }
                    showDraggableTextField = false
                }
            )
        }

        // 属性设置对话框
        if (showPropertiesDialog) {
            ShapeDrawingPropertiesMenuDialog(drawingState.currentShapeProperty) { updatedProperties ->
                // 模式一：只更新当前选择的属性，不影响已绘制的形状
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
 * 绘制所有动画效果
 */
private fun DrawScope.drawAllAnimations(
    animationManager: ShapeAnimationManager,
    displayLines: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Line>,
    displayCircles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Circle>,
    displayTriangles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Triangle>,
    displayRectangles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Rectangle>,
    displayPolygons: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Polygon>
) {
    val currentTime = System.currentTimeMillis()
    
    animationManager.animatedShapes.forEach { (_, animatedShape) ->
        val elapsed = currentTime - animatedShape.startTime
        val progress = (elapsed.toFloat() / animatedShape.duration.toFloat()).coerceIn(0f, 1f)
        
        if (progress < 1f) {
            val easedProgress = animationManager.easeInOutCubic(progress)
            val scale = animationManager.lerp(animatedShape.startScale, animatedShape.endScale, easedProgress)
            val alpha = animationManager.lerp(animatedShape.startAlpha, animatedShape.endAlpha, easedProgress)
            
            // 简化的动画效果：绘制一个简单的圆形高亮
            val key = animatedShape.key
            val shapeType = animatedShape.shapeType
            val highlightColor = animatedShape.highlightColor
            
            // 解析位置信息
            val parts = key.split("_")
            if (parts.size >= 3) {
                val x = parts[1].toFloatOrNull() ?: 0f
                val y = parts[2].toFloatOrNull() ?: 0f
                val center = Offset(x, y)
                
                // 绘制脉冲效果
                val pulseAlpha = alpha * (0.5f + 0.5f * kotlin.math.sin((progress * kotlin.math.PI * 4).toDouble()).toFloat())
                
                // 绘制多层圆形高亮
                drawCircle(
                    color = highlightColor.copy(alpha = pulseAlpha * 0.3f),
                    radius = 30f * scale,
                    center = center
                )
                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha * 0.6f),
                    radius = 20f * scale,
                    center = center
                )
                drawCircle(
                    color = highlightColor.copy(alpha = pulseAlpha * 0.8f),
                    radius = 10f * scale,
                    center = center
                )
            }
        }
    }
}

/**
 * 文字输入对话框组件
 */
@Composable
private fun TextInputDialog(
    modifier: Modifier,
    bitmapWidth: Int,
    bitmapHeight: Int,
    density: androidx.compose.ui.unit.Density,
    currentText: String,
    currentShapeProperty: ShapeProperties,
    onTextChanged: (String) -> Unit,
    onDragged: (Offset) -> Unit
) {
    draggableTextField(
        modifier = modifier,
        bitmapWidth = bitmapWidth,
        bitmapHeight = bitmapHeight,
        density = density,
        text = currentText,
        onTextChanged = onTextChanged,
        onDragged = onDragged
    )
}
