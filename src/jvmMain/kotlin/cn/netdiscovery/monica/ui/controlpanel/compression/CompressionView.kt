package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.utils.CompressionAlgorithm
import cn.netdiscovery.monica.utils.ImageCompressionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * 图像压缩 UI 视图
 * 
 * @author: Tony Shen
 * @date: 2025/12/07
 * @version: V2.0
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun compressionView(state: ApplicationState) {
    val viewModel: CompressionViewModel = remember { CompressionViewModel() }
    val i18nState = rememberI18nState()
    
    var compressionMode by remember { mutableStateOf(CompressionMode.SINGLE) }
    var selectedOutputDir by remember { mutableStateOf<File?>(null) }
    
    // Toast 状态（提升到顶层，使 toast 在整个页面居中）
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    
    // 当切换模式时，重置结果
    LaunchedEffect(compressionMode) {
        viewModel.resetResult()
        if (compressionMode == CompressionMode.BATCH) {
            viewModel.selectedImage = null
            viewModel.selectedImageFileSize = 0L
        }
    }
    
    // 显示 Toast（在整个页面居中）
    if (showToast) {
        cn.netdiscovery.monica.ui.widget.centerToast(
            modifier = Modifier.fillMaxSize(),
            message = toastMessage
        ) {
            showToast = false
        }
    }
    
    // 左右分栏布局
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 左侧控制面板
        LeftControlPanel(
            modifier = Modifier
                .width(400.dp)
                .fillMaxHeight(),
            viewModel = viewModel,
            compressionMode = compressionMode,
            onModeChange = { compressionMode = it },
            selectedOutputDir = selectedOutputDir,
            onOutputDirSelected = { selectedOutputDir = it },
            state = state,
            i18nState = i18nState,
            onShowToast = { message ->
                toastMessage = message
                showToast = true
            }
        )
        
        // 右侧图片预览对比区域
        CompressionRightPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            state = state,
            viewModel = viewModel,
            compressionMode = compressionMode,
            onShowToast = { message ->
                toastMessage = message
                showToast = true
            },
            i18nState = i18nState
        )
    }
}

@Composable
private fun LeftControlPanel(
    modifier: Modifier = Modifier,
    viewModel: CompressionViewModel,
    compressionMode: CompressionMode,
    onModeChange: (CompressionMode) -> Unit,
    selectedOutputDir: File?,
    onOutputDirSelected: (File) -> Unit,
    state: ApplicationState,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState,
    onShowToast: (String) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource("images/controlpanel/compress.png"),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.primary
                )
                Text(
                    text = i18nState.getString("image_compression"),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
            
            Divider()
            
            // 压缩算法选择（下拉菜单样式）
            CompressionAlgorithmDropdown(
                viewModel = viewModel,
                i18nState = i18nState
            )
            
            // WebP 不支持提示
            if ((viewModel.selectedAlgorithm == CompressionAlgorithm.WEBP_LOSSY ||
                 viewModel.selectedAlgorithm == CompressionAlgorithm.WEBP_LOSSLESS) &&
                !ImageCompressionUtils.isWebPSupported()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colors.error
                        )
                        Text(
                            text = i18nState.getString("webp_not_supported_auto_convert").format(ImageCompressionUtils.getWebPFallbackFormat(viewModel.selectedAlgorithm == CompressionAlgorithm.WEBP_LOSSY)),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
            }
            
            Divider()
            
            // 质量设置
            when (viewModel.selectedAlgorithm) {
                CompressionAlgorithm.JPEG_QUALITY,
                CompressionAlgorithm.WEBP_LOSSY -> {
                    QualitySlider(
                        value = viewModel.quality,
                        onValueChange = { viewModel.quality = it },
                        i18nState = i18nState
                    )
                }
                CompressionAlgorithm.PNG_OPTIMIZATION,
                CompressionAlgorithm.WEBP_LOSSLESS -> {
                    CompressionLevelSlider(
                        value = viewModel.compressionLevel,
                        onValueChange = { viewModel.compressionLevel = it },
                        i18nState = i18nState
                    )
                }
            }
            
            Divider()
            
            // 输入选择
            CompressionInputSection(
                compressionMode = compressionMode,
                onModeChange = onModeChange,
                selectedOutputDir = selectedOutputDir,
                onOutputDirSelected = onOutputDirSelected,
                viewModel = viewModel,
                state = state,
                i18nState = i18nState,
                onShowToast = onShowToast
            )
            
            // 压缩进度和消息显示
            if (viewModel.isCompressing || viewModel.compressionMessage.isNotEmpty()) {
                Divider()
                CompressionProgressSection(
                    viewModel = viewModel,
                    onCancel = { viewModel.cancelCompression { i18nState.getString(it) } },
                    i18nState = i18nState
                )
            }
        }
    }
}


/**
 * 压缩模式枚举
 */
enum class CompressionMode(val displayName: String) {
    SINGLE("单张图片"),
    BATCH("批量图片")
}