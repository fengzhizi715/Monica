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

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.ShapeDrawingView
 * @author: Tony Shen
 * @date: 2024/11/21 14:09
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

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


    fun clear() {
        // 保存当前颜色设置
        val currentColor = currentShapeProperty.color
        
        shape = ShapeEnum.NotAShape
        currentShapeProperty = currentShapeProperty.copy(color = currentColor)

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

        text = ""
        
        // 重置最后一个形状的跟踪
        lastDrawnShapeKey = null
        lastDrawnShapeType = null
        
        logger.info("已清理形状数据，保留颜色: $currentColor")
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
        val bitmapWidth = image.width
        val bitmapHeight = image.height

        val halfWidth = bitmapWidth/2
        val halfHeight = bitmapHeight/2

        val width = (bitmapWidth/density.density).dp
        val height = (bitmapHeight/density.density).dp

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

                    clear()
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
                        textFieldHeight = 130f
                    )

                    logger.info("文本位置已计算: $currentPosition")

                    // 验证文本位置
                    val textValidation = CoordinateSystem.validateOffset(currentPosition, bitmapWidth, bitmapHeight)
                    if (textValidation.isValid) {
                        texts[currentPosition] = Text(currentPosition, text, currentShapeProperty)
                        lastDrawnShapeKey = currentPosition
                        lastDrawnShapeType = "Text"
                        logger.info("添加文字: '$text' 在位置 $currentPosition")
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


