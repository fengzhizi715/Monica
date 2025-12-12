package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.i18n.I18nState
import java.awt.image.BufferedImage

/**
 * 中间图像预览区域
 */
@Composable
fun FilterPreviewArea(
    modifier: Modifier = Modifier,
    state: ApplicationState,
    previewImage: BufferedImage?,
    zoomLevel: Float,
    onZoomChange: (Float) -> Unit,
    onImageClick: () -> Unit,
    i18nState: I18nState
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = Color(0xFFF5F5F5),
        elevation = 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 显示预览图像或当前图像
            val displayImage = previewImage ?: state.currentImage
            
            if (displayImage != null) {
                // 图像预览
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 2.dp,
                    backgroundColor = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = displayImage.toPainter(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = zoomLevel,
                                    scaleY = zoomLevel
                                )
                        )
                    }
                }
            } else {
                // 空状态提示
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clickable(onClick = onImageClick),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 2.dp,
                    backgroundColor = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = i18nState.getString("click_to_select_image"),
                                fontSize = 18.sp,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            // 底部缩放控制栏
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.9f),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 缩小按钮
                    TextButton(
                        onClick = {
                            onZoomChange((zoomLevel - 0.1f).coerceAtLeast(0.1f))
                        },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "−",
                            fontSize = 18.sp,
                            color = Color(0xFF222222),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // 缩放百分比显示
                    Text(
                        text = "${(zoomLevel * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222),
                        modifier = Modifier.width(50.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    // 放大按钮
                    IconButton(
                        onClick = {
                            onZoomChange((zoomLevel + 0.1f).coerceAtMost(5.0f))
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = Color(0xFF222222)
                        )
                    }
                    
                    // 分隔线
                    Divider(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp),
                        color = Color(0xFFE0E0E0)
                    )
                    
                    // 适应屏幕按钮（使用文本代替图标）
                    TextButton(
                        onClick = {
                            onZoomChange(1.0f)
                        },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = i18nState.getString("fit"),
                            fontSize = 12.sp,
                            color = Color(0xFF222222)
                        )
                    }
                }
            }
        }
    }
}

