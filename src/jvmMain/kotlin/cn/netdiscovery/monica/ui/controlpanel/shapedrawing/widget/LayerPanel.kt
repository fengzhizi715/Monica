package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerType
import cn.netdiscovery.monica.state.ApplicationState
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
    val layers by editorController.layerManager.layers.collectAsState()
    val activeLayer by editorController.layerManager.activeLayer.collectAsState()

    var editingLayerId by remember { mutableStateOf<UUID?>(null) }
    var editingName by remember { mutableStateOf("") }

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
                            val bufferedImage = getBufferedImage(file, state)
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

        Spacer(modifier = Modifier.Companion.height(4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            displayLayers.forEach { layer ->
                val actualIndex = layers.indexOfFirst { it.id == layer.id }
                val isActive = activeLayer?.id == layer.id
                val upEnabled = actualIndex < layers.lastIndex
                val downEnabled = actualIndex > 0
                val cardColor = if (isActive) {
                    MaterialTheme.colors.primary.copy(alpha = 0.08f)
                } else {
                    MaterialTheme.colors.surface
                }
                val borderColor = if (isActive) {
                    MaterialTheme.colors.primary.copy(alpha = 0.4f)
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = cardColor,
                    border = BorderStroke(
                        width = if (isActive) 2.dp else 1.dp,
                        color = borderColor
                    ),
                    modifier = Modifier.Companion.fillMaxWidth()
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
                            // 可见性复选框
                            Checkbox(
                                checked = layer.visible,
                                onCheckedChange = { checked ->
                                    editorController.layerManager.setLayerVisibility(layer.id, checked)
                                },
                                modifier = Modifier.Companion.size(20.dp)
                            )

                            // 图层类型图标
                            Box(
                                modifier = Modifier.Companion.size(32.dp),
                                contentAlignment = Alignment.Companion.Center
                            ) {
                                Surface(
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                                    color = if (isActive) {
                                        MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                    } else {
                                        MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
                                    },
                                    modifier = Modifier.Companion.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Companion.Center) {
                                        Text(
                                            text = if (layer.type == LayerType.IMAGE) "图" else "形",
                                            style = MaterialTheme.typography.caption.copy(
                                                color = if (isActive) {
                                                    MaterialTheme.colors.primary
                                                } else {
                                                    MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                                },
                                                fontWeight = FontWeight.Companion.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            // 图层信息
                            Column(
                                modifier = Modifier.Companion.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                if (editingLayerId == layer.id) {
                                    OutlinedTextField(
                                        value = editingName,
                                        onValueChange = { editingName = it },
                                        singleLine = true,
                                        modifier = Modifier.Companion.fillMaxWidth(),
                                        textStyle = MaterialTheme.typography.body2
                                    )
                                } else {
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
                            }
                        }

                        // 操作按钮区域
                        if (editingLayerId == layer.id) {
                            Row(
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        editorController.layerManager.renameLayer(
                                            layer.id,
                                            editingName.trim().ifEmpty { layer.name })
                                        editingLayerId = null
                                    },
                                    modifier = Modifier.Companion.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colors.primary
                                    )
                                ) {
                                    Text("保存", style = MaterialTheme.typography.caption)
                                }
                                OutlinedButton(
                                    onClick = { editingLayerId = null },
                                    modifier = Modifier.Companion.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Text("取消", style = MaterialTheme.typography.caption)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        editorController.layerManager.setLayerLocked(layer.id, !layer.locked)
                                    },
                                    modifier = Modifier.Companion.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = if (layer.locked) "解锁" else "锁定",
                                        modifier = Modifier.Companion.size(16.dp),
                                        tint = if (layer.locked) {
                                            MaterialTheme.colors.error
                                        } else {
                                            MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        }
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        editingLayerId = layer.id
                                        editingName = layer.name
                                    },
                                    modifier = Modifier.Companion.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "重命名",
                                        modifier = Modifier.Companion.size(16.dp),
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                IconButton(
                                    onClick = { editorController.layerManager.moveLayerUp(layer.id) },
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
                                    onClick = { editorController.layerManager.moveLayerDown(layer.id) },
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
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun LayerType.toDisplayName(): String = when (this) {
    LayerType.IMAGE -> "图像层"
    LayerType.SHAPE -> "形状层"
}