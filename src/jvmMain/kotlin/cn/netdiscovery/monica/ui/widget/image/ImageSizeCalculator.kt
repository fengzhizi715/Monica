package cn.netdiscovery.monica.ui.widget.image

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 统一的图片尺寸计算工具
 * 确保在不同页面中图片显示大小一致
 * 
 * @author Tony Shen
 * @date 2025/9/4
 * @version V1.0
 */
object ImageSizeCalculator {
    
    private val logger: Logger = LoggerFactory.getLogger(ImageSizeCalculator::class.java)
    
    // 默认最大尺寸配置 - 增加尺寸以支持更大的图片
    private const val DEFAULT_MAX_WIDTH_DP = 1600f  // 从1200增加到1600
    private const val DEFAULT_MAX_HEIGHT_DP = 1200f // 从800增加到1200
    private const val MIN_DENSITY = 0.1f // 防止除零错误
    
    /**
     * 计算统一的图片显示尺寸
     * @param state 应用状态
     * @param maxWidthDp 最大宽度（dp）
     * @param maxHeightDp 最大高度（dp）
     * @return 显示尺寸对
     */
    @androidx.compose.runtime.Composable
    fun calculateImageSize(
        state: ApplicationState,
        maxWidthDp: Float = DEFAULT_MAX_WIDTH_DP,
        maxHeightDp: Float = DEFAULT_MAX_HEIGHT_DP
    ): Pair<androidx.compose.ui.unit.Dp, androidx.compose.ui.unit.Dp> {
        val density = LocalDensity.current
        
        // 安全检查密度值
        val safeDensity = if (density.density < MIN_DENSITY) {
            logger.warn("检测到异常密度值: ${density.density}, 使用默认值 1.0")
            1.0f
        } else {
            density.density
        }
        
        val image = state.currentImage?.toComposeImageBitmap()
        
        return if (image != null && image.width > 0 && image.height > 0) {
            val bitmapWidth = image.width
            val bitmapHeight = image.height
            
            // 原始图片尺寸（dp）
            val originalWidthDp = bitmapWidth / safeDensity
            val originalHeightDp = bitmapHeight / safeDensity
            
            // 计算缩放比例，保持宽高比
            val scale = minOf(
                maxWidthDp / originalWidthDp,
                maxHeightDp / originalHeightDp
            ).coerceAtMost(1f) // 不放大图片，只缩小
            
            val displayWidth = (originalWidthDp * scale).dp
            val displayHeight = (originalHeightDp * scale).dp
            
            logger.debug("图片尺寸计算: 原始(${bitmapWidth}x${bitmapHeight}) -> 显示(${displayWidth.value}dp x ${displayHeight.value}dp)")
            
            displayWidth to displayHeight
        } else {
            logger.warn("图片为空或尺寸无效，返回默认尺寸")
            0.dp to 0.dp
        }
    }
    
    /**
     * 获取图片的像素尺寸
     * @param state 应用状态
     * @return 图片的宽度和高度（像素）
     */
    fun getImagePixelSize(state: ApplicationState): Pair<Int, Int>? {
        val image = state.currentImage?.toComposeImageBitmap()
        return if (image != null && image.width > 0 && image.height > 0) {
            image.width to image.height
        } else {
            logger.warn("无法获取有效的图片像素尺寸")
            null
        }
    }
    
    /**
     * 获取图片的显示尺寸（像素）- 非Composable版本
     * @param state 应用状态
     * @param density 密度值
     * @return 图片的显示宽度和高度（像素）
     */
    fun getImageDisplayPixelSize(state: ApplicationState, density: Float): Pair<Int, Int>? {
        val image = state.currentImage?.toComposeImageBitmap()
        
        return if (image != null && image.width > 0 && image.height > 0) {
            val bitmapWidth = image.width
            val bitmapHeight = image.height
            
            // 原始图片尺寸（dp）
            val originalWidthDp = bitmapWidth / density
            val originalHeightDp = bitmapHeight / density
            
            // 计算缩放比例，保持宽高比
            val scale = minOf(
                DEFAULT_MAX_WIDTH_DP / originalWidthDp,
                DEFAULT_MAX_HEIGHT_DP / originalHeightDp
            ).coerceAtMost(1f) // 不放大图片，只缩小
            
            val displayWidthPx = (originalWidthDp * scale * density).toInt()
            val displayHeightPx = (originalHeightDp * scale * density).toInt()
            
            displayWidthPx to displayHeightPx
        } else {
            null
        }
    }
    
    /**
     * 获取图片的显示尺寸（像素）
     * @param state 应用状态
     * @return 图片的显示宽度和高度（像素）
     */
    @androidx.compose.runtime.Composable
    fun getImageDisplayPixelSize(state: ApplicationState): Pair<Int, Int>? {
        val density = LocalDensity.current.density
        return getImageDisplayPixelSize(state, density)
    }
}
