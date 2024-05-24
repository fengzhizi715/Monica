package cn.netdiscovery.monica.ui.controlpanel.doodle

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent

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
    val density = LocalDensity.current

    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }

    val properties by rememberUpdatedState(newValue = currentPathProperty)

    Box(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
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

                this.drawImage(image = image,
                    dstSize = IntSize(width.toPx().toInt(), height.toPx().toInt()))

                when (motionEvent) {

                    MotionEvent.Down -> {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        currentPath.quadraticBezierTo(
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
                                color = property.color,
                                path = path,
                                style = Stroke(
                                    width = property.strokeWidth,
                                    cap = property.strokeCap,
                                    join = property.strokeJoin
                                )
                            )
                        } else {
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
                                color = currentPathProperty.color,
                                path = currentPath,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                )
                            )
                        } else {
                            drawPath(
                                color = Color.Transparent,
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
                    modifier = Modifier.width(80.dp),
                    onClick = {
                        showColorDialog = true
                        currentPathProperty.eraseMode = false
                    },
                ) {
                    Text("颜色")
                }

                OutlinedButton(
                    modifier = Modifier.width(80.dp),
                    onClick = {
                        showPropertiesDialog = true
                        currentPathProperty.eraseMode = false
                    },
                ) {
                    Text("刷子")
                }

                OutlinedButton(
                    modifier = Modifier.width(80.dp),
                    onClick = {
                        currentPathProperty.eraseMode = true
                    },
                ) {
                    Text("橡皮擦")
                }

                OutlinedButton(
                    modifier = Modifier.width(80.dp),
                    onClick = {
                        val drawScope = CanvasDrawScope()
                        val size = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
                        val bitmap = state.currentImage!!.toComposeImageBitmap()
                        val canvas = Canvas(bitmap)

                        drawScope.draw(
                            density = density,
                            layoutDirection = LayoutDirection.Ltr,
                            canvas = canvas,
                            size = size,
                        ) {
                            state.togglePreviewWindow(false)

                            paths.forEach {

                                val path = it.first
                                val property = it.second

                                drawPath(
                                    color = property.color,
                                    path = path,
                                    style = Stroke(
                                        width = property.strokeWidth,
                                        cap = property.strokeCap,
                                        join = property.strokeJoin
                                    )
                                )
                            }
                        }

                        state.currentImage = bitmap.toAwtImage()
                    },
                ) {
                    Text("保存")
                }
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

        if (showPropertiesDialog) {
            PropertiesMenuDialog(properties) {
                showPropertiesDialog = !showPropertiesDialog
            }
        }
    }
}