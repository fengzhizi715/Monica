package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.centerToast
import cn.netdiscovery.monica.utils.*
import cn.netdiscovery.monica.utils.chooseImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JFrame

@Composable
fun CompressionRightPanel(
    modifier: Modifier = Modifier,
    state: ApplicationState,
    viewModel: CompressionViewModel,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    compressionMode: CompressionMode,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    if (showToast) {
        centerToast(
            modifier = Modifier,
            message = toastMessage
        ) {
            showToast = false
        }
    }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 标签页
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.surface,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colors.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text(i18nState.getString("original")) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text(i18nState.getString("compressed")) },
                    enabled = viewModel.showResult
                )
            }
            
            // 图片预览区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedTabIndex == 0 -> {
                        OriginalImageTab(
                            viewModel = viewModel,
                            state = state,
                            compressionMode = compressionMode,
                            i18nState = i18nState,
                            onImageSelected = { selectedFile ->
                                try {
                                    // 先获取原始文件大小
                                    val fileSize = selectedFile.length()
                                    val image = ImageIO.read(selectedFile)
                                    if (image != null) {
                                        viewModel.selectedImage = image
                                        viewModel.selectedImageFile = selectedFile
                                        viewModel.selectedImageFileSize = fileSize
                                    } else {
                                        toastMessage = i18nState.getString("cannot_read_image_file")
                                        showToast = true
                                    }
                                } catch (e: Exception) {
                                    toastMessage = i18nState.getString("load_image_failed").format(e.message ?: "")
                                    showToast = true
                                }
                            },
                            isCompressing = viewModel.isCompressing
                        )
                    }
                    selectedTabIndex == 1 -> {
                        CompressedImageTab(
                            viewModel = viewModel,
                            compressionMode = compressionMode,
                            i18nState = i18nState,
                            onSave = { outputFile ->
                                if (viewModel.compressedImage != null) {
                                    val params = viewModel.getCurrentParams()
                                    val saveResult = ImageCompressionUtils.compressAndSaveImage(
                                        viewModel.compressedImage!!,
                                        outputFile,
                                        params
                                    )
                                    if (saveResult != null) {
                                        toastMessage = i18nState.getString("save_success").format(outputFile.absolutePath)
                                    } else {
                                        toastMessage = i18nState.getString("save_failed")
                                    }
                                    showToast = true
                                }
                            }
                        )
                    }
                }
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
            
            // 文件大小信息和操作按钮（仅在单张图模式下显示操作按钮）
            if (viewModel.showResult) {
                CompressionResultInfo(
                    viewModel = viewModel,
                    state = state,
                    compressionMode = compressionMode,
                    i18nState = i18nState
                )
            }
        }
    }
}

@Composable
private fun OriginalImageTab(
    viewModel: CompressionViewModel,
    state: ApplicationState,
    compressionMode: CompressionMode,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState,
    onImageSelected: (File) -> Unit,
    isCompressing: Boolean
) {
    val imageToShow = viewModel.selectedImage
    if (imageToShow != null) {
        Image(
            painter = imageToShow.toPainter(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    } else {
        // 单张图模式下，显示选择图片按钮
        if (compressionMode == CompressionMode.SINGLE) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        chooseImage(state, onImageSelected)
                    },
                    enabled = !isCompressing,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(
                        i18nState.getString("select_image"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Text(
                text = i18nState.getString("please_select_or_load_image"),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun CompressedImageTab(
    viewModel: CompressionViewModel,
    compressionMode: CompressionMode,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState,
    onSave: (File) -> Unit
) {
    if (viewModel.compressedImage != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = viewModel.compressedImage!!.toPainter(),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            
            // 保存按钮（仅在单张图模式下显示）
            if (compressionMode == CompressionMode.SINGLE) {
                Button(
                    onClick = {
                        val fileChooser = JFileChooser()
                        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
                        fileChooser.selectedFile = File("compressed.${viewModel.selectedAlgorithm.format}")
                        val result = fileChooser.showSaveDialog(JFrame())
                        if (result == JFileChooser.APPROVE_OPTION) {
                            var outputFile = fileChooser.selectedFile
                            
                            // 确保文件扩展名正确
                            if (!outputFile.name.contains(".")) {
                                outputFile = File(outputFile.parent, "${outputFile.name}.${viewModel.selectedAlgorithm.format}")
                            }
                            
                            // 检查文件是否已存在
                            if (outputFile.exists()) {
                                val confirmResult = javax.swing.JOptionPane.showConfirmDialog(
                                    null,
                                    i18nState.getString("file_exists_overwrite").format(outputFile.name),
                                    i18nState.getString("file_overwrite_confirm"),
                                    javax.swing.JOptionPane.YES_NO_OPTION,
                                    javax.swing.JOptionPane.WARNING_MESSAGE
                                )
                                if (confirmResult != javax.swing.JOptionPane.YES_OPTION) {
                                    return@Button
                                }
                            }
                            
                            onSave(outputFile)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(
                        i18nState.getString("save_compressed_image"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        Text(
            text = i18nState.getString("please_compress_first"),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun CompressionResultInfo(
    viewModel: CompressionViewModel,
    state: ApplicationState,
    compressionMode: CompressionMode,
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
                text = "${i18nState.getString("compressed")}: ${ImageCompressionUtils.formatFileSize(viewModel.compressedSize)} ${i18nState.getString("compression_ratio")}: ${viewModel.compressionRatio}%",
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
                i18nState = i18nState
            )
        }
    }
}

