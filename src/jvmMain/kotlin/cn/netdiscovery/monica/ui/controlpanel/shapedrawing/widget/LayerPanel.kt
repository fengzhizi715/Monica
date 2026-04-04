package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.AdjustmentLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayerMaskShape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.Layer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerBlendMode
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerAdjustment
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ShapeLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerType
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.properties.ExposedSelectionMenu
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getBufferedImage
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.collections.asReversed

@Composable
fun LayerPanel(
    editorController: EditorController,
    state: ApplicationState,
    modifier: Modifier = Modifier.Companion
) {
    val density = LocalDensity.current
    val layers by editorController.layerManager.layers.collectAsState()
    val activeLayer by editorController.layerManager.activeLayer.collectAsState()
    val selectedLayerIds by editorController.selectedLayerIds.collectAsState()
    val layerGroups by editorController.layerGroups.collectAsState()

    var deleteConfirmLayerId by remember { mutableStateOf<UUID?>(null) }
    var propertyEditorLayerId by remember { mutableStateOf<UUID?>(null) }
    var propertyName by remember { mutableStateOf("") }
    var propertyVisible by remember { mutableStateOf(true) }
    var propertyLocked by remember { mutableStateOf(false) }
    var propertyOpacity by remember { mutableStateOf(1f) }
    var propertyBlendMode by remember { mutableStateOf(LayerBlendMode.NORMAL) }
    var propertyAdjustment by remember { mutableStateOf(LayerAdjustment()) }
    var groupEditorId by remember { mutableStateOf<UUID?>(null) }
    var groupName by remember { mutableStateOf("") }
    var draggedLayerId by remember { mutableStateOf<UUID?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    val displayLayers = remember(layers) { layers.asReversed() }
    val logger = remember { LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass) }

    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "图层",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.Companion.padding(bottom = 4.dp)
        )

        val shapeLayerCount = layers.count { it.type == LayerType.SHAPE }
        val canAddShapeLayer = editorController.canAddShapeLayer()

        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val result = editorController.addShapeLayer("形状图层")
                    if (result == null && !canAddShapeLayer) {
                        state.showTray("最多只能创建 1 个形状层", "提示")
                    }
                },
                modifier = Modifier.Companion.weight(1f),
                enabled = canAddShapeLayer,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(14.dp)
                )
                Spacer(modifier = Modifier.Companion.width(4.dp))
                Text(
                    if (canAddShapeLayer) "形状层" else "已达上限",
                    style = MaterialTheme.typography.caption
                )
            }

            OutlinedButton(
                onClick = {
                    chooseImage(state) { file ->
                        try {
                            var bufferedImage = getBufferedImage(file, state)
                            
                            // 如果添加的图像超过背景图，自动缩放
                            val bgSize = editorController.getBackgroundSize()
                            if (bgSize != null) {
                                val bgWidth = bgSize.first.toInt()
                                val bgHeight = bgSize.second.toInt()
                                
                                // 如果图像超过背景层大小，缩放到不超过背景层
                                if (bufferedImage.width > bgWidth || bufferedImage.height > bgHeight) {
                                    val scaleX = bgWidth.toFloat() / bufferedImage.width
                                    val scaleY = bgHeight.toFloat() / bufferedImage.height
                                    val scale = minOf(scaleX, scaleY)
                                    
                                    val newWidth = (bufferedImage.width * scale).toInt()
                                    val newHeight = (bufferedImage.height * scale).toInt()
                                    
                                    val scaledImage = java.awt.Image.SCALE_SMOOTH
                                    val resizedBufImage = bufferedImage.getScaledInstance(newWidth, newHeight, scaledImage)
                                    bufferedImage = java.awt.image.BufferedImage(newWidth, newHeight, java.awt.image.BufferedImage.TYPE_INT_RGB)
                                    val g2d = bufferedImage.createGraphics()
                                    g2d.drawImage(resizedBufImage, 0, 0, null)
                                    g2d.dispose()
                                    
                                    logger.info("自动缩放图像: ${bufferedImage.width}x${bufferedImage.height} (原始: ${getBufferedImage(file, state).width}x${getBufferedImage(file, state).height})")
                                }
                            }
                            
                            val imageBitmap = bufferedImage.toComposeImageBitmap()
                            val imageLayerCount = layers.count { it.type == LayerType.IMAGE } + 1
                            val layerName = "图像图层 $imageLayerCount"
                            editorController.createImageLayer(layerName, imageBitmap)
                            logger.info("成功添加图像层: $layerName, 文件: ${file.name}")
                        } catch (e: Exception) {
                            logger.error("添加图像层失败: ${file.name}", e)
                            state.showTray("添加图像层失败: ${e.message}", "错误")
                        }
                    }
                },
                modifier = Modifier.Companion.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(14.dp)
                )
                Spacer(modifier = Modifier.Companion.width(4.dp))
                Text("图像层", style = MaterialTheme.typography.caption)
            }
        }

        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { editorController.createAdjustmentLayer("调整层") },
                modifier = Modifier.Companion.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.Companion.size(14.dp))
                Spacer(modifier = Modifier.Companion.width(4.dp))
                Text("调整层", style = MaterialTheme.typography.caption)
            }

            OutlinedButton(
                onClick = {
                    val group = editorController.createGroupFromSelection()
                    if (group == null) {
                        state.showTray("至少选择 2 个非背景图层才能创建分组", "提示")
                    }
                },
                modifier = Modifier.Companion.weight(1f),
                enabled = selectedLayerIds.size >= 2,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.Companion.size(14.dp))
                Spacer(modifier = Modifier.Companion.width(4.dp))
                Text("创建分组", style = MaterialTheme.typography.caption)
            }
        }

        if (selectedLayerIds.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colors.primary.copy(alpha = 0.06f),
                border = BorderStroke(1.dp, MaterialTheme.colors.primary.copy(alpha = 0.15f)),
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.Companion.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "已选择 ${selectedLayerIds.size} 个图层",
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { editorController.selectAllEditableLayers() },
                            modifier = Modifier.Companion.weight(1f)
                        ) {
                            Text("全选", style = MaterialTheme.typography.caption)
                        }
                        OutlinedButton(
                            onClick = { editorController.clearLayerSelection() },
                            modifier = Modifier.Companion.weight(1f)
                        ) {
                            Text("清空", style = MaterialTheme.typography.caption)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { editorController.setSelectedLayersVisibility(false) },
                            modifier = Modifier.Companion.weight(1f)
                        ) {
                            Text("批量隐藏", style = MaterialTheme.typography.caption)
                        }
                        OutlinedButton(
                            onClick = { editorController.setSelectedLayersLocked(true) },
                            modifier = Modifier.Companion.weight(1f)
                        ) {
                            Text("批量锁定", style = MaterialTheme.typography.caption)
                        }
                    }
                    OutlinedButton(
                        onClick = { editorController.deleteSelectedLayers() },
                        modifier = Modifier.Companion.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.error
                        )
                    ) {
                        Text("批量删除", style = MaterialTheme.typography.caption)
                    }
                }
            }
        } else {
            OutlinedButton(
                onClick = { editorController.selectAllEditableLayers() },
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Text("多选图层", style = MaterialTheme.typography.caption)
            }
        }

        Spacer(modifier = Modifier.Companion.height(4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            displayLayers.forEachIndexed { displayIndex, layer ->
                val actualIndex = layers.indexOfFirst { it.id == layer.id }
                val isBackgroundLayer = editorController.isBackgroundLayer(layer)
                val isActive = activeLayer?.id == layer.id
                val isSelected = layer.id in selectedLayerIds
                val group = layerGroups.firstOrNull { it.id == layer.groupId }
                val isDragging = draggedLayerId == layer.id
                val upEnabled = !isBackgroundLayer && actualIndex < layers.lastIndex
                val downEnabled = !isBackgroundLayer && actualIndex > 0
                val cardColor = when {
                    isDragging -> MaterialTheme.colors.primary.copy(alpha = 0.15f)
                    isActive -> MaterialTheme.colors.primary.copy(alpha = 0.08f)
                    else -> MaterialTheme.colors.surface
                }
                val borderColor = when {
                    isDragging -> MaterialTheme.colors.primary.copy(alpha = 0.6f)
                    isActive -> MaterialTheme.colors.primary.copy(alpha = 0.4f)
                    else -> MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = cardColor,
                    border = BorderStroke(
                        width = if (isDragging || isActive) 2.dp else 1.dp,
                        color = borderColor
                    ),
                    elevation = if (isDragging) 8.dp else 0.dp,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .zIndex(if (isDragging) 1f else 0f)
                        .pointerInput(layer.id, layers.size, density.density) {
                            if (isBackgroundLayer) return@pointerInput
                            val itemHeightPx = with(density) { 80.dp.toPx() }
                            val threshold = itemHeightPx * 0.5f
                            
                            detectDragGestures(
                                onDragStart = {
                                    draggedLayerId = layer.id
                                    dragOffset = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    dragOffset += dragAmount.y
                                    
                                    // 重新计算当前索引（因为 layers 可能已更新）
                                    val currentLayers = editorController.layerManager.layers.value
                                    val currentIndex = currentLayers.indexOfFirst { it.id == layer.id }
                                    
                                    // 注意：displayLayers 是反转的，所以向下拖拽（dragOffset > 0）应该向上移动（index 增加）
                                    if (dragOffset > threshold && currentIndex < currentLayers.lastIndex) {
                                        // 向下拖拽，在列表中向上移动（index 增加）
                                        val targetIndex = currentIndex + 1
                                        editorController.moveLayerTo(layer.id, targetIndex)
                                        dragOffset = 0f
                                    } else if (dragOffset < -threshold && currentIndex > 0) {
                                        // 向上拖拽，在列表中向下移动（index 减少）
                                        val targetIndex = currentIndex - 1
                                        editorController.moveLayerTo(layer.id, targetIndex)
                                        dragOffset = 0f
                                    }
                                },
                                onDragEnd = {
                                    draggedLayerId = null
                                    dragOffset = 0f
                                }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier.Companion
                            .clickable { editorController.setActiveLayer(layer.id) }
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (!isBackgroundLayer) {
                                        editorController.toggleLayerSelection(layer.id)
                                    }
                                },
                                enabled = !isBackgroundLayer,
                                modifier = Modifier.Companion.size(20.dp)
                            )

                            // 可见性复选框
                            Checkbox(
                                checked = layer.visible,
                                onCheckedChange = { checked ->
                                    editorController.setLayerVisibility(layer.id, checked)
                                },
                                modifier = Modifier.Companion.size(20.dp)
                            )

                            LayerThumbnail(
                                layer = layer,
                                isActive = isActive,
                                modifier = Modifier.Companion.size(40.dp)
                            )

                            // 图层信息
                            Column(
                                modifier = Modifier.Companion.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = layer.name,
                                    style = MaterialTheme.typography.body2.copy(
                                        color = if (isActive) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            MaterialTheme.colors.onSurface
                                        },
                                        fontWeight = if (isActive) FontWeight.Companion.SemiBold else FontWeight.Companion.Normal
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Companion.Ellipsis
                                )
                                if (group != null) {
                                    Text(
                                        text = "分组: ${group.name}",
                                        style = MaterialTheme.typography.caption.copy(
                                            color = MaterialTheme.colors.primary.copy(alpha = 0.8f)
                                        ),
                                        modifier = Modifier.Companion.clickable {
                                            groupEditorId = group.id
                                            groupName = group.name
                                        }
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.Companion.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = layer.type.toDisplayName(),
                                        style = MaterialTheme.typography.caption.copy(
                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        )
                                    )
                                    if (layer.type == LayerType.SHAPE && isActive) {
                                        Text(
                                            text = "• 当前绘制",
                                            style = MaterialTheme.typography.caption.copy(
                                                color = MaterialTheme.colors.primary,
                                                fontWeight = FontWeight.Companion.Bold
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = "透明度 ${layer.opacity.toOpacityPercent()}",
                                    style = MaterialTheme.typography.caption.copy(
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.65f)
                                    ),
                                    modifier = Modifier.Companion.clickable {
                                        propertyEditorLayerId = layer.id
                                        propertyName = layer.name
                                        propertyVisible = layer.visible
                                        propertyLocked = layer.locked
                                        propertyOpacity = layer.opacity
                                        propertyBlendMode = layer.blendMode
                                        propertyAdjustment = (layer as? AdjustmentLayer)?.adjustment ?: LayerAdjustment()
                                    }
                                )
                            }
                        }

                        // 操作按钮区域
                        Row(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    propertyEditorLayerId = layer.id
                                    propertyName = layer.name
                                    propertyVisible = layer.visible
                                    propertyLocked = layer.locked
                                    propertyOpacity = layer.opacity
                                    propertyBlendMode = layer.blendMode
                                    propertyAdjustment = (layer as? AdjustmentLayer)?.adjustment ?: LayerAdjustment()
                                },
                                modifier = Modifier.Companion.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "属性",
                                    modifier = Modifier.Companion.size(16.dp),
                                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(
                                onClick = { editorController.moveLayerUp(layer.id) },
                                enabled = upEnabled,
                                modifier = Modifier.Companion.size(32.dp)
                            ) {
                                Text(
                                    "↑",
                                    style = MaterialTheme.typography.caption,
                                    color = if (upEnabled) {
                                        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    } else {
                                        MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                    }
                                )
                            }
                            IconButton(
                                onClick = { editorController.moveLayerDown(layer.id) },
                                enabled = downEnabled,
                                modifier = Modifier.Companion.size(32.dp)
                            ) {
                                Text(
                                    "↓",
                                    style = MaterialTheme.typography.caption,
                                    color = if (downEnabled) {
                                        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    } else {
                                        MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                    }
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (editorController.isBackgroundLayer(layer)) {
                                        state.showTray("无法删除背景图层", "提示")
                                    } else {
                                        deleteConfirmLayerId = layer.id
                                    }
                                },
                                modifier = Modifier.Companion.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "删除",
                                    modifier = Modifier.Companion.size(16.dp),
                                    tint = MaterialTheme.colors.error.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 删除确认对话框
    deleteConfirmLayerId?.let { layerId ->
        val layerToDelete = layers.firstOrNull { it.id == layerId }
        if (layerToDelete != null) {
            AlertDialog(
                onDismissRequest = { deleteConfirmLayerId = null },
                title = {
                    Text("确认删除")
                },
                text = {
                    Text("确定要删除图层 \"${layerToDelete.name}\" 吗？此操作无法撤销。")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editorController.removeLayer(layerId)
                            deleteConfirmLayerId = null
                        }
                    ) {
                        Text("删除", color = MaterialTheme.colors.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { deleteConfirmLayerId = null }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }

    propertyEditorLayerId?.let { layerId ->
        val layerToEdit = layers.firstOrNull { it.id == layerId }
        if (layerToEdit != null) {
            val imageLayer = layerToEdit as? ImageLayer
            val adjustmentLayer = layerToEdit as? AdjustmentLayer
            AlertDialog(
                onDismissRequest = { propertyEditorLayerId = null },
                title = {
                    Text("图层属性")
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LayerThumbnail(
                            layer = layerToEdit,
                            isActive = true,
                            modifier = Modifier.Companion
                                .size(64.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "类型: ${layerToEdit.type.toDisplayName()}",
                            style = MaterialTheme.typography.caption
                        )
                        Text(
                            text = "混合模式: ${layerToEdit.blendMode.toDisplayName()}",
                            style = MaterialTheme.typography.caption
                        )
                        OutlinedTextField(
                            value = propertyName,
                            onValueChange = { propertyName = it },
                            enabled = !editorController.isBackgroundLayer(layerToEdit),
                            singleLine = true,
                            modifier = Modifier.Companion.fillMaxWidth(),
                            label = { Text("图层名称") }
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = propertyVisible,
                                onCheckedChange = { propertyVisible = it }
                            )
                            Text("显示")
                            Spacer(modifier = Modifier.Companion.width(8.dp))
                            Checkbox(
                                checked = propertyLocked,
                                onCheckedChange = { propertyLocked = it }
                            )
                            Text("锁定")
                        }
                        Text(
                            text = "透明度: ${propertyOpacity.toOpacityPercent()}",
                            style = MaterialTheme.typography.body2
                        )
                        Slider(
                            value = propertyOpacity,
                            onValueChange = { propertyOpacity = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colors.primary,
                                activeTrackColor = MaterialTheme.colors.primary
                            )
                        )
                        ExposedSelectionMenu(
                            title = "混合模式",
                            index = LayerBlendMode.entries.indexOf(propertyBlendMode),
                            options = LayerBlendMode.entries.map { it.toDisplayName() },
                            onSelected = { propertyBlendMode = LayerBlendMode.entries[it] }
                        )
                        if (adjustmentLayer != null) {
                            Text(
                                text = "亮度: ${(propertyAdjustment.brightness * 100).toInt()}",
                                style = MaterialTheme.typography.body2
                            )
                            Slider(
                                value = propertyAdjustment.brightness,
                                onValueChange = {
                                    propertyAdjustment = propertyAdjustment.copy(brightness = it)
                                },
                                valueRange = -1f..1f
                            )
                            Text(
                                text = "对比度: ${"%.2f".format(propertyAdjustment.contrast)}",
                                style = MaterialTheme.typography.body2
                            )
                            Slider(
                                value = propertyAdjustment.contrast,
                                onValueChange = {
                                    propertyAdjustment = propertyAdjustment.copy(contrast = it)
                                },
                                valueRange = 0f..3f
                            )
                            Text(
                                text = "饱和度: ${"%.2f".format(propertyAdjustment.saturation)}",
                                style = MaterialTheme.typography.body2
                            )
                            Slider(
                                value = propertyAdjustment.saturation,
                                onValueChange = {
                                    propertyAdjustment = propertyAdjustment.copy(saturation = it)
                                },
                                valueRange = 0f..3f
                            )
                            Text(
                                text = if (adjustmentLayer.targetLayerIds.isEmpty()) {
                                    "作用范围: 所有下层图层"
                                } else {
                                    "作用范围: ${adjustmentLayer.targetLayerIds.size} 个目标图层"
                                },
                                style = MaterialTheme.typography.caption
                            )
                        }
                        if (imageLayer != null && !editorController.isBackgroundLayer(layerToEdit)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        imageLayer.image?.let { bitmap ->
                                            val squareSize = minOf(bitmap.width, bitmap.height).toFloat()
                                            val left = (bitmap.width - squareSize) / 2f
                                            val top = (bitmap.height - squareSize) / 2f
                                            editorController.updateImageLayerCrop(
                                                imageLayer.id,
                                                Rect(left, top, left + squareSize, top + squareSize)
                                            )
                                        }
                                    }
                                ) {
                                    Text("中心方形裁剪", style = MaterialTheme.typography.caption)
                                }
                                OutlinedButton(
                                    onClick = { editorController.updateImageLayerCrop(imageLayer.id, null) }
                                ) {
                                    Text("清除裁剪", style = MaterialTheme.typography.caption)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        editorController.createCenteredMask(imageLayer.id, ImageLayerMaskShape.RECTANGLE)
                                    }
                                ) {
                                    Text("矩形蒙版", style = MaterialTheme.typography.caption)
                                }
                                OutlinedButton(
                                    onClick = {
                                        editorController.createCenteredMask(imageLayer.id, ImageLayerMaskShape.ELLIPSE)
                                    }
                                ) {
                                    Text("椭圆蒙版", style = MaterialTheme.typography.caption)
                                }
                                OutlinedButton(
                                    onClick = { editorController.setImageLayerMask(imageLayer.id, null) }
                                ) {
                                    Text("清除蒙版", style = MaterialTheme.typography.caption)
                                }
                            }
                            layerToEdit.groupId?.let { groupId ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { editorController.moveGroupUp(groupId) }) {
                                        Text("组上移", style = MaterialTheme.typography.caption)
                                    }
                                    OutlinedButton(onClick = { editorController.moveGroupDown(groupId) }) {
                                        Text("组下移", style = MaterialTheme.typography.caption)
                                    }
                                    OutlinedButton(onClick = { editorController.ungroup(groupId) }) {
                                        Text("取消分组", style = MaterialTheme.typography.caption)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editorController.renameLayer(layerId, propertyName.trim().ifEmpty { layerToEdit.name })
                            editorController.setLayerVisibility(layerId, propertyVisible)
                            editorController.setLayerLocked(layerId, propertyLocked)
                            editorController.setLayerOpacity(layerId, propertyOpacity)
                            editorController.setLayerBlendMode(layerId, propertyBlendMode)
                            if (adjustmentLayer != null) {
                                editorController.updateAdjustmentLayer(layerId, propertyAdjustment)
                            }
                            propertyEditorLayerId = null
                        }
                    ) {
                        Text("应用")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { propertyEditorLayerId = null }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }

    groupEditorId?.let { groupId ->
        val group = layerGroups.firstOrNull { it.id == groupId }
        if (group != null) {
            AlertDialog(
                onDismissRequest = { groupEditorId = null },
                title = { Text("分组属性") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            singleLine = true,
                            modifier = Modifier.Companion.fillMaxWidth(),
                            label = { Text("分组名称") }
                        )
                        Text(
                            text = "组内图层: ${editorController.getGroupLayers(groupId).size}",
                            style = MaterialTheme.typography.caption
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editorController.renameGroup(groupId, groupName.trim().ifEmpty { group.name })
                            groupEditorId = null
                        }
                    ) {
                        Text("应用")
                    }
                },
                dismissButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { editorController.setGroupVisibility(groupId, false) }) {
                            Text("隐藏组")
                        }
                        TextButton(onClick = { editorController.setGroupLocked(groupId, true) }) {
                            Text("锁定组")
                        }
                        TextButton(onClick = { editorController.ungroup(groupId) }) {
                            Text("解散")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun LayerThumbnail(
    layer: Layer,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isActive) {
            MaterialTheme.colors.primary.copy(alpha = 0.08f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.04f)
        },
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        modifier = modifier
    ) {
        when (layer) {
            is ImageLayer -> {
                val bitmap = layer.image
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("图", style = MaterialTheme.typography.caption)
                    }
                }
            }
            is ShapeLayer -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "${layer.totalShapeCount()}形",
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            is AdjustmentLayer -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "调",
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("层", style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}

private fun LayerType.toDisplayName(): String = when (this) {
    LayerType.IMAGE -> "图像层"
    LayerType.SHAPE -> "形状层"
    LayerType.ADJUSTMENT -> "调整层"
}

private fun Float.toOpacityPercent(): String = "${(this * 100).toInt()}%"

private fun LayerBlendMode.toDisplayName(): String = when (this) {
    LayerBlendMode.NORMAL -> "Normal"
    LayerBlendMode.MULTIPLY -> "Multiply"
    LayerBlendMode.SCREEN -> "Screen"
    LayerBlendMode.OVERLAY -> "Overlay"
}

private fun ShapeLayer.totalShapeCount(): Int =
    displayLines.size +
        displayCircles.size +
        displayTriangles.size +
        displayRectangles.size +
        displayPolygons.size +
        displayTexts.size
