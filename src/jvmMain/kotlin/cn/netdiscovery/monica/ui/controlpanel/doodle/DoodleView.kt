package cn.netdiscovery.monica.ui.controlpanel.doodle

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.doodle.model.PathProperties
import cn.netdiscovery.monica.ui.widget.color.ColorSelectionDialog
import cn.netdiscovery.monica.ui.controlpanel.doodle.widget.PropertiesMenuDialog
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent
import cn.netdiscovery.monica.ui.widget.image.gesture.dragMotionEvent
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.extensions.drawWithLayer
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.showimage.DoodleView
 * @author: Tony Shen
 * @date:  2024/5/19 21:11
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun drawImage(
    state: ApplicationState
) {
    val viewModel: DoodleViewModel = koinInject()

    val density = LocalDensity.current

    // 双路径系统：displayPaths用于显示，originalPaths用于保存
    val displayPaths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    val originalPaths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    val pathsUndone = remember { mutableStateListOf<Pair<Pair<Path, PathProperties>, Pair<Path, PathProperties>>>() }
    
    // 撤销历史限制
    val maxUndoHistory = 50

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var currentDisplayPath by remember { mutableStateOf(Path()) }
    var currentOriginalPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }
    var eraserProperty by remember { mutableStateOf(PathProperties(strokeWidth = 20f, eraseMode = true)) }

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showEraserDialog by remember { mutableStateOf(false) }

    // 直接使用 currentPathProperty，不使用 rememberUpdatedState

    // 使用更直接的状态管理
    val drawingState = remember { mutableStateOf(Triple(MotionEvent.Idle, Offset.Unspecified, Path())) }

    // 安全获取图片，避免空指针异常
    val image = state.currentImage?.toComposeImageBitmap()
    
    // 如果图片为空，显示提示信息
    if (image == null) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "请先加载图片",
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
        return
    }

    Box(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        // 使用统一的图片尺寸计算
        val (width, height) = ImageSizeCalculator.calculateImageSize(state)
        
        // 获取原始图片尺寸和显示尺寸，用于保存时的坐标转换
        val originalSize = ImageSizeCalculator.getImagePixelSize(state)
        val displaySize = ImageSizeCalculator.getImageDisplayPixelSize(state, density.density)
        
        // 预计算缩放比例，避免重复计算
        val scaleX = if (originalSize != null && displaySize != null) {
            originalSize.first.toFloat() / displaySize.first.toFloat()
        } else 1f
        val scaleY = if (originalSize != null && displaySize != null) {
            originalSize.second.toFloat() / displaySize.second.toFloat()
        } else 1f

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
                        
                        // 显示路径使用显示坐标（用于实时显示）
                        currentDisplayPath.moveTo(currentPosition.x, currentPosition.y)
                        
                        // 原始路径使用原始坐标（用于保存）
                        val originalPosition = Offset(currentPosition.x * scaleX, currentPosition.y * scaleY)
                        currentOriginalPath.moveTo(originalPosition.x, originalPosition.y)
                        
                        // 更新绘制状态
                        drawingState.value = Triple(motionEvent, currentPosition, currentDisplayPath)
                        
                        previousPosition = currentPosition
                        pointerInputChange.consume()
                    },
                    onDrag = { pointerInputChange ->
                        val newPosition = pointerInputChange.position
                        
                        // 立即更新状态，确保实时响应
                        motionEvent = MotionEvent.Move
                        currentPosition = newPosition
                        
                        if (previousPosition != Offset.Unspecified) {
                            // 显示路径使用显示坐标（用于实时显示）
                            currentDisplayPath.lineTo(currentPosition.x, currentPosition.y)
                            
                            // 原始路径使用原始坐标（用于保存）
                            val originalPosition = Offset(currentPosition.x * scaleX, currentPosition.y * scaleY)
                            currentOriginalPath.lineTo(originalPosition.x, originalPosition.y)

                            previousPosition = currentPosition
                            
                            // 更新绘制状态，强制重绘
                            drawingState.value = Triple(motionEvent, currentPosition, currentDisplayPath)
                        }
                        pointerInputChange.consume()
                    },
                    onDragEnd = { pointerInputChange ->
                        motionEvent = MotionEvent.Up
                        
                        // 显示路径使用显示坐标（用于实时显示）
                        currentDisplayPath.lineTo(currentPosition.x, currentPosition.y)
                        
                        // 原始路径使用原始坐标（用于保存）
                        val originalPosition = Offset(currentPosition.x * scaleX, currentPosition.y * scaleY)
                        currentOriginalPath.lineTo(originalPosition.x, originalPosition.y)

                        // 同时保存显示路径和原始路径
                        displayPaths.add(Pair(currentDisplayPath, currentPathProperty))
                        originalPaths.add(Pair(currentOriginalPath, currentPathProperty))
                        
                        // 重置路径
                        currentDisplayPath = Path()
                        currentOriginalPath = Path()
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )

                        // 限制撤销历史数量，防止内存溢出
                        if (pathsUndone.size >= maxUndoHistory) {
                            pathsUndone.removeAt(0)
                        }
                        pathsUndone.clear()

                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                        pointerInputChange.consume()
                    }
                )

            Canvas(modifier = drawModifier) {

                this.drawImage(image = image,
                    dstSize = IntSize(width.toPx().toInt(), height.toPx().toInt()))

                // 绘制已完成的路径（使用显示路径）
                displayPaths.forEach {
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
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                // 绘制当前正在绘制的路径（使用显示路径）
                val (currentMotionEvent, currentPos, currentPath) = drawingState.value
                if (currentMotionEvent != MotionEvent.Idle && currentPos != Offset.Unspecified) {
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
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {
            toolTipButton(text = "选择颜色",
                painter = painterResource("images/doodle/color.png"),
                onClick = {
                    showColorDialog = true
                    currentPathProperty.eraseMode = false
                })

            toolTipButton(text = "属性更改",
                painter = painterResource("images/doodle/brush.png"),
                onClick = {
                    showPropertiesDialog = true
                    currentPathProperty.eraseMode = false
                })

            toolTipButton(text = "橡皮擦",
                painter = painterResource("images/doodle/eraser.png"),
                onClick = {
                    showEraserDialog = true
                })

            toolTipButton(text = "上一步",
                painter = painterResource("images/doodle/previous_step.png"),
                onClick = {
                    val lastDisplayItem = displayPaths.lastOrNull()
                    val lastOriginalItem = originalPaths.lastOrNull()
                    if (lastDisplayItem != null && lastOriginalItem != null) {
                        displayPaths.removeLast()
                        originalPaths.removeLast()
                        pathsUndone.add(Pair(lastDisplayItem, lastOriginalItem))
                    }
                })

            toolTipButton(text = "撤回",
                painter = painterResource("images/doodle/revoke.png"),
                onClick = {
                    val lastUndoPaths = pathsUndone.lastOrNull()
                    lastUndoPaths?.let { (displayPath, originalPath) ->
                        pathsUndone.removeLast()
                        displayPaths.add(displayPath)
                        originalPaths.add(originalPath)
                    }
                })

            toolTipButton(text = "清空画布",
                painter = painterResource("images/doodle/save.png"), // 使用现有图标
                onClick = {
                    displayPaths.clear()
                    originalPaths.clear()
                    pathsUndone.clear()
                })

            toolTipButton(text = "保存",
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    viewModel.saveCanvasToBitmap(density, originalPaths, image, state)
                })
        }

        if (showColorDialog) {
            ColorSelectionDialog(
                currentPathProperty.color,
                onDismiss = { showColorDialog = !showColorDialog },
                onNegativeClick = { showColorDialog = !showColorDialog },
                onPositiveClick = { color: Color ->
                    showColorDialog = !showColorDialog
                    currentPathProperty.color = color
                }
            )
        }

        if (showPropertiesDialog) {
            PropertiesMenuDialog(currentPathProperty) {
                showPropertiesDialog = !showPropertiesDialog
            }
        }

        if (showEraserDialog) {
            PropertiesMenuDialog(eraserProperty) {
                showEraserDialog = !showEraserDialog
                // 切换到橡皮擦模式
                currentPathProperty = PathProperties(
                    strokeWidth = eraserProperty.strokeWidth,
                    color = eraserProperty.color,
                    alpha = eraserProperty.alpha,
                    strokeCap = eraserProperty.strokeCap,
                    strokeJoin = eraserProperty.strokeJoin,
                    eraseMode = eraserProperty.eraseMode
                )
            }
        }
    }
}