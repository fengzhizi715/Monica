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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.*
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.draggableTextField
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.ShapeDrawingPropertiesMenuDialog
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer
import cn.netdiscovery.monica.ui.widget.color.ColorSelectionDialog
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.extensions.drawWithLayer
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.CoordinateSystem
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.ShapeDrawingView
 * @author: Tony Shen
 * @date: 2024/11/21 14:09
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

/**
 * 动画形状数据类
 */
data class AnimatedShape(
    val key: String,
    val shapeType: String,
    val startTime: Long,
    val duration: Long = 500L,
    val startScale: Float = 0.5f,
    val endScale: Float = 1.0f,
    val startAlpha: Float = 0.3f,
    val endAlpha: Float = 1.0f
)

@Composable
fun shapeDrawing(state: ApplicationState) {
    val viewModel: ShapeDrawingViewModel = koinInject()

    val density = LocalDensity.current

    var shape by remember { mutableStateOf(ShapeEnum.NotAShape) }

    // 线段相关
    var currentLineStart by remember { mutableStateOf(Offset.Unspecified) }
    var currentLineEnd   by remember { mutableStateOf(Offset.Unspecified) }
    val lines = remember { mutableStateMapOf<Offset, Line>() }

    // 圆相关
    var currentCircleCenter by remember { mutableStateOf(Offset.Unspecified) }
    var currentCircleRadius by remember { mutableStateOf(0.0f) }
    val circles = remember { mutableStateMapOf<Offset, Circle>() }

    // 三角相关
    var currentTriangleFirst  by remember { mutableStateOf(Offset.Unspecified) }
    var currentTriangleSecond by remember { mutableStateOf(Offset.Unspecified) }
    var currentTriangleThird  by remember { mutableStateOf(Offset.Unspecified) }
    val triangles = remember { mutableStateMapOf<Offset, Triangle>() }

    // 矩形相关
    var currentRectFirst by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectTL    by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectBR    by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectTR    by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectBL    by remember { mutableStateOf(Offset.Unspecified) }
    val rectangles = remember { mutableStateMapOf<Offset, Rectangle>() }

    // 多边形相关
    var currentPolygonFirst by remember { mutableStateOf(Offset.Unspecified) }
    var currentPolygonPoints = remember { mutableSetOf<Offset>() }
    val polygons = remember { mutableStateMapOf<Offset, Polygon>() }

    // 文字相关
    var text by remember { mutableStateOf("") }
    val texts = remember { mutableStateMapOf<Offset, Text>() }

    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    
    // 跟踪最后一个绘制的形状
    var lastDrawnShapeKey by remember { mutableStateOf<Offset?>(null) }
    var lastDrawnShapeType by remember { mutableStateOf<String?>(null) }
    
    // 动画状态
    var animatedShapes by remember { mutableStateOf<Map<String, AnimatedShape>>(emptyMap()) }
    var animationProgress by remember { mutableStateOf(0f) }
    
    // 动画控制器
    val animationController = rememberInfiniteTransition(label = "shapeAnimation")
    val animatedProgress by animationController.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shapeAnimationProgress"
    )


    var currentShapeProperty by remember { mutableStateOf(ShapeProperties()) }
    val properties by rememberUpdatedState(newValue = currentShapeProperty)

    // 安全处理图像，统一坐标系统
    val image = state.currentImage?.toComposeImageBitmap() ?: run {
        logger.error("当前图像为空，无法进行绘制")
        return
    }

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showDraggableTextField by remember { mutableStateOf(false) }


    /**
     * 清除当前绘制状态，但保留已完成的形状
     * 这个函数只清除临时状态，不会影响已经绘制完成的形状
     */
    fun clear() {
        // 保存当前颜色设置
        val currentColor = currentShapeProperty.color
        
        // 只清除绘制状态，不清除已完成的形状
        shape = ShapeEnum.NotAShape
        currentShapeProperty = currentShapeProperty.copy(color = currentColor)

        // 清除临时绘制状态
        currentLineStart = Offset.Unspecified
        currentLineEnd = Offset.Unspecified

        currentCircleCenter = Offset.Unspecified
        currentCircleRadius = 0.0f

        currentTriangleFirst  = Offset.Unspecified
        currentTriangleSecond = Offset.Unspecified
        currentTriangleThird  = Offset.Unspecified

        currentRectFirst = Offset.Unspecified
        currentRectTL = Offset.Unspecified
        currentRectBR = Offset.Unspecified
        currentRectTR = Offset.Unspecified
        currentRectBL = Offset.Unspecified

        currentPolygonFirst = Offset.Unspecified
        currentPolygonPoints.clear()

        // 清除临时文本输入
        text = ""
        
        // 重置最后一个形状的跟踪
        lastDrawnShapeKey = null
        lastDrawnShapeType = null
        
        logger.info("已清理临时绘制状态，保留所有已完成的形状，保留颜色: $currentColor")
    }
    
    /**
     * 清除所有已完成的形状（危险操作）
     * 这个函数会清除所有已经绘制完成的形状，包括文字
     */
    fun clearAllShapes() {
        lines.clear()
        circles.clear()
        triangles.clear()
        rectangles.clear()
        polygons.clear()
        texts.clear()
        
        // 重置最后一个形状的跟踪
        lastDrawnShapeKey = null
        lastDrawnShapeType = null
        
        logger.info("已清除所有已完成的形状")
    }

    /**
     * 更新最后一个绘制的形状的颜色
     */
    fun updateLastDrawnShapeColor(newColor: Color) {
        val key = lastDrawnShapeKey
        val type = lastDrawnShapeType
        
        if (key != null && type != null) {
            when (type) {
                "Line" -> {
                    if (lines.containsKey(key)) {
                        lines[key] = lines[key]!!.copy(shapeProperties = lines[key]!!.shapeProperties.copy(color = newColor))
                        logger.info("已更新最后一个线段颜色: $newColor")
                    }
                }
                "Circle" -> {
                    if (circles.containsKey(key)) {
                        circles[key] = circles[key]!!.copy(shapeProperties = circles[key]!!.shapeProperties.copy(color = newColor))
                        logger.info("已更新最后一个圆形颜色: $newColor")
                    }
                }
                "Triangle" -> {
                    if (triangles.containsKey(key)) {
                        triangles[key] = triangles[key]!!.copy(shapeProperties = triangles[key]!!.shapeProperties.copy(color = newColor))
                        logger.info("已更新最后一个三角形颜色: $newColor")
                    }
                }
                "Rectangle" -> {
                    if (rectangles.containsKey(key)) {
                        rectangles[key] = rectangles[key]!!.copy(shapeProperties = rectangles[key]!!.shapeProperties.copy(color = newColor))
                        logger.info("已更新最后一个矩形颜色: $newColor")
                    }
                }
                "Polygon" -> {
                    if (polygons.containsKey(key)) {
                        polygons[key] = polygons[key]!!.copy(shapeProperties = polygons[key]!!.shapeProperties.copy(color = newColor))
                        logger.info("已更新最后一个多边形颜色: $newColor")
                    }
                }
                "Text" -> {
                    if (texts.containsKey(key)) {
                        texts[key] = texts[key]!!.copy(shapeProperties = texts[key]!!.shapeProperties.copy(color = newColor))
                        logger.info("已更新最后一个文字颜色: $newColor")
                    }
                }
            }
        } else {
            logger.warn("没有找到最后一个绘制的形状")
        }
    }
    
    /**
     * 添加动画形状
     */
    fun addAnimatedShape(shapeType: String, key: Offset) {
        val shapeKey = "${shapeType}_${key.x}_${key.y}"
        val currentTime = System.currentTimeMillis()
        
        val animatedShape = AnimatedShape(
            key = shapeKey,
            shapeType = shapeType,
            startTime = currentTime
        )
        
        animatedShapes = animatedShapes + (shapeKey to animatedShape)
        
        // 5秒后移除动画
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
            delay(5000)
            animatedShapes = animatedShapes - shapeKey
        }
        
        logger.info("添加动画形状: $shapeType")
    }
    
    /**
     * 线性插值函数
     */
    fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + (end - start) * fraction
    }
    
    /**
     * 绘制动画高亮效果
     */
    fun DrawScope.drawAnimationHighlight(animatedShape: AnimatedShape, scale: Float, alpha: Float) {
        val key = animatedShape.key
        val shapeType = animatedShape.shapeType
        
        // 根据形状类型获取位置和绘制动画效果
        when (shapeType) {
            "Line" -> {
                val lineKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (lines.containsKey(lineKey)) {
                    val line = lines[lineKey]!!
                    drawLine(
                        color = Color.Yellow.copy(alpha = alpha * 0.5f),
                        start = line.from,
                        end = line.to,
                        strokeWidth = 8f * scale,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
            "Circle" -> {
                val circleKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (circles.containsKey(circleKey)) {
                    val circle = circles[circleKey]!!
                    drawCircle(
                        color = Color.Yellow.copy(alpha = alpha * 0.3f),
                        radius = circle.radius * scale,
                        center = circle.center
                    )
                }
            }
            "Triangle" -> {
                val triangleKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (triangles.containsKey(triangleKey)) {
                    val triangle = triangles[triangleKey]!!
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(triangle.first.x, triangle.first.y)
                            lineTo(triangle.second?.x ?: triangle.first.x, triangle.second?.y ?: triangle.first.y)
                            lineTo(triangle.third?.x ?: triangle.first.x, triangle.third?.y ?: triangle.first.y)
                            close()
                        },
                        color = Color.Yellow.copy(alpha = alpha * 0.3f),
                        style = Stroke(width = 6f * scale)
                    )
                }
            }
            "Rectangle" -> {
                val rectKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (rectangles.containsKey(rectKey)) {
                    val rect = rectangles[rectKey]!!
                    drawRect(
                        color = Color.Yellow.copy(alpha = alpha * 0.3f),
                        topLeft = rect.tl,
                        size = androidx.compose.ui.geometry.Size(
                            rect.br.x - rect.tl.x,
                            rect.br.y - rect.tl.y
                        )
                    )
                }
            }
            "Polygon" -> {
                val polygonKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (polygons.containsKey(polygonKey)) {
                    val polygon = polygons[polygonKey]!!
                    if (polygon.points.isNotEmpty()) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(polygon.points[0].x, polygon.points[0].y)
                            polygon.points.drop(1).forEach { point ->
                                lineTo(point.x, point.y)
                            }
                            close()
                        }
                        drawPath(
                            path = path,
                            color = Color.Yellow.copy(alpha = alpha * 0.3f),
                            style = Stroke(width = 6f * scale)
                        )
                    }
                }
            }
        }
    }

    /**
     * 确定三角形的坐标
     */
    fun determineCoordinatesOfTriangle() {
        if (previousPosition != currentPosition && currentTriangleFirst == Offset.Unspecified) {
            currentTriangleFirst = currentPosition
        } else if (currentTriangleFirst != Offset.Unspecified && currentTriangleSecond == Offset.Unspecified && currentTriangleFirst != currentPosition) {
            currentTriangleSecond = currentPosition
        } else if (currentTriangleFirst != Offset.Unspecified && currentTriangleSecond != currentPosition) {
            currentTriangleThird = currentPosition
        }
    }

    /**
     * 确定矩形的坐标
     */
    fun determineCoordinatesOfRectangle() {
        if (currentRectBR.x > currentRectFirst.x && currentRectBR.y > currentRectFirst.y) {

            if (currentRectTL != currentRectFirst)
                currentRectTL = currentRectFirst

            currentRectTR = Offset(currentRectBR.x, currentRectTL.y)
            currentRectBL = Offset(currentRectTL.x, currentRectBR.y)
        } else if (currentRectBR.x > currentRectFirst.x && currentRectBR.y < currentRectFirst.y) {

            if (currentRectTL != currentRectFirst)
                currentRectTL = currentRectFirst

            currentRectBL = currentRectTL
            currentRectTR = currentRectBR

            currentRectTL = Offset(currentRectBL.x, currentRectTR.y)
            currentRectBR = Offset(currentRectTR.x, currentRectBL.y)
        } else if (currentRectBR.x < currentRectFirst.x && currentRectBR.y > currentRectFirst.y) {

            if (currentRectTL != currentRectFirst)
                currentRectTL = currentRectFirst

            currentRectTR = currentRectTL
            currentRectBL = currentRectBR

            currentRectTL = Offset(currentRectBL.x, currentRectTR.y)
            currentRectBR = Offset(currentRectTR.x, currentRectBL.y)
        } else if (currentRectBR.x < currentRectFirst.x && currentRectBR.y < currentRectFirst.y) {

            if (currentRectTL != currentRectFirst)
                currentRectTL = currentRectFirst

            var temp = currentRectTL
            currentRectTL = currentRectBR
            currentRectBR = temp

            currentRectTR = Offset(currentRectBR.x, currentRectTL.y)
            currentRectBL = Offset(currentRectTL.x, currentRectBR.y)
        }
    }

    Box(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        // 使用统一的图片尺寸计算
        val (width, height) = ImageSizeCalculator.calculateImageSize(state)
        
        // 获取图片的显示像素尺寸用于坐标验证（而不是原始像素尺寸）
        val displayPixelSize = ImageSizeCalculator.getImageDisplayPixelSize(state)
        val bitmapWidth = displayPixelSize?.first ?: 0
        val bitmapHeight = displayPixelSize?.second ?: 0
        
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
                        motionEvent = MotionEvent.Down
                        currentPosition = pointerInputChange.position
                        pointerInputChange.consume()
                    },
                    onDrag = { pointerInputChange ->
                        motionEvent = MotionEvent.Move
                        currentPosition = pointerInputChange.position
                        pointerInputChange.consume()

                    },
                    onDragEnd = { pointerInputChange ->
                        motionEvent = MotionEvent.Up
                        pointerInputChange.consume()
                    }
                )

            Canvas(modifier = drawModifier) {

                val canvasDrawer = CanvasDrawer(TextDrawer, drawContext.canvas)

                this.drawImage(image = image,
                    dstSize = IntSize(width.toPx().toInt(), height.toPx().toInt()))

                when (motionEvent) {

                    MotionEvent.Down -> {
                        when(shape) {
                            ShapeEnum.Line -> {
                                if (previousPosition != currentPosition && currentLineStart == Offset.Unspecified) {
                                    currentLineStart = currentPosition
                                } else if (currentLineStart != Offset.Unspecified) {
                                    currentLineEnd = currentPosition
                                }
                            }

                            ShapeEnum.Circle -> {
                                if (previousPosition != currentPosition && currentCircleCenter == Offset.Unspecified) {
                                    currentCircleCenter = currentPosition
                                }
                            }

                            ShapeEnum.Triangle -> {
                                determineCoordinatesOfTriangle()
                            }

                            ShapeEnum.Rectangle -> {
                                if (previousPosition != currentPosition && currentRectTL == Offset.Unspecified) {
                                    currentRectTL = currentPosition
                                    currentRectFirst = currentPosition
                                } else if (currentRectTL != Offset.Unspecified) {
                                    currentRectBR = currentPosition

                                    determineCoordinatesOfRectangle()
                                }
                            }

                            ShapeEnum.Polygon -> {
                                if (previousPosition != currentPosition && currentPolygonFirst == Offset.Unspecified) {
                                    currentPolygonFirst = currentPosition
                                    currentPolygonPoints.add(currentPolygonFirst)
                                }  else if (currentPolygonFirst != Offset.Unspecified) {
                                    currentPolygonPoints.add(currentPosition)
                                }
                            }

                            else -> Unit
                        }

                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        when(shape) {
                            ShapeEnum.Line -> {
                                currentLineEnd = currentPosition
                                lines[currentLineStart] = Line(currentLineStart, currentLineEnd, currentShapeProperty)
                            }

                            ShapeEnum.Circle -> {
                                currentCircleRadius = CoordinateSystem.calculateCircleRadius(currentCircleCenter, currentPosition)
                                circles[currentCircleCenter] = Circle(currentCircleCenter, currentCircleRadius, currentShapeProperty)
                            }

                            ShapeEnum.Triangle -> {
                                determineCoordinatesOfTriangle()

                                if (currentTriangleFirst != Offset.Unspecified && currentTriangleSecond != Offset.Unspecified && currentTriangleThird != Offset.Unspecified) {
                                    triangles[currentTriangleFirst] = Triangle(currentTriangleFirst, currentTriangleSecond, currentTriangleThird, currentShapeProperty)
                                }
                            }

                            ShapeEnum.Rectangle -> {
                                currentRectBR = currentPosition

                                determineCoordinatesOfRectangle()

                                rectangles[currentRectFirst] = Rectangle(currentRectTL, currentRectBL, currentRectBR, currentRectTR, currentRectFirst, currentShapeProperty)
                            }

                            ShapeEnum.Polygon -> {
                                currentPolygonPoints.add(currentPosition)

                                polygons[currentPolygonFirst] = Polygon(currentPolygonPoints.toList(),  currentShapeProperty)
                            }

                            else -> Unit
                        }

                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        when(shape) {
                            ShapeEnum.Line -> {
                                // 验证线段坐标
                                val startValidation = CoordinateSystem.validateOffset(currentLineStart, bitmapWidth, bitmapHeight)
                                val endValidation = CoordinateSystem.validateOffset(currentLineEnd, bitmapWidth, bitmapHeight)
                                
                                if (startValidation.isValid && endValidation.isValid) {
                                    lines[currentLineStart] = Line(currentLineStart, currentLineEnd, currentShapeProperty)
                                    lastDrawnShapeKey = currentLineStart
                                    lastDrawnShapeType = "Line"
                                    addAnimatedShape("Line", currentLineStart)
                                    logger.info("添加线段: ${currentLineStart} -> ${currentLineEnd}")
                                } else {
                                    logger.warn("线段坐标无效: ${startValidation.message}, ${endValidation.message}")
                                }
                            }

                            ShapeEnum.Circle -> {
                                // 验证圆形坐标
                                val centerValidation = CoordinateSystem.validateOffset(currentCircleCenter, bitmapWidth, bitmapHeight)
                                
                                if (centerValidation.isValid && currentCircleRadius > 0) {
                                    circles[currentCircleCenter] = Circle(currentCircleCenter, currentCircleRadius, currentShapeProperty)
                                    lastDrawnShapeKey = currentCircleCenter
                                    lastDrawnShapeType = "Circle"
                                    addAnimatedShape("Circle", currentCircleCenter)
                                    logger.info("添加圆形: 中心=${currentCircleCenter}, 半径=${currentCircleRadius}")
                                } else {
                                    logger.warn("圆形坐标无效: ${centerValidation.message}")
                                }
                            }

                            ShapeEnum.Triangle -> {
                                // 验证三角形坐标
                                val firstValidation = CoordinateSystem.validateOffset(currentTriangleFirst, bitmapWidth, bitmapHeight)
                                val secondValidation = CoordinateSystem.validateOffset(currentTriangleSecond, bitmapWidth, bitmapHeight)
                                val thirdValidation = CoordinateSystem.validateOffset(currentTriangleThird, bitmapWidth, bitmapHeight)
                                
                                if (firstValidation.isValid && secondValidation.isValid && thirdValidation.isValid) {
                                    triangles[currentTriangleFirst] = Triangle(currentTriangleFirst, currentTriangleSecond, currentTriangleThird, currentShapeProperty)
                                    lastDrawnShapeKey = currentTriangleFirst
                                    lastDrawnShapeType = "Triangle"
                                    addAnimatedShape("Triangle", currentTriangleFirst)
                                    logger.info("添加三角形: ${currentTriangleFirst}, ${currentTriangleSecond}, ${currentTriangleThird}")
                                } else {
                                    logger.warn("三角形坐标无效: ${firstValidation.message}, ${secondValidation.message}, ${thirdValidation.message}")
                                }
                            }

                            ShapeEnum.Rectangle -> {
                                // 验证矩形坐标
                                val tlValidation = CoordinateSystem.validateOffset(currentRectTL, bitmapWidth, bitmapHeight)
                                val brValidation = CoordinateSystem.validateOffset(currentRectBR, bitmapWidth, bitmapHeight)
                                
                                if (tlValidation.isValid && brValidation.isValid) {
                                    rectangles[currentRectFirst] = Rectangle(currentRectTL, currentRectBL, currentRectBR, currentRectTR, currentRectFirst, currentShapeProperty)
                                    lastDrawnShapeKey = currentRectFirst
                                    lastDrawnShapeType = "Rectangle"
                                    addAnimatedShape("Rectangle", currentRectFirst)
                                    logger.info("添加矩形: ${currentRectTL} -> ${currentRectBR}")
                                } else {
                                    logger.warn("矩形坐标无效: ${tlValidation.message}, ${brValidation.message}")
                                }
                            }

                            ShapeEnum.Polygon -> {
                                // 验证多边形坐标
                                if (currentPolygonPoints.size >= 3) {
                                    val boundaryValidation = CoordinateSystem.validateShapeBoundary(currentPolygonPoints.toList(), bitmapWidth, bitmapHeight)
                                    
                                    if (boundaryValidation.isValid) {
                                        polygons[currentPolygonFirst] = Polygon(currentPolygonPoints.toList(), currentShapeProperty)
                                        lastDrawnShapeKey = currentPolygonFirst
                                        lastDrawnShapeType = "Polygon"
                                        addAnimatedShape("Polygon", currentPolygonFirst)
                                        logger.info("添加多边形: ${currentPolygonPoints.size}个顶点")
                                    } else {
                                        logger.warn("多边形边界无效: ${boundaryValidation.message}")
                                    }
                                } else {
                                    logger.warn("多边形顶点数量不足: ${currentPolygonPoints.size} < 3")
                                }
                            }

                            else -> Unit
                        }

                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }

                    else -> Unit
                }

                drawWithLayer {
                    viewModel.drawShape(canvasDrawer,lines,circles,triangles,rectangles,polygons, texts)
                    
                    // 绘制动画效果
                    val currentTime = System.currentTimeMillis()
                    animatedShapes.forEach { (key, animatedShape) ->
                        val elapsed = currentTime - animatedShape.startTime
                        val progress = (elapsed.toFloat() / animatedShape.duration).coerceIn(0f, 1f)
                        
                        if (progress < 1f) {
                            val scale = lerp(animatedShape.startScale, animatedShape.endScale, progress)
                            val alpha = lerp(animatedShape.startAlpha, animatedShape.endAlpha, progress)
                            
                            // 绘制动画高亮效果
                            drawAnimationHighlight(animatedShape, scale, alpha)
                        }
                    }
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {

            toolTipButton(text = "选择颜色",
                painter = painterResource("images/doodle/color.png"),
                onClick = {
                    showColorDialog = true
                })

            toolTipButton(text = "属性更改",
                painter = painterResource("images/doodle/brush.png"),
                onClick = {
                    showPropertiesDialog = true
                })

            toolTipButton(text = "线段",
                painter = painterResource("images/shapedrawing/line.png"),
                onClick = {
                    clear()

                    shape = ShapeEnum.Line
                })

            toolTipButton(text = "圆形",
                painter = painterResource("images/shapedrawing/circle.png"),
                onClick = {
                    clear()

                    shape = ShapeEnum.Circle
                })

            toolTipButton(text = "三角形",
                painter = painterResource("images/shapedrawing/triangle.png"),
                onClick = {
                    clear()

                    shape = ShapeEnum.Triangle
                })

            toolTipButton(text = "矩形",
                painter = painterResource("images/shapedrawing/rectangle.png"),
                onClick = {
                    clear()

                    shape = ShapeEnum.Rectangle
                })

            toolTipButton(text = "多边形",
                painter = painterResource("images/shapedrawing/polygon.png"),
                onClick = {
                    clear()

                    shape = ShapeEnum.Polygon
                })

            toolTipButton(text = "添加文字",
                painter = painterResource("images/shapedrawing/text.png"),
                onClick = {
                    showDraggableTextField = true
                    // 移除clear()调用，保留之前添加的文字
                })

            toolTipButton(text = "清除所有",
                painter = painterResource("images/doodle/brush.png"),
                onClick = {
                    clearAllShapes()
                })
                
            toolTipButton(text = "保存",
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    viewModel.saveCanvasToBitmap(density,lines,circles,triangles,rectangles,polygons, texts, image,state)
                })
        }

        if (showColorDialog) {
            ColorSelectionDialog(
                currentShapeProperty.color,
                onDismiss = { showColorDialog = !showColorDialog },
                onNegativeClick = { showColorDialog = !showColorDialog },
                onPositiveClick = { color: Color ->
                    showColorDialog = !showColorDialog
                    currentShapeProperty = currentShapeProperty.copy(color = color)
                    // 只更新最后一个绘制的形状的颜色
                    updateLastDrawnShapeColor(color)
                    logger.info("颜色已更新: $color")
                }
            )
        }

        if (showDraggableTextField) {
            draggableTextField(modifier = Modifier.width(250.dp).height(130.dp),
                bitmapWidth = bitmapWidth,
                bitmapHeight = bitmapHeight,
                density = density,
                text = text,
                onTextChanged = {
                    text = it
                }, onDragged = { offset ->
                    // 使用统一的坐标系统计算文本位置
                    currentPosition = CoordinateSystem.calculateTextPosition(
                        dragOffset = offset,
                        imageWidth = bitmapWidth,
                        imageHeight = bitmapHeight,
                        density = density,
                        textFieldWidth = 250f,
                        textFieldHeight = 130f,
                        fontSize = currentShapeProperty.fontSize
                    )

                    logger.info("文本位置已计算: $currentPosition")

                    // 验证文本位置（现在currentPosition是Canvas坐标）
                    val textValidation = CoordinateSystem.validateOffset(currentPosition, bitmapWidth, bitmapHeight)
                    if (textValidation.isValid) {
                        texts[currentPosition] = Text(currentPosition, text, currentShapeProperty)
                        lastDrawnShapeKey = currentPosition
                        lastDrawnShapeType = "Text"
                        logger.info("添加文字: '$text' 在Canvas位置 $currentPosition")
                        
                        // 清空文本框内容，为下次输入做准备
                        text = ""
                    } else {
                        logger.warn("文本位置无效: ${textValidation.message}")
                    }
                    showDraggableTextField = false
                })
        }

        if (showPropertiesDialog) {

            ShapeDrawingPropertiesMenuDialog(currentShapeProperty) { updatedProperties ->
                // 更新全局属性
                currentShapeProperty = updatedProperties
                // 更新当前文字
                texts[currentPosition] = Text(currentPosition, text, updatedProperties)
                logger.info("属性已更新: fontSize=${updatedProperties.fontSize}, alpha=${updatedProperties.alpha}, fill=${updatedProperties.fill}, border=${updatedProperties.border}")
                showPropertiesDialog = false
            }
        }
    }
}


