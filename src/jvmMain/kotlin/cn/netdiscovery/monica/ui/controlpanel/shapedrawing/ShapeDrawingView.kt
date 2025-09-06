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
 * 优化的动画形状数据类
 */
data class AnimatedShape(
    val key: String,
    val shapeType: String,
    val startTime: Long,
    val duration: Long = 800L, // 增加动画时长
    val startScale: Float = 0.3f, // 更明显的缩放效果
    val endScale: Float = 1.2f, // 稍微放大
    val startAlpha: Float = 0.1f, // 更淡的起始透明度
    val endAlpha: Float = 0.8f, // 更明显的结束透明度
    val highlightColor: Color = Color.Cyan, // 使用青色高亮
    val pulseEffect: Boolean = true // 添加脉冲效果
)

@Composable
fun shapeDrawing(state: ApplicationState) {
    val viewModel: ShapeDrawingViewModel = koinInject()

    val density = LocalDensity.current

    var shape by remember { mutableStateOf(ShapeEnum.NotAShape) }

    // 双坐标系统：displayShapes用于显示，originalShapes用于保存
    // 线段相关
    var currentLineStart by remember { mutableStateOf(Offset.Unspecified) }
    var currentLineEnd   by remember { mutableStateOf(Offset.Unspecified) }
    val displayLines = remember { mutableStateMapOf<Offset, Line>() }
    val originalLines = remember { mutableStateMapOf<Offset, Line>() }

    // 圆相关
    var currentCircleCenter by remember { mutableStateOf(Offset.Unspecified) }
    var currentCircleRadius by remember { mutableStateOf(0.0f) }
    val displayCircles = remember { mutableStateMapOf<Offset, Circle>() }
    val originalCircles = remember { mutableStateMapOf<Offset, Circle>() }

    // 三角相关
    var currentTriangleFirst  by remember { mutableStateOf(Offset.Unspecified) }
    var currentTriangleSecond by remember { mutableStateOf(Offset.Unspecified) }
    var currentTriangleThird  by remember { mutableStateOf(Offset.Unspecified) }
    val displayTriangles = remember { mutableStateMapOf<Offset, Triangle>() }
    val originalTriangles = remember { mutableStateMapOf<Offset, Triangle>() }

    // 矩形相关
    var currentRectFirst by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectTL    by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectBR    by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectTR    by remember { mutableStateOf(Offset.Unspecified) }
    var currentRectBL    by remember { mutableStateOf(Offset.Unspecified) }
    val displayRectangles = remember { mutableStateMapOf<Offset, Rectangle>() }
    val originalRectangles = remember { mutableStateMapOf<Offset, Rectangle>() }

    // 多边形相关
    var currentPolygonFirst by remember { mutableStateOf(Offset.Unspecified) }
    var currentPolygonPoints = remember { mutableSetOf<Offset>() }
    val displayPolygons = remember { mutableStateMapOf<Offset, Polygon>() }
    val originalPolygons = remember { mutableStateMapOf<Offset, Polygon>() }

    // 文字相关
    var text by remember { mutableStateOf("") }
    val displayTexts = remember { mutableStateMapOf<Offset, Text>() }
    val originalTexts = remember { mutableStateMapOf<Offset, Text>() }

    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    
    // 跟踪最后一个绘制的形状
    var lastDrawnShapeKey by remember { mutableStateOf<Offset?>(null) }
    var lastDrawnShapeType by remember { mutableStateOf<String?>(null) }
    
    // 优化的动画状态管理
    var animatedShapes by remember { mutableStateOf<Map<String, AnimatedShape>>(emptyMap()) }
    
    // 使用 Compose 动画系统优化性能
    val animationController = rememberInfiniteTransition(label = "shapeAnimation")
    val animatedProgress by animationController.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic), // 使用缓动函数
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
        displayLines.clear()
        originalLines.clear()
        displayCircles.clear()
        originalCircles.clear()
        displayTriangles.clear()
        originalTriangles.clear()
        displayRectangles.clear()
        originalRectangles.clear()
        displayPolygons.clear()
        originalPolygons.clear()
        displayTexts.clear()
        originalTexts.clear()
        
        // 重置最后一个形状的跟踪
        lastDrawnShapeKey = null
        lastDrawnShapeType = null
        
        logger.info("已清除所有已完成的形状")
    }
    
    /**
     * 坐标转换函数：显示坐标 -> 原始坐标
     */
    fun displayToOriginal(displayOffset: Offset, scaleX: Float, scaleY: Float): Offset {
        return Offset(displayOffset.x * scaleX, displayOffset.y * scaleY)
    }
    
    /**
     * 转换线段坐标
     */
    fun convertLineToOriginal(displayLine: Line, scaleX: Float, scaleY: Float): Line {
        val originalFrom = displayToOriginal(displayLine.from, scaleX, scaleY)
        val originalTo = displayToOriginal(displayLine.to, scaleX, scaleY)
        return Line(originalFrom, originalTo, displayLine.shapeProperties)
    }
    
    /**
     * 转换圆形坐标
     */
    fun convertCircleToOriginal(displayCircle: Circle, scaleX: Float, scaleY: Float): Circle {
        val originalCenter = displayToOriginal(displayCircle.center, scaleX, scaleY)
        val originalRadius = displayCircle.radius * ((scaleX + scaleY) / 2f) // 平均缩放半径
        return Circle(originalCenter, originalRadius, displayCircle.shapeProperties)
    }
    
    /**
     * 转换三角形坐标
     */
    fun convertTriangleToOriginal(displayTriangle: Triangle, scaleX: Float, scaleY: Float): Triangle {
        val originalFirst = displayToOriginal(displayTriangle.first, scaleX, scaleY)
        val originalSecond = displayTriangle.second?.let { displayToOriginal(it, scaleX, scaleY) }
        val originalThird = displayTriangle.third?.let { displayToOriginal(it, scaleX, scaleY) }
        return Triangle(originalFirst, originalSecond, originalThird, displayTriangle.shapeProperties)
    }
    
    /**
     * 转换矩形坐标
     */
    fun convertRectangleToOriginal(displayRect: Rectangle, scaleX: Float, scaleY: Float): Rectangle {
        val originalTl = displayToOriginal(displayRect.tl, scaleX, scaleY)
        val originalBl = displayToOriginal(displayRect.bl, scaleX, scaleY)
        val originalBr = displayToOriginal(displayRect.br, scaleX, scaleY)
        val originalTr = displayToOriginal(displayRect.tr, scaleX, scaleY)
        val originalFirst = displayToOriginal(displayRect.rectFirst, scaleX, scaleY)
        return Rectangle(originalTl, originalBl, originalBr, originalTr, originalFirst, displayRect.shapeProperties)
    }
    
    /**
     * 转换多边形坐标
     */
    fun convertPolygonToOriginal(displayPolygon: Polygon, scaleX: Float, scaleY: Float): Polygon {
        val originalPoints = displayPolygon.points.map { displayToOriginal(it, scaleX, scaleY) }
        return Polygon(originalPoints, displayPolygon.shapeProperties)
    }
    
    /**
     * 转换文字坐标
     */
    fun convertTextToOriginal(displayText: Text, scaleX: Float, scaleY: Float): Text {
        val originalPoint = displayToOriginal(displayText.point, scaleX, scaleY)
        return Text(originalPoint, displayText.message, displayText.shapeProperties)
    }

    /**
     * 优化的添加动画形状函数
     */
    fun addAnimatedShape(shapeType: String, key: Offset) {
        val shapeKey = "${shapeType}_${key.x}_${key.y}"
        val currentTime = System.currentTimeMillis()
        
        // 根据形状类型设置不同的动画参数
        val animationParams = when (shapeType) {
            "Circle" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 1000L, // 圆形动画稍长
                highlightColor = Color.Cyan
            )
            "Line" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 600L, // 线段动画较短
                highlightColor = Color.Magenta
            )
            "Triangle" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 800L,
                highlightColor = Color.Green
            )
            "Rectangle" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 700L,
                highlightColor = Color.Blue
            )
            else -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                highlightColor = Color.Yellow
            )
        }
        
        animatedShapes = animatedShapes + (shapeKey to animationParams)
        
        // 动画结束后自动移除
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
            delay(animationParams.duration + 200) // 稍微延长一点
            animatedShapes = animatedShapes - shapeKey
        }
        
        logger.info("添加动画形状: $shapeType")
    }
    
    /**
     * 改进的缓动插值函数
     */
    fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + (end - start) * fraction
    }
    
    /**
     * 缓动函数 - 缓入缓出
     */
    fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4f * t * t * t
        } else {
            val temp = -2f * t + 2f
            1f - (temp * temp * temp) / 2f
        }
    }
    
    /**
     * 脉冲效果函数
     */
    fun pulseEffect(progress: Float): Float {
        return (kotlin.math.sin((progress * kotlin.math.PI * 4).toDouble()).toFloat() + 1f) / 2f
    }
    
    /**
     * 优化的绘制动画高亮效果
     */
    fun DrawScope.drawAnimationHighlight(animatedShape: AnimatedShape, scale: Float, alpha: Float) {
        val key = animatedShape.key
        val shapeType = animatedShape.shapeType
        val highlightColor = animatedShape.highlightColor
        
        // 添加脉冲效果
        val pulseAlpha = if (animatedShape.pulseEffect) {
            alpha * (0.5f + 0.5f * pulseEffect(scale))
        } else {
            alpha
        }
        
        // 根据形状类型获取位置和绘制动画效果
        when (shapeType) {
            "Line" -> {
                val lineKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (displayLines.containsKey(lineKey)) {
                    val line = displayLines[lineKey]!!
                    // 绘制多层效果
                    drawLine(
                        color = highlightColor.copy(alpha = pulseAlpha * 0.8f),
                        start = line.from,
                        end = line.to,
                        strokeWidth = 12f * scale,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = pulseAlpha * 0.6f),
                        start = line.from,
                        end = line.to,
                        strokeWidth = 6f * scale,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
            "Circle" -> {
                val circleKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (displayCircles.containsKey(circleKey)) {
                    val circle = displayCircles[circleKey]!!
                    // 绘制外圈和内圈
                    drawCircle(
                        color = highlightColor.copy(alpha = pulseAlpha * 0.4f),
                        radius = circle.radius * scale * 1.2f,
                        center = circle.center,
                        style = Stroke(width = 4f * scale)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = pulseAlpha * 0.6f),
                        radius = circle.radius * scale,
                        center = circle.center,
                        style = Stroke(width = 2f * scale)
                    )
                }
            }
            "Triangle" -> {
                val triangleKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (displayTriangles.containsKey(triangleKey)) {
                    val triangle = displayTriangles[triangleKey]!!
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(triangle.first.x, triangle.first.y)
                        lineTo(triangle.second?.x ?: triangle.first.x, triangle.second?.y ?: triangle.first.y)
                        lineTo(triangle.third?.x ?: triangle.first.x, triangle.third?.y ?: triangle.first.y)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = highlightColor.copy(alpha = pulseAlpha * 0.5f),
                        style = Stroke(width = 8f * scale)
                    )
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = pulseAlpha * 0.7f),
                        style = Stroke(width = 3f * scale)
                    )
                }
            }
            "Rectangle" -> {
                val rectKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (displayRectangles.containsKey(rectKey)) {
                    val rect = displayRectangles[rectKey]!!
                    val rectSize = androidx.compose.ui.geometry.Size(
                        rect.br.x - rect.tl.x,
                        rect.br.y - rect.tl.y
                    )
                    drawRect(
                        color = highlightColor.copy(alpha = pulseAlpha * 0.4f),
                        topLeft = rect.tl,
                        size = rectSize,
                        style = Stroke(width = 6f * scale)
                    )
                    drawRect(
                        color = Color.White.copy(alpha = pulseAlpha * 0.6f),
                        topLeft = rect.tl,
                        size = rectSize,
                        style = Stroke(width = 2f * scale)
                    )
                }
            }
            "Polygon" -> {
                val polygonKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                if (displayPolygons.containsKey(polygonKey)) {
                    val polygon = displayPolygons[polygonKey]!!
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
                            color = highlightColor.copy(alpha = pulseAlpha * 0.5f),
                            style = Stroke(width = 8f * scale)
                        )
                        drawPath(
                            path = path,
                            color = Color.White.copy(alpha = pulseAlpha * 0.7f),
                            style = Stroke(width = 3f * scale)
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
        
        // 获取原始图片尺寸和显示尺寸，用于坐标转换
        val originalSize = ImageSizeCalculator.getImagePixelSize(state)
        val displaySize = ImageSizeCalculator.getImageDisplayPixelSize(state, density.density)
        
        // 预计算缩放比例，避免重复计算
        val scaleX = if (originalSize != null && displaySize != null) {
            originalSize.first.toFloat() / displaySize.first.toFloat()
        } else 1f
        val scaleY = if (originalSize != null && displaySize != null) {
            originalSize.second.toFloat() / displaySize.second.toFloat()
        } else 1f
        
        // 获取图片的显示像素尺寸用于坐标验证
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
                                displayLines[currentLineStart] = Line(currentLineStart, currentLineEnd, currentShapeProperty)
                            }

                            ShapeEnum.Circle -> {
                                currentCircleRadius = CoordinateSystem.calculateCircleRadius(currentCircleCenter, currentPosition)
                                displayCircles[currentCircleCenter] = Circle(currentCircleCenter, currentCircleRadius, currentShapeProperty)
                            }

                            ShapeEnum.Triangle -> {
                                determineCoordinatesOfTriangle()

                                if (currentTriangleFirst != Offset.Unspecified && currentTriangleSecond != Offset.Unspecified && currentTriangleThird != Offset.Unspecified) {
                                    displayTriangles[currentTriangleFirst] = Triangle(currentTriangleFirst, currentTriangleSecond, currentTriangleThird, currentShapeProperty)
                                }
                            }

                            ShapeEnum.Rectangle -> {
                                currentRectBR = currentPosition

                                determineCoordinatesOfRectangle()

                                displayRectangles[currentRectFirst] = Rectangle(currentRectTL, currentRectBL, currentRectBR, currentRectTR, currentRectFirst, currentShapeProperty)
                            }

                            ShapeEnum.Polygon -> {
                                currentPolygonPoints.add(currentPosition)

                                displayPolygons[currentPolygonFirst] = Polygon(currentPolygonPoints.toList(),  currentShapeProperty)
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
                                    // 保存到显示坐标系统
                                    val displayLine = Line(currentLineStart, currentLineEnd, currentShapeProperty)
                                    displayLines[currentLineStart] = displayLine
                                    
                                    // 转换并保存到原始坐标系统
                                    val originalLine = convertLineToOriginal(displayLine, scaleX, scaleY)
                                    originalLines[currentLineStart] = originalLine
                                    
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
                                    // 保存到显示坐标系统
                                    val displayCircle = Circle(currentCircleCenter, currentCircleRadius, currentShapeProperty)
                                    displayCircles[currentCircleCenter] = displayCircle
                                    
                                    // 转换并保存到原始坐标系统
                                    val originalCircle = convertCircleToOriginal(displayCircle, scaleX, scaleY)
                                    originalCircles[currentCircleCenter] = originalCircle
                                    
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
                                    // 保存到显示坐标系统
                                    val displayTriangle = Triangle(currentTriangleFirst, currentTriangleSecond, currentTriangleThird, currentShapeProperty)
                                    displayTriangles[currentTriangleFirst] = displayTriangle
                                    
                                    // 转换并保存到原始坐标系统
                                    val originalTriangle = convertTriangleToOriginal(displayTriangle, scaleX, scaleY)
                                    originalTriangles[currentTriangleFirst] = originalTriangle
                                    
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
                                    // 保存到显示坐标系统
                                    val displayRect = Rectangle(currentRectTL, currentRectBL, currentRectBR, currentRectTR, currentRectFirst, currentShapeProperty)
                                    displayRectangles[currentRectFirst] = displayRect
                                    
                                    // 转换并保存到原始坐标系统
                                    val originalRect = convertRectangleToOriginal(displayRect, scaleX, scaleY)
                                    originalRectangles[currentRectFirst] = originalRect
                                    
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
                                        // 保存到显示坐标系统
                                        val displayPolygon = Polygon(currentPolygonPoints.toList(), currentShapeProperty)
                                        displayPolygons[currentPolygonFirst] = displayPolygon
                                        
                                        // 转换并保存到原始坐标系统
                                        val originalPolygon = convertPolygonToOriginal(displayPolygon, scaleX, scaleY)
                                        originalPolygons[currentPolygonFirst] = originalPolygon
                                        
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
                    // 使用displayShapes进行显示
                    viewModel.drawShape(canvasDrawer, displayLines, displayCircles, displayTriangles, displayRectangles, displayPolygons, displayTexts)
                    
                    // 优化的动画效果绘制
                    val currentTime = System.currentTimeMillis()
                    animatedShapes.forEach { (key, animatedShape) ->
                        val elapsed = currentTime - animatedShape.startTime
                        val rawProgress = (elapsed.toFloat() / animatedShape.duration).coerceIn(0f, 1f)
                        
                        if (rawProgress < 1f) {
                            // 使用缓动函数改进动画效果
                            val easedProgress = easeInOutCubic(rawProgress)
                            
                            val scale = lerp(animatedShape.startScale, animatedShape.endScale, easedProgress)
                            val alpha = lerp(animatedShape.startAlpha, animatedShape.endAlpha, easedProgress)
                            
                            // 绘制优化的动画高亮效果
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
                painter = painterResource("images/doodle/clear.png"),
                onClick = {
                    clearAllShapes()
                })
                
            toolTipButton(text = "保存",
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    viewModel.saveCanvasToBitmap(density, originalLines, originalCircles, originalTriangles, originalRectangles, originalPolygons, originalTexts, image, state)
                })
        }

        if (showColorDialog) {
            ColorSelectionDialog(
                currentShapeProperty.color,
                onDismiss = { showColorDialog = false },
                onNegativeClick = { showColorDialog = false },
                onPositiveClick = { color: Color ->
                    showColorDialog = false
                    currentShapeProperty = currentShapeProperty.copy(color = color)
                    logger.info("颜色已更改: ${color}")
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
                        // 保存到显示坐标系统
                        val displayText = Text(currentPosition, text, currentShapeProperty)
                        displayTexts[currentPosition] = displayText
                        
                        // 转换并保存到原始坐标系统
                        val originalText = convertTextToOriginal(displayText, scaleX, scaleY)
                        originalTexts[currentPosition] = originalText
                        
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
                // 更新当前文字（使用displayTexts）
                displayTexts[currentPosition] = Text(currentPosition, text, updatedProperties)
                logger.info("属性已更新: fontSize=${updatedProperties.fontSize}, alpha=${updatedProperties.alpha}, fill=${updatedProperties.fill}, border=${updatedProperties.border}")
                showPropertiesDialog = false
            }
        }
    }
}