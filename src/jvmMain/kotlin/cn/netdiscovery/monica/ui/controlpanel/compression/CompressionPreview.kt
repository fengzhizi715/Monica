package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.*
import cn.netdiscovery.monica.utils.chooseImage
import java.io.File
import javax.imageio.ImageIO

@Composable
fun CompressionRightPanel(
    modifier: Modifier = Modifier,
    state: ApplicationState,
    viewModel: CompressionViewModel,
    compressionMode: CompressionMode,
    onShowToast: (String) -> Unit,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 图片预览区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CompressionPreviewArea(
                    state = state,
                    viewModel = viewModel,
                    compressionMode = compressionMode,
                    onShowToast = onShowToast,
                    i18nState = i18nState
                )
            }
            
            // WebP 降级警告
            if (viewModel.webpFallbackWarning != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource("images/controlpanel/compress.png"),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.error
                        )
                        Text(
                            text = viewModel.webpFallbackWarning!!,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
            }

            // 压缩后文件变大提示
            if (viewModel.sizeChangeWarning != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource("images/controlpanel/compress.png"),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.error
                        )
                        Text(
                            text = viewModel.sizeChangeWarning!!,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
            }
            
            // 文件大小信息和操作按钮（仅在单张图模式下显示操作按钮）
            if (viewModel.showResult) {
                CompressionResultInfo(
                    viewModel = viewModel,
                    state = state,
                    compressionMode = compressionMode,
                    onShowToast = onShowToast,
                    i18nState = i18nState
                )
            }
        }
    }
}

@Composable
private fun CompressionPreviewArea(
    state: ApplicationState,
    viewModel: CompressionViewModel,
    compressionMode: CompressionMode,
    onShowToast: (String) -> Unit,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    // 批量模式：右侧仅作为信息展示区，避免“无意义预览”
    if (compressionMode == CompressionMode.BATCH) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource("images/controlpanel/compress.png"),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.35f)
            )
            Text(
                text = i18nState.getString("batch_mode_no_preview"),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.body2
            )
        }
        return
    }

    val original = viewModel.selectedImage
    val compressed = viewModel.compressedImage

    if (original == null) {
        if (compressionMode == CompressionMode.SINGLE) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(
                    painter = painterResource("images/controlpanel/compress.png"),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = i18nState.getString("please_select_image_to_compress"),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.body1
                )
                Button(
                    onClick = {
                        chooseImage(state) { selectedFile ->
                            try {
                                val fileSize = selectedFile.length()
                                val image = ImageIO.read(selectedFile)
                                if (image != null) {
                                    viewModel.selectedImage = image
                                    viewModel.selectedImageFile = selectedFile
                                    viewModel.selectedImageFileSize = fileSize
                                } else {
                                    onShowToast(i18nState.getString("cannot_read_image_file"))
                                }
                            } catch (e: Exception) {
                                onShowToast(i18nState.getString("load_image_failed").format(e.message ?: ""))
                            }
                        }
                    },
                    enabled = !viewModel.isCompressing,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(i18nState.getString("select_image"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Text(
                text = i18nState.getString("please_select_or_load_image"),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
        return
    }

    // 单图模式：右侧优先展示“压缩后的预览”；Reset/清理结果后会回到原图展示
    val displayImage = if (viewModel.showResult && compressed != null) compressed else original

    Image(
        painter = displayImage.toPainter(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun CompressionResultInfo(
    viewModel: CompressionViewModel,
    state: ApplicationState,
    compressionMode: CompressionMode,
    onShowToast: (String) -> Unit,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${i18nState.getString("original")}: ${ImageCompressionUtils.formatFileSize(viewModel.originalSize)}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = run {
                    val original = viewModel.originalSize
                    val compressed = viewModel.compressedSize
                    val ratioLabel = if (original > 0 && compressed > original) {
                        i18nState.getString("size_increase")
                    } else {
                        i18nState.getString("compression_ratio")
                    }
                    val percent = if (original > 0) abs((100 * (1 - compressed.toDouble() / original)).toInt()) else 0
                    "${i18nState.getString("compressed")}: ${ImageCompressionUtils.formatFileSize(compressed)} $ratioLabel: $percent%"
                },
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
        }
        
        // 操作按钮（仅在单张图模式下显示）
        if (compressionMode == CompressionMode.SINGLE) {
            CompressionActionButtons(
                viewModel = viewModel,
                state = state,
                onShowToast = onShowToast,
                i18nState = i18nState
            )
        }
    }
}

