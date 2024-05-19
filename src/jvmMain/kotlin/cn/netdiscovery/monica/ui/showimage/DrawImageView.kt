package cn.netdiscovery.monica.ui.showimage

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.image.ImageWithConstraints
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent
import cn.netdiscovery.monica.ui.widget.image.gesture.pointerMotionEvents
import cn.netdiscovery.monica.utils.extension.to2fStr

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.showimage.DrawImageView
 * @author: Tony Shen
 * @date:  2024/5/19 21:11
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun drawImage(
    state: ApplicationState,
    image: ImageBitmap
) {
    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    var angle by remember { mutableStateOf(0f) }  //旋转角度
    var scale by remember { mutableStateOf(1f) }  //缩放
    var offsetX by remember { mutableStateOf(0f) }//x偏移
    var offsetY by remember { mutableStateOf(0f) }//y偏移
    var matrix by remember { mutableStateOf(Matrix()) } //矩阵

    var showColorDialog by remember { mutableStateOf(false) }

    val properties by rememberUpdatedState(newValue = currentPathProperty)

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ImageWithConstraints(
            imageBitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = angle
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {
            val imageWidth = this.imageWidth
            val imageHeight = this.imageHeight

            val modifier = Modifier.size(imageWidth, imageHeight)

            val transition: InfiniteTransition = rememberInfiniteTransition()

            val phase by transition.animateFloat(
                initialValue = .9f,
                targetValue = .3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val color = Color.Green

            val paint = remember {
                Paint().apply {
                    style = PaintingStyle.Stroke
                    strokeWidth = 15f
                    strokeCap = StrokeCap.Round


                    this.asFrameworkPaint().apply {
                        val transparent = color
                            .copy(alpha = 0f)
                            .toArgb()

                        this.color = transparent
                    }
                }
            }

            // Path is what is used for drawing line on Canvas
            val path = remember(modifier) { Path() }

            val drawModifier = modifier
                .clipToBounds()
                .pointerMotionEvents(
                    onDown = { pointerInputChange: PointerInputChange ->
                        currentPosition = pointerInputChange.position
                        motionEvent = MotionEvent.Down
                        pointerInputChange.consume()
                    },
                    onMove = { pointerInputChange: PointerInputChange ->
                        currentPosition = pointerInputChange.position
                        motionEvent = MotionEvent.Move
                        pointerInputChange.consume()
                    },
                    onUp = { pointerInputChange: PointerInputChange ->
                        motionEvent = MotionEvent.Up
                        pointerInputChange.consume()
                    },
                    delayAfterDownInMillis = 25L
                )

            Canvas(modifier = drawModifier) {
                when (motionEvent) {
                    MotionEvent.Down -> {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        path.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )

                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)
                        paths.add(Pair(currentPath, currentPathProperty))
                        currentPath = Path()
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )
                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }

                    else -> Unit
                }

                with(drawContext.canvas.nativeCanvas) {

                    val checkPoint = saveLayer(null, null)

                    paths.forEach {

                        val path = it.first
                        val property = it.second

                        if (!property.eraseMode) {
                            drawPath(
                                color = property.color.copy((0.4f + phase).coerceAtMost(1f)),
                                path = path,
                                style = Stroke(
                                    width = property.strokeWidth,
                                    cap = property.strokeCap,
                                    join = property.strokeJoin
                                )
                            )
                        } else {

                            // Source
                            drawPath(
                                color = Color.Transparent,
                                path = path,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                ),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }

                    if (motionEvent != MotionEvent.Idle) {

                        if (!currentPathProperty.eraseMode) {
                            drawPath(
                                color = currentPathProperty.color.copy((0.4f + phase).coerceAtMost(1f)),
                                path = currentPath,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                )
                            )
                        } else {
                            drawPath(
                                color = Color.Transparent.copy((0.4f + phase).coerceAtMost(1f)),
                                path = currentPath,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                ),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }
                    restoreToCount(checkPoint)
                }
            }
        }

        Row(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp)) {

            Column(
                Modifier.padding(end = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedButton(
                    onClick = {
                        angle = 0f
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                        matrix = Matrix()

                        state.scale = 1f
                    },
                ) {
                    Text("恢复")
                }

                OutlinedButton(
                    onClick = {
                        showColorDialog = true
                    },
                ) {
                    Text("画笔")
                }
            }

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.scale.to2fStr(),
                    color = Color.Unspecified,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                verticalSlider(
                    value = state.scale,
                    onValueChange = {
                        state.scale = it
                        scale = it
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .background(Color(0xffdedede)),
                    valueRange = 0.1f..5f
                )
            }
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
    }
}