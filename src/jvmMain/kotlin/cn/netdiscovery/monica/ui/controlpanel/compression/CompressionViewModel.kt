package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import javax.imageio.ImageIO

/**
 * 图像压缩 ViewModel
 * 
 * @author: Tony Shen
 * @date: 2025/12/07
 * @version: V1.0
 */
private val logger: Logger = LoggerFactory.getLogger(CompressionViewModel::class.java)

class CompressionViewModel {
    
    // 压缩算法选择
    var selectedAlgorithm by mutableStateOf(CompressionAlgorithm.JPEG_QUALITY)
    
    // JPEG 和 WebP Lossy 的质量参数（0.0 - 1.0）
    var quality by mutableStateOf(0.8f)
    
    // PNG 和 WebP Lossless 的压缩级别（0 - 9）
    var compressionLevel by mutableStateOf(6)
    
    // 压缩进度显示
    var isCompressing by mutableStateOf(false)
    var compressionProgress by mutableStateOf(0f)
    var compressionMessage by mutableStateOf("")
    
    // 压缩结果统计
    var originalSize by mutableStateOf(0L)
    var compressedSize by mutableStateOf(0L)
    var compressionRatio by mutableStateOf(0)
    var showResult by mutableStateOf(false)
    
    // 单张图模式下选择的图片
    var selectedImage by mutableStateOf<java.awt.image.BufferedImage?>(null)
    
    // 单张图模式下选择的原始文件
    var selectedImageFile by mutableStateOf<File?>(null)
    
    // 单张图模式下选择的原始文件大小
    var selectedImageFileSize by mutableStateOf(0L)
    
    // 压缩后的图片
    var compressedImage by mutableStateOf<java.awt.image.BufferedImage?>(null)
    
    // WebP 降级提示
    var webpFallbackWarning by mutableStateOf<String?>(null)
    
    // 批量压缩时的文件数
    var totalFiles by mutableStateOf(0)
    var processedFiles by mutableStateOf(0)
    
    // 压缩任务引用（用于取消）
    private var compressionJob: Job? = null
    
    /**
     * 获取当前的压缩参数
     */
    fun getCurrentParams(): CompressionParams {
        return CompressionParams(
            algorithm = selectedAlgorithm,
            quality = quality,
            compressionLevel = compressionLevel
        )
    }
    
    /**
     * 压缩单张图片到预览（不保存文件）
     */
    fun compressSingleImageToPreview(
        scope: CoroutineScope,
        getString: (String) -> String
    ) {
        // 取消之前的任务
        compressionJob?.cancel()
        compressionJob = scope.launch(Dispatchers.Default) {
            try {
                val image = selectedImage ?: run {
                    compressionMessage = getString("error_please_select_image")
                    return@launch
                }
                
                // 更新状态
                isCompressing = true
                compressionMessage = getString("compressing_image")
                compressionProgress = 0.3f
                
                val params = getCurrentParams()
                // 使用原始文件的实际大小
                originalSize = selectedImageFileSize
                
                compressionProgress = 0.6f
                
                // 检查 WebP 支持
                if ((params.algorithm == CompressionAlgorithm.WEBP_LOSSY || 
                     params.algorithm == CompressionAlgorithm.WEBP_LOSSLESS) &&
                    !ImageCompressionUtils.isWebPSupported()) {
                    val fallbackFormat = ImageCompressionUtils.getWebPFallbackFormat(
                        params.algorithm == CompressionAlgorithm.WEBP_LOSSY
                    )
                    webpFallbackWarning = getString("webp_not_supported").format(fallbackFormat)
                } else {
                    // 检查格式转换警告（JPG 转 PNG 等）
                    val formatWarningKey = ImageCompressionUtils.checkFormatConversionWarning(
                        selectedImageFile,
                        params.algorithm
                    )
                    webpFallbackWarning = formatWarningKey?.let { getString(it) }
                }
                
                // 压缩到内存
                val compressedBytes = ImageCompressionUtils.compressImage(image, params)
                
                if (compressedBytes != null) {
                    val (compressedData, usedFallback) = compressedBytes
                    compressedSize = compressedData.size.toLong()
                    compressionRatio = ImageCompressionUtils.calculateCompressionRatio(originalSize, compressedSize)
                    
                    // 如果使用了降级处理，更新警告信息
                    if (usedFallback && webpFallbackWarning == null) {
                        val fallbackFormat = ImageCompressionUtils.getWebPFallbackFormat(
                            params.algorithm == CompressionAlgorithm.WEBP_LOSSY
                        )
                        webpFallbackWarning = getString("webp_encode_failed").format(fallbackFormat)
                    }
                    
                    // 从压缩后的字节数组加载图片
                    val compressedImageData = javax.imageio.ImageIO.read(java.io.ByteArrayInputStream(compressedData))
                    if (compressedImageData != null) {
                        compressedImage = compressedImageData
                    }
                    
                    compressionMessage = getString("compression_success")
                    compressionProgress = 1f
                    showResult = true
                } else {
                    compressionMessage = getString("compression_failed")
                    compressionProgress = 0f
                }
                isCompressing = false
            } catch (e: Exception) {
                logger.error("Single image compression error", e)
                compressionMessage = getString("compression_error").format(e.message ?: "")
                compressionProgress = 0f
                isCompressing = false
            }
        }
        compressionJob = null
    }
    
    /**
     * 批量压缩文件夹中的所有图片
     * 使用流式处理优化内存使用
     */
    fun compressBatch(
        sourceDir: File,
        outputDir: File,
        scope: CoroutineScope,
        getString: (String) -> String
    ) {
        // 取消之前的任务
        compressionJob?.cancel()
        compressionJob = scope.launch(Dispatchers.Default) {
            try {
                isCompressing = true
                compressionMessage = getString("preparing_batch_compression")
                compressionProgress = 0f
                
                val params = getCurrentParams()
                val imageExtensions = setOf("jpg", "jpeg", "png", "bmp", "gif", "tiff")
                
                // 先统计文件数量（用于进度显示）
                var fileCount = 0
                sourceDir.walk().forEach { file ->
                    if (file.isFile && file.extension.lowercase() in imageExtensions) {
                        fileCount++
                    }
                }
                
                totalFiles = fileCount
                processedFiles = 0
                
                if (totalFiles == 0) {
                    compressionMessage = getString("no_images_in_folder")
                    isCompressing = false
                    return@launch
                }
                
                if (!outputDir.exists()) {
                    outputDir.mkdirs()
                }
                
                var totalOriginalSize = 0L
                var totalCompressedSize = 0L
                
                // 使用流式处理，逐个处理文件，避免一次性加载所有文件到内存
                sourceDir.walk().forEach { file ->
                    // 检查是否已取消
                    if (!isActive) {
                        compressionMessage = getString("compression_cancelled")
                        return@forEach
                    }
                    
                    if (!file.isFile || file.extension.lowercase() !in imageExtensions) {
                        return@forEach
                    }
                    
                    try {
                        processedFiles++
                        compressionMessage = getString("compressing_file").format(file.name, processedFiles, totalFiles)
                        compressionProgress = processedFiles.toFloat() / totalFiles
                        
                        // 读取图片（处理完立即释放）
                        val image = ImageIO.read(file) ?: run {
                            return@forEach
                        }
                        
                        val baseName = file.nameWithoutExtension
                        val outputFileName = "$baseName.${params.algorithm.format}"
                        val outputFile = File(outputDir, outputFileName)
                        
                        val fileOriginalSize = file.length()
                        
                        val result = ImageCompressionUtils.compressAndSaveImage(image, outputFile, params)
                        
                        // 立即释放图片内存
                        image.flush()
                        
                        if (result != null) {
                            val (savedSize, _) = result
                            totalOriginalSize += fileOriginalSize
                            totalCompressedSize += savedSize
                        }
                        
                    } catch (e: Exception) {
                        logger.error("File processing error: ${file.absolutePath}", e)
                    }
                }
                
                originalSize = totalOriginalSize
                compressedSize = totalCompressedSize
                compressionRatio = ImageCompressionUtils.calculateCompressionRatio(totalOriginalSize, totalCompressedSize)
                compressionMessage = getString("batch_compression_completed").format(processedFiles, totalFiles)
                showResult = true
                
            } catch (e: Exception) {
                logger.error("Batch compression error", e)
                compressionMessage = getString("batch_compression_error").format(e.message ?: "")
            } finally {
                isCompressing = false
                compressionJob = null
            }
        }
    }
    
    /**
     * 取消压缩任务
     */
    fun cancelCompression(getString: (String) -> String) {
        compressionJob?.cancel()
        compressionJob = null
        isCompressing = false
        compressionMessage = getString("compression_cancelled")
    }
    
    /**
     * 重置压缩结果（切换模式时调用）
     */
    fun resetResult() {
        showResult = false
        // 注意：不清空 originalSize，因为单张图模式下它应该保留原始文件大小
        // 批量模式下 originalSize 会在 compressBatch 中重新计算
        compressedSize = 0L
        compressionRatio = 0
        processedFiles = 0
        totalFiles = 0
        compressionProgress = 0f
        compressionMessage = ""
        compressedImage = null
        webpFallbackWarning = null
    }
    
    /**
     * 应用压缩后的图片到编辑器
     */
    fun applyCompressedImage(state: ApplicationState) {
        val image = compressedImage ?: return
        
        // 同时更新 rawImage 和 currentImage，保持与其他地方的一致性
        state.rawImage = image
        state.currentImage = image
        // 压缩后的图片没有原始文件，设置为 null
        state.rawImageFile = null
    }
}