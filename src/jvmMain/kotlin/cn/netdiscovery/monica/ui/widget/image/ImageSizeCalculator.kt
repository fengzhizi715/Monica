package cn.netdiscovery.monica.ui.widget.image

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import androidx.compose.ui.graphics.toComposeImageBitmap

/**
 * 统一的图片尺寸计算工具
 * 确保在不同页面中图片显示大小一致
 * 
 * @author Tony Shen
 * @date 2025/9/4
 * @version V1.0
 */
object ImageSizeCalculator {
    
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
        maxWidthDp: Float = 1200f,
        maxHeightDp: Float = 800f
    ): Pair<androidx.compose.ui.unit.Dp, androidx.compose.ui.unit.Dp> {
        val density = LocalDensity.current
        val image = state.currentImage?.toComposeImageBitmap()
        
        return if (image != null) {
            val bitmapWidth = image.width
            val bitmapHeight = image.height
            
            // 原始图片尺寸（dp）
            val originalWidthDp = bitmapWidth / density.density
            val originalHeightDp = bitmapHeight / density.density
            
            // 计算缩放比例，保持宽高比
            val scale = minOf(
                maxWidthDp / originalWidthDp,
                maxHeightDp / originalHeightDp
            ).coerceAtMost(1f) // 不放大图片，只缩小
            
            val displayWidth = (originalWidthDp * scale).dp
            val displayHeight = (originalHeightDp * scale).dp
            
            displayWidth to displayHeight
        } else {
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
        return image?.let { it.width to it.height }
    }
}
