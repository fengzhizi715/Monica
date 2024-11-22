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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Style
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Line
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Rectangle
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Triangle
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.pow
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
    val circles = remember { mutableStateMapOf<Offset, Float>() }

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

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    val image = state.currentImage!!.toComposeImageBitmap()

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

                            else -> {}
                        }

                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        when(shape) {
                            ShapeEnum.Line -> {
                                currentLineEnd = currentPosition
                                lines[currentLineStart] = Line(currentLineStart, currentLineEnd)
                            }

                            ShapeEnum.Circle -> {
                                currentCircleRadius = calcCircleRadius(currentCircleCenter, currentPosition)
                                circles[currentCircleCenter] = currentCircleRadius
                            }

                            ShapeEnum.Triangle -> {
                                determineCoordinatesOfTriangle()

                                if (currentTriangleFirst != Offset.Unspecified && currentTriangleSecond != Offset.Unspecified && currentTriangleThird != Offset.Unspecified) {
                                    triangles[currentTriangleFirst] = Triangle(currentTriangleFirst, currentTriangleSecond, currentTriangleThird)
                                }
                            }

                            ShapeEnum.Rectangle -> {
                                currentRectBR = currentPosition

                                determineCoordinatesOfRectangle()

                                rectangles[currentRectFirst] = Rectangle(currentRectTL, currentRectBL, currentRectBR, currentRectTR)
                            }

                            else -> {}
                        }

                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        when(shape) {
                            ShapeEnum.Line -> {
                                lines[currentLineStart] = Line(currentLineStart, currentLineEnd)
                            }

                            ShapeEnum.Circle -> {
                                circles[currentCircleCenter] = currentCircleRadius
                            }

                            ShapeEnum.Triangle -> {
                                if (currentTriangleFirst != Offset.Unspecified && currentTriangleSecond != Offset.Unspecified && currentTriangleThird != Offset.Unspecified) {
                                    triangles[currentTriangleFirst] = Triangle(currentTriangleFirst, currentTriangleSecond, currentTriangleThird)
                                }
                            }

                            ShapeEnum.Rectangle -> {
                                rectangles[currentRectFirst] = Rectangle(currentRectTL, currentRectBL, currentRectBR, currentRectTR)
                            }

                            else -> {}
                        }

                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }

                    else -> Unit
                }

                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    lines.forEach {

                        val line = it.value

                        if (line.from != Offset.Unspecified && line.to != Offset.Unspecified) {
                            canvasDrawer.line(line.from,line.to, Style(null, Color.Red, Border.Line, null, fill = true, scale = 1f, bounded = true))
                        }
                    }

                    circles.forEach {
                        val circleCenter = it.key
                        val circleRadius = it.value

                        canvasDrawer.point(circleCenter, Color.Red)
                        canvasDrawer.circle(circleCenter, circleRadius, Style(null, Color.Red, Border.No, null, fill = true, scale = 1f, bounded = true))
                    }

                    triangles.forEach {
                        val triangle = it.value

                        if (triangle.first != Offset.Unspecified && triangle.second != Offset.Unspecified && triangle.third != Offset.Unspecified) {
                            val list = mutableListOf<Offset>().apply {
                                add(triangle.first)
                                add(triangle.second)
                                add(triangle.third)
                            }

                            canvasDrawer.polygon(list, Style(null, Color.Red, Border.No, null, fill = true, scale = 1f, bounded = true))
                        }
                    }

                    rectangles.forEach {
                        val rect = it.value

                        if (rect.tl!=Offset.Unspecified && rect.bl!=Offset.Unspecified && rect.br!=Offset.Unspecified && rect.tr!=Offset.Unspecified) {
                            val list = mutableListOf<Offset>().apply {
                                add(rect.tl)
                                add(rect.bl)
                                add(rect.br)
                                add(rect.tr)
                            }

                            canvasDrawer.polygon(list, Style(null, Color.Red, Border.No, null, fill = true, scale = 1f, bounded = true))
                        }
                    }

                    restoreToCount(checkPoint)
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {

            toolTipButton(text = "线段",
                painter = painterResource("images/shapedrawing/line.png"),
                onClick = {
                    shape = ShapeEnum.Line

                    currentLineStart = Offset.Unspecified
                    currentLineEnd = Offset.Unspecified
                })

            toolTipButton(text = "圆形",
                painter = painterResource("images/shapedrawing/circle.png"),
                onClick = {
                    shape = ShapeEnum.Circle

                    currentCircleCenter = Offset.Unspecified
                    currentCircleRadius = 0.0f
                })

            toolTipButton(text = "三角",
                painter = painterResource("images/shapedrawing/triangle.png"),
                onClick = {
                    shape = ShapeEnum.Triangle

                    currentTriangleFirst  = Offset.Unspecified
                    currentTriangleSecond = Offset.Unspecified
                    currentTriangleThird  = Offset.Unspecified
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
                })

            toolTipButton(text = "保存",
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                })
        }
    }
}

private fun calcCircleRadius(center:Offset, position: Offset):Float {
    return sqrt((abs(position.x - center.x).pow(2) + abs(position.y - center.y).pow(2)).toDouble()).toFloat()
}