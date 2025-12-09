package cn.netdiscovery.monica.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

/**
 * 图像压缩工具类
 * 支持多种压缩算法：JPEG Quality、PNG Optimization、WebP Lossy、WebP Lossless
 * 
 * @author: Tony Shen
 * @date: 2025/12/07
 * @version: V1.0
 */
private val logger: Logger = LoggerFactory.getLogger(ImageCompressionUtils::class.java)

/**
 * 压缩算法枚举
 */
enum class CompressionAlgorithm(val displayName: String, val format: String) {
    JPEG_QUALITY("JPEG Quality", "jpg"),
    PNG_OPTIMIZATION("PNG Optimization", "png"),
    WEBP_LOSSY("WebP Lossy", "webp"),
    WEBP_LOSSLESS("WebP Lossless", "webp")
}

/**
 * 压缩参数数据类
 */
data class CompressionParams(
    val algorithm: CompressionAlgorithm = CompressionAlgorithm.JPEG_QUALITY,
    val quality: Float = 0.8f, // 0.0 - 1.0，用于 JPEG 和 WebP Lossy
    val compressionLevel: Int = 9 // 0 - 9，用于 PNG 和 WebP Lossless
) {
    init {
        require(quality in 0f..1f) { "Quality must be between 0 and 1" }
        require(compressionLevel in 0..9) { "Compression level must be between 0 and 9" }
    }
}

object ImageCompressionUtils {
    
    /**
     * 检查系统是否支持 WebP 格式
     */
    fun isWebPSupported(): Boolean {
        return try {
            val readers = ImageIO.getImageReadersByFormatName("webp")
            readers.hasNext()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取 WebP 降级后的格式
     */
    fun getWebPFallbackFormat(isLossy: Boolean): String {
        return if (isLossy) "JPEG" else "PNG"
    }
    
    /**
     * 压缩单张图片
     * 
     * @param image BufferedImage 图像对象
     * @param params 压缩参数
     * @return 压缩后的字节数组，失败返回 null
     */
    /**
     * 压缩单张图片
     * 
     * @param image BufferedImage 图像对象
     * @param params 压缩参数
     * @return Pair<压缩后的字节数组, 是否使用了降级处理>，失败返回 null
     */
    fun compressImage(image: BufferedImage, params: CompressionParams): Pair<ByteArray, Boolean>? {
        return try {
            val outputStream = ByteArrayOutputStream()
            var usedFallback = false
            
            when (params.algorithm) {
                CompressionAlgorithm.JPEG_QUALITY -> {
                    compressJPEG(image, params.quality, outputStream)
                }
                CompressionAlgorithm.PNG_OPTIMIZATION -> {
                    compressPNG(image, params.compressionLevel, outputStream)
                }
                CompressionAlgorithm.WEBP_LOSSY -> {
                    usedFallback = compressWebP(image, params.quality, true, outputStream)
                }
                CompressionAlgorithm.WEBP_LOSSLESS -> {
                    usedFallback = compressWebP(image, 1f, false, outputStream)
                }
            }
            
            Pair(outputStream.toByteArray(), usedFallback)
        } catch (e: Exception) {
            logger.error("Image compression failed", e)
            null
        }
    }
    
    /**
     * 压缩单张图片（兼容旧接口）
     */
    @Deprecated("使用 compressImage 替代，可以获取降级信息")
    fun compressImageLegacy(image: BufferedImage, params: CompressionParams): ByteArray? {
        return compressImage(image, params)?.first
    }
    
    /**
     * 压缩 JPEG 图片
     */
    private fun compressJPEG(image: BufferedImage, quality: Float, outputStream: ByteArrayOutputStream) {
        val writer: ImageWriter? = ImageIO.getImageWritersByFormatName("jpg").next()
        if (writer != null) {
            val param = writer.defaultWriteParam
            var compressionApplied = false
            if (param.canWriteCompressed()) {
                try {
                    param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                    if (param.compressionTypes.isNotEmpty()) {
                        param.compressionType = param.compressionTypes[0]
                    }
                    param.compressionQuality = quality
                    compressionApplied = true
                } catch (e: Exception) {
                    // Compression parameters cannot be applied, use default compression
                }
            }
            
            val imageOutput = ImageIO.createImageOutputStream(outputStream)
            writer.output = imageOutput
            writer.write(null, javax.imageio.IIOImage(image, null, null), param)
            writer.dispose()
            imageOutput?.close()
        } else {
            // Fallback: use default JPEG output
            ImageIO.write(image, "jpg", outputStream)
        }
    }
    
    /**
     * 压缩 PNG 图片
     * PNG 使用 Deflate 压缩，compressionLevel 控制压缩级别
     * compressionLevel: 0 = 最低压缩（快速），9 = 最高压缩（慢速）
     * 
     * 注意：Java ImageIO 对 PNG 压缩的支持有限，某些实现可能不支持压缩参数
     * 如果无法应用压缩参数，会使用默认的 PNG 输出（可能压缩效果较差）
     * 
     * 优化：对于从 JPG 等有损格式转换来的图片，优化 BufferedImage 类型以提高压缩效率
     */
    private fun compressPNG(image: BufferedImage, compressionLevel: Int, outputStream: ByteArrayOutputStream) {
        // 优化图片类型以提高压缩效率
        // 如果图片不是标准的 RGB/ARGB 类型，转换为标准类型可以减少文件大小
        val optimizedImage = if (image.type != BufferedImage.TYPE_INT_RGB && 
                                 image.type != BufferedImage.TYPE_INT_ARGB &&
                                 image.type != BufferedImage.TYPE_3BYTE_BGR) {
            // 检查是否有透明通道
            val hasAlpha = image.colorModel.hasAlpha()
            val targetType = if (hasAlpha) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
            
            // 创建优化后的图片
            val converted = BufferedImage(image.width, image.height, targetType)
            val g: Graphics2D = converted.createGraphics() as Graphics2D
            // 使用高质量渲染
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.drawImage(image, 0, 0, null)
            g.dispose()
            converted
        } else {
            image
        }
        
        val writer: ImageWriter? = ImageIO.getImageWritersByFormatName("png").next()
        if (writer != null) {
            val param = writer.defaultWriteParam
            
            // 尝试设置压缩参数
            var compressionApplied = false
            if (param.canWriteCompressed()) {
                try {
                    param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                    if (param.compressionTypes.isNotEmpty()) {
                        param.compressionType = param.compressionTypes[0]
                    }
                    // PNG 压缩级别 0-9，9 是最高压缩率
                    // ImageIO 的 compressionQuality 值越高压缩率越高，所以直接使用 compressionLevel / 9f
                    param.compressionQuality = if (compressionLevel == 0) 0f else compressionLevel / 9f
                    compressionApplied = true
                } catch (e: Exception) {
                    // Compression parameters cannot be applied, use default compression
                }
            }
            
            val imageOutput = ImageIO.createImageOutputStream(outputStream)
            writer.output = imageOutput
            writer.write(null, javax.imageio.IIOImage(optimizedImage, null, null), param)
            writer.dispose()
            imageOutput?.close()
        } else {
            // Fallback: use default PNG output
            ImageIO.write(optimizedImage, "png", outputStream)
        }
    }
    
    /**
     * 压缩 WebP 图片
     * 注意：Java 内置不支持 WebP，需要外部库支持
     * 这里采用降级处理，转换为 JPEG 或 PNG
     * 
     * @return 是否使用了降级处理（true = 降级，false = 原生 WebP）
     */
    private fun compressWebP(
        image: BufferedImage,
        quality: Float,
        isLossy: Boolean,
        outputStream: ByteArrayOutputStream
    ): Boolean {
        // 检查是否支持 WebP
        if (!isWebPSupported()) {
            // WebP requires third-party library support (e.g., webp-imageio), fallback for now
            if (isLossy) {
                // WebP Lossy fallback to JPEG
                compressJPEG(image, quality, outputStream)
            } else {
                // WebP Lossless fallback to PNG
                compressPNG(image, 9, outputStream)
            }
            return true // Return true to indicate fallback was used
        }
        
        // If WebP is supported, try to use native WebP encoding
        // Note: WebP encoding library support is required here
        // Still using fallback for now
        if (isLossy) {
            compressJPEG(image, quality, outputStream)
        } else {
            compressPNG(image, 9, outputStream)
        }
        return true
    }
    
    /**
     * 压缩并保存图片到文件
     * 
     * @param image BufferedImage 图像对象
     * @param outputFile 输出文件路径
     * @param params 压缩参数
     * @return 压缩后的文件大小（字节），失败返回 -1
     */
    /**
     * 压缩并保存图片到文件
     * 
     * @param image BufferedImage 图像对象
     * @param outputFile 输出文件路径
     * @param params 压缩参数
     * @param originalFile 原始文件（可选，用于格式检测）
     * @return Pair<压缩后的文件大小（字节）, 是否使用了降级处理>，失败返回 null
     */
    fun compressAndSaveImage(
        image: BufferedImage,
        outputFile: File,
        params: CompressionParams,
        originalFile: File? = null
    ): Pair<Long, Boolean>? {
        return try {
            val result = compressImage(image, params) ?: return null
            val (compressedData, usedFallback) = result
            
            outputFile.writeBytes(compressedData)
            
            Pair(compressedData.size.toLong(), usedFallback)
        } catch (e: Exception) {
            logger.error("Failed to save compressed image", e)
            null
        }
    }
    
    /**
     * 检测格式转换是否可能导致文件变大
     * 
     * @param originalFile 原始文件
     * @param targetAlgorithm 目标压缩算法
     * @return 如果转换可能导致文件变大，返回警告信息的 key，否则返回 null
     */
    fun checkFormatConversionWarning(
        originalFile: File?,
        targetAlgorithm: CompressionAlgorithm
    ): String? {
        if (originalFile == null) return null
        
        val originalFormat = ImageFormatDetector.detectFormat(originalFile)
        
        return when {
            // JPG 转 PNG：PNG 是无损格式，通常比 JPG 大
            originalFormat == ImageFormat.JPEG && targetAlgorithm == CompressionAlgorithm.PNG_OPTIMIZATION -> {
                "format_conversion_warning_jpg_to_png"
            }
            // JPG 转 WebP Lossless：同样可能变大
            originalFormat == ImageFormat.JPEG && targetAlgorithm == CompressionAlgorithm.WEBP_LOSSLESS -> {
                "format_conversion_warning_jpg_to_webp_lossless"
            }
            else -> null
        }
    }
    
    /**
     * 压缩并保存图片到文件（兼容旧接口）
     */
    @Deprecated("使用 compressAndSaveImage 替代，可以获取降级信息")
    fun compressAndSaveImageLegacy(
        image: BufferedImage,
        outputFile: File,
        params: CompressionParams
    ): Long {
        return compressAndSaveImage(image, outputFile, params)?.first ?: -1
    }
    
    /**
     * 批量压缩文件夹中的所有图片
     * 
     * @param sourceDir 源文件夹
     * @param outputDir 输出文件夹
     * @param params 压缩参数
     * @return 成功压缩的文件数
     */
    fun compressBatch(
        sourceDir: File,
        outputDir: File,
        params: CompressionParams
    ): Int {
        return try {
            if (!sourceDir.isDirectory) {
                logger.error("Source path is not a directory: ${sourceDir.absolutePath}")
                return 0
            }
            
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            
            val imageExtensions = setOf("jpg", "jpeg", "png", "bmp", "gif", "tiff")
            val imageFiles = sourceDir.listFiles { file ->
                file.isFile && file.extension.lowercase() in imageExtensions
            } ?: emptyArray()
            
            var successCount = 0
            
            imageFiles.forEach { file ->
                try {
                    val image = ImageIO.read(file) ?: return@forEach
                    
                    // 生成输出文件名
                    val baseName = file.nameWithoutExtension
                    val outputFileName = "$baseName.${params.algorithm.format}"
                    val outputFile = File(outputDir, outputFileName)
                    
                    // 压缩并保存
                    val result = compressAndSaveImage(image, outputFile, params)
                    
                    if (result != null) {
                        successCount++
                    }
                } catch (e: Exception) {
                    logger.error("Failed to process image: ${file.absolutePath}", e)
                }
            }
            
            successCount
        } catch (e: Exception) {
            logger.error("Batch compression error", e)
            0
        }
    }
    
    /**
     * 计算压缩效果
     * 
     * @param originalSize 原始大小（字节）
     * @param compressedSize 压缩后大小（字节）
     * @return 压缩率百分比
     */
    fun calculateCompressionRatio(originalSize: Long, compressedSize: Long): Int {
        return if (originalSize > 0) {
            (100 * (1 - compressedSize.toDouble() / originalSize)).toInt()
        } else {
            0
        }
    }
    
    /**
     * 格式化文件大小为可读的字符串
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes <= 0 -> "0 B"
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${String.format("%.2f", bytes / (1024.0 * 1024.0))} MB"
            else -> "${String.format("%.2f", bytes / (1024.0 * 1024.0 * 1024.0))} GB"
        }
    }
}

