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
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
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

    var currentCircleCenter by remember { mutableStateOf(Offset.Unspecified) }
    var currentCircleRadius by remember { mutableStateOf(0.0f) }

    val circles = remember { mutableStateMapOf<Offset, Float>() }

    var shape by remember { mutableStateOf(ShapeEnum.NotAShape) }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    val image = state.currentImage!!.toComposeImageBitmap()

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
                            ShapeEnum.Circle -> {
                                if (previousPosition != currentPosition && currentCircleCenter == Offset.Unspecified) {
                                    currentCircleCenter = currentPosition
                                }
                            }
                            else -> {}
                        }

                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        when(shape) {
                            ShapeEnum.Circle -> {
                                if (previousPosition != Offset.Unspecified) {
                                    currentCircleRadius = calcCircleRadius(currentCircleCenter, currentPosition)
                                    circles[currentCircleCenter] = currentCircleRadius

                                }
                            }

                            else -> {}
                        }

                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        when(shape) {
                            ShapeEnum.Circle -> {
                                circles[currentCircleCenter] = currentCircleRadius
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

                    circles.forEach {

                        val circleCenter = it.key
                        val circleRadius = it.value

                        canvasDrawer.point(circleCenter, Color.Red)
                        canvasDrawer.circle(circleCenter, circleRadius, Style(null, Color.Red, Border.No, null, fill = true, scale = 1f, bounded = true))
                    }

                    restoreToCount(checkPoint)
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {

            toolTipButton(text = "圆形",
                painter = painterResource("images/shapedrawing/circle.png"),
                onClick = {
                    shape = ShapeEnum.Circle
                    currentCircleCenter = Offset.Unspecified
                    currentCircleRadius = 0.0f
                })

            toolTipButton(text = "矩形",
                painter = painterResource("images/shapedrawing/rectangle.png"),
                onClick = {
                    shape = ShapeEnum.Rectangle
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