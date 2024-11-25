package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.*
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer
import cn.netdiscovery.monica.ui.widget.color.ColorSelectionDialog
import cn.netdiscovery.monica.ui.widget.confirmButton
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.extension.drawWithLayer
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

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
    var fontSize by remember { mutableStateOf(40f) }
    val texts = remember { mutableStateMapOf<Offset, Text>() }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var currentShapeProperty by remember { mutableStateOf(ShapeProperties()) }
    var showColorDialog by remember { mutableStateOf(false) }

    val properties by rememberUpdatedState(newValue = currentShapeProperty)

    val image = state.currentImage!!.toComposeImageBitmap()

    var showDraggableTextField by remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }

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
                                currentCircleRadius = calcCircleRadius(currentCircleCenter, currentPosition)
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
                                lines[currentLineStart] = Line(currentLineStart, currentLineEnd, currentShapeProperty)
                            }

                            ShapeEnum.Circle -> {
                                circles[currentCircleCenter] = Circle(currentCircleCenter, currentCircleRadius, currentShapeProperty)
                            }

                            ShapeEnum.Triangle -> {
                                if (currentTriangleFirst != Offset.Unspecified) {
                                    triangles[currentTriangleFirst] = Triangle(currentTriangleFirst, currentTriangleSecond, currentTriangleThird, currentShapeProperty)
                                }
                            }

                            ShapeEnum.Rectangle -> {
                                rectangles[currentRectFirst] = Rectangle(currentRectTL, currentRectBL, currentRectBR, currentRectTR, currentRectFirst, currentShapeProperty)
                            }

                            ShapeEnum.Polygon -> {
                                polygons[currentPolygonFirst] = Polygon(currentPolygonPoints.toList(), currentShapeProperty)
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

            toolTipButton(text = "线段",
                painter = painterResource("images/shapedrawing/line.png"),
                onClick = {
                    shape = ShapeEnum.Line

                    currentLineStart = Offset.Unspecified
                    currentLineEnd = Offset.Unspecified
                    currentShapeProperty = ShapeProperties()
                })

            toolTipButton(text = "圆形",
                painter = painterResource("images/shapedrawing/circle.png"),
                onClick = {
                    shape = ShapeEnum.Circle

                    currentCircleCenter = Offset.Unspecified
                    currentCircleRadius = 0.0f
                    currentShapeProperty = ShapeProperties()
                })

            toolTipButton(text = "三角形",
                painter = painterResource("images/shapedrawing/triangle.png"),
                onClick = {
                    shape = ShapeEnum.Triangle

                    currentTriangleFirst  = Offset.Unspecified
                    currentTriangleSecond = Offset.Unspecified
                    currentTriangleThird  = Offset.Unspecified
                    currentShapeProperty = ShapeProperties()
                })

            toolTipButton(text = "矩形",
                painter = painterResource("images/shapedrawing/rectangle.png"),
                onClick = {
                    shape = ShapeEnum.Rectangle

                    currentRectFirst = Offset.Unspecified
                    currentRectTL = Offset.Unspecified
                    currentRectBR = Offset.Unspecified
                    currentRectTR = Offset.Unspecified
                    currentRectBL = Offset.Unspecified
                    currentShapeProperty = ShapeProperties()
                })

            toolTipButton(text = "多边形",
                painter = painterResource("images/shapedrawing/polygon.png"),
                onClick = {
                    shape = ShapeEnum.Polygon

                    currentPolygonFirst = Offset.Unspecified
                    currentPolygonPoints.clear()
                    currentShapeProperty = ShapeProperties()
                })

            toolTipButton(text = "添加文字",
                painter = painterResource("images/shapedrawing/text.png"),
                onClick = {
                    showDraggableTextField = true

                    text = ""
                    fontSize = 40f
                    currentShapeProperty = ShapeProperties()
                })

            toolTipButton(text = "保存",
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    viewModel.saveCanvasToBitmap(density,lines,circles,triangles,rectangles,polygons, texts, image,state)
                })
        }

        if (showColorDialog) {
            ColorSelectionDialog(
                properties.color,
                onDismiss = { showColorDialog = !showColorDialog },
                onNegativeClick = { showColorDialog = !showColorDialog },
                onPositiveClick = { color: Color ->
                    showColorDialog = !showColorDialog
                    properties.color = color
                }
            )
        }

        if (showDraggableTextField) {
            DraggableTextField(modifier = Modifier.width(250.dp).height(130.dp), bitmapWidth = bitmapWidth, bitmapHeight = bitmapHeight, density = density, text = text,
                onTextChanged = {
                    text = it
                }, onDragged = { offset ->
                    val x = halfWidth.toFloat()  + offset.x - 110*density.density
                    val y = halfHeight.toFloat() + offset.y - 35*density.density
                    currentPosition = Offset(x,y)

                    logger.info("currentPosition = $currentPosition")

                    texts[currentPosition] = Text(currentPosition, text, currentShapeProperty, fontSize)

                    showDraggableTextField = false
                })
        }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableTextField(
    modifier: Modifier = Modifier,
    text: String,
    bitmapWidth: Int,
    bitmapHeight: Int,
    density: Density,
    onTextChanged: (String) -> Unit,
    onDragged: (Offset) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val halfWidth = bitmapWidth/2
    val halfHeight = bitmapHeight/2
    val halfTextFieldWidth = 125/density.density
    val halfTextFieldHeight = 65/density.density

    Box(
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change ->
                    offset += change
                    if (abs(offset.x) > halfWidth - halfTextFieldWidth || abs(offset.y) > halfHeight - halfTextFieldHeight) {
                        offset -= change
                        return@detectDragGestures
                    }
                }
            }
            .shadow(8.dp)
            .background(Color.White)
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
            .clip(RoundedCornerShape(8.dp))
    ) {

        Column {
            TextField (
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier.width(220.dp)
            )

            confirmButton(true, modifier = Modifier.align(Alignment.End).padding(top = 5.dp)) {
                onDragged.invoke(offset)
            }
        }
    }
}


private fun calcCircleRadius(center:Offset, position: Offset):Float {
    return sqrt((abs(position.x - center.x).pow(2) + abs(position.y - center.y).pow(2)).toDouble()).toFloat()
}