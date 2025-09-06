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
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.showimage.DoodleView
 * @author: Tony Shen
 * @date:  2024/5/19 21:11
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

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
    
    // 分离当前绘制状态，避免与已完成路径的相互影响
    val currentDrawingPath = remember { mutableStateOf<Pair<Path, PathProperties>?>(null) }
    
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

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }

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
                color = Color.Gray
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
                        
                        // 更新分离的绘制状态
                        currentDrawingPath.value = Pair(currentDisplayPath, currentPathProperty)
                        
                        previousPosition = currentPosition
                        pointerInputChange.consume()
                    },
                    onDrag = { pointerInputChange ->
                        val newPosition = pointerInputChange.position
                        
                        // 立即更新状态，确保实时响应
                        motionEvent = MotionEvent.Move
                        currentPosition = newPosition
                        
                        if (previousPosition != Offset.Unspecified) {
                            // 使用quadraticBezierTo绘制平滑曲线
                            val midX = (previousPosition.x + currentPosition.x) / 2
                            val midY = (previousPosition.y + currentPosition.y) / 2
                            
                            // 显示路径使用显示坐标（用于实时显示）
                            currentDisplayPath.quadraticBezierTo(previousPosition.x, previousPosition.y, midX, midY)
                            
                            // 原始路径使用原始坐标（用于保存）
                            val originalPosition = Offset(currentPosition.x * scaleX, currentPosition.y * scaleY)
                            val originalPreviousPosition = Offset(previousPosition.x * scaleX, previousPosition.y * scaleY)
                            val originalMidX = (originalPreviousPosition.x + originalPosition.x) / 2
                            val originalMidY = (originalPreviousPosition.y + originalPosition.y) / 2
                            currentOriginalPath.quadraticBezierTo(originalPreviousPosition.x, originalPreviousPosition.y, originalMidX, originalMidY)

                            previousPosition = currentPosition
                            
                            // 更新分离的绘制状态 - 创建新的Path对象确保实时更新
                            val newDisplayPath = Path().apply {
                                addPath(currentDisplayPath)
                            }
                            currentDrawingPath.value = Pair(newDisplayPath, currentPathProperty)
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
                        // 创建PathProperties的副本，避免引用共享
                        val pathPropertyCopy = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = Color(currentPathProperty.color.red, currentPathProperty.color.green, currentPathProperty.color.blue, currentPathProperty.color.alpha),
                            alpha = currentPathProperty.alpha,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin
                        )
                        displayPaths.add(Pair(currentDisplayPath, pathPropertyCopy))
                        originalPaths.add(Pair(currentOriginalPath, pathPropertyCopy))
                        
                        logger.info("路径已添加，当前路径数量: displayPaths=${displayPaths.size}, originalPaths=${originalPaths.size}")
                        logger.info("保存的路径颜色: ${pathPropertyCopy.color}")
                        
                        // 清空当前绘制状态
                        currentDrawingPath.value = null
                        
                        // 重置路径
                        currentDisplayPath = Path()
                        currentOriginalPath = Path()
                        // 保持当前的颜色设置，不重置currentPathProperty

                        // 限制撤销历史数量，防止内存溢出
                        if (pathsUndone.size >= maxUndoHistory) {
                            pathsUndone.removeAt(0)
                        }
                        // 注意：不要清空撤销历史，让用户可以撤销之前的操作

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
                // 使用key来确保路径变化时能正确重绘
                displayPaths.forEachIndexed { index, pathPair ->
                    val path = pathPair.first
                    val property = pathPair.second

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

                // 绘制当前正在绘制的路径（使用分离的状态）
                currentDrawingPath.value?.let { (currentPath, currentProps) ->
                    drawPath(
                        color = currentProps.color,
                        path = currentPath,
                        style = Stroke(
                            width = currentProps.strokeWidth,
                            cap = currentProps.strokeCap,
                            join = currentProps.strokeJoin
                        )
                    )
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {
            // 选择颜色
            toolTipButton(text = "选择颜色",
                painter = painterResource("images/doodle/color.png"),
                onClick = {
                    showColorDialog = true
                })

            // 属性更改
            toolTipButton(text = "属性更改",
                painter = painterResource("images/doodle/brush.png"),
                onClick = {
                    showPropertiesDialog = true
                })

            // 上一步
            toolTipButton(text = "上一步",
                painter = painterResource("images/doodle/previous_step.png"),
                onClick = {
                    logger.info("撤销前状态: displayPaths=${displayPaths.size}, originalPaths=${originalPaths.size}")
                    if (displayPaths.isNotEmpty() && originalPaths.isNotEmpty()) {
                        // 确保两个列表大小一致
                        if (displayPaths.size == originalPaths.size) {
                            val lastDisplayItem = displayPaths.removeLast()
                            val lastOriginalItem = originalPaths.removeLast()
                            pathsUndone.add(Pair(lastDisplayItem, lastOriginalItem))
                            
                            // 清空当前绘制状态
                            currentDrawingPath.value = null
                            
                            logger.info("撤销操作：移除了一个路径，当前路径数量: displayPaths=${displayPaths.size}, originalPaths=${originalPaths.size}")
                        } else {
                            logger.warn("路径列表大小不一致: displayPaths=${displayPaths.size}, originalPaths=${originalPaths.size}")
                        }
                    } else {
                        logger.info("没有可撤销的操作: displayPaths=${displayPaths.size}, originalPaths=${originalPaths.size}")
                    }
                })

            // 撤回
            toolTipButton(text = "撤回",
                painter = painterResource("images/doodle/revoke.png"),
                onClick = {
                    if (pathsUndone.isNotEmpty()) {
                        val lastUndoPaths = pathsUndone.removeLast()
                        val (displayPath, originalPath) = lastUndoPaths
                        displayPaths.add(displayPath)
                        originalPaths.add(originalPath)
                        
                        // 强制重绘：重置绘制状态
                        drawingState.value = Triple(MotionEvent.Idle, Offset.Unspecified, Path())
                        
                        logger.info("重做操作：恢复了一个路径")
                    } else {
                        logger.info("没有可重做的操作")
                    }
                })

            // 清空画布
            toolTipButton(text = "清空画布",
                painter = painterResource("images/doodle/clear.png"),
                onClick = {
                    // 清空所有路径
                    displayPaths.clear()
                    originalPaths.clear()
                    pathsUndone.clear()
                    
                    // 重置当前绘制状态
                    currentDisplayPath = Path()
                    currentOriginalPath = Path()
                    currentPosition = Offset.Unspecified
                    previousPosition = Offset.Unspecified
                    motionEvent = MotionEvent.Idle
                    
                    // 重置绘制状态，强制重绘
                    drawingState.value = Triple(MotionEvent.Idle, Offset.Unspecified, Path())
                    
                    logger.info("画布已清空，所有状态已重置")
                })

            // 保存
            toolTipButton(text = "保存",
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    viewModel.saveCanvasToBitmap(density, originalPaths, image, state)
                })
        }

        if (showColorDialog) {
            ColorSelectionDialog(
                currentPathProperty.color,
                onDismiss = { showColorDialog = false },
                onNegativeClick = { showColorDialog = false },
                onPositiveClick = { color: Color ->
                    showColorDialog = false
                    currentPathProperty = currentPathProperty.copy(color = color)
                    logger.info("颜色已更改: ${color}")
                    
                    // 更新当前绘制路径的颜色
                    currentDrawingPath.value?.let { (path, props) ->
                        currentDrawingPath.value = Pair(path, props.copy(color = color))
                    }
                }
            )
        }

        if (showPropertiesDialog) {
            PropertiesMenuDialog(
                pathOption = currentPathProperty, 
                onDismiss = {
                    showPropertiesDialog = false
                },
                onPropertiesChanged = { updatedProperty ->
                    currentPathProperty = updatedProperty
                    showPropertiesDialog = false
                    
                    // 更新当前绘制路径的属性
                    currentDrawingPath.value?.let { (path, props) ->
                        currentDrawingPath.value = Pair(path, updatedProperty)
                    }
                },
                title = "画笔设置"
            )
        }
    }
}