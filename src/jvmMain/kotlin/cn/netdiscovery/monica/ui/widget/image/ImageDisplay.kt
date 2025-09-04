package cn.netdiscovery.monica.ui.widget.image

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState

/**
 * 统一的图片显示组件
 * 确保在不同页面中图片显示大小一致
 * 
 * @author Tony Shen
 * @date 2025/9/4
 * @version V1.0
 */
@Composable
fun ImageDisplay(
    state: ApplicationState,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    maxWidthRatio: Float = 0.8f, // 最大宽度占屏幕的80%
    maxHeightRatio: Float = 0.8f, // 最大高度占屏幕的80%
    maintainAspectRatio: Boolean = true,
    onImageSizeCalculated: ((width: Int, height: Int) -> Unit)? = null
) {
    val density = LocalDensity.current
    
    val image = state.currentImage?.toComposeImageBitmap()
    
    if (image != null) {
        val bitmapWidth = image.width
        val bitmapHeight = image.height
        
        // 计算显示尺寸
        val displaySize = remember(bitmapWidth, bitmapHeight, density.density) {
            calculateDisplaySize(
                bitmapWidth = bitmapWidth,
                bitmapHeight = bitmapHeight,
                density = density.density,
                maxWidthRatio = maxWidthRatio,
                maxHeightRatio = maxHeightRatio,
                maintainAspectRatio = maintainAspectRatio
            )
        }
        
        // 回调图片尺寸信息
        LaunchedEffect(displaySize) {
            onImageSizeCalculated?.invoke(displaySize.width, displaySize.height)
        }
        
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                bitmap = image,
                contentDescription = "Display Image",
                modifier = Modifier
                    .width(displaySize.width.dp)
                    .height(displaySize.height.dp),
                contentScale = contentScale
            )
        }
    }
}

/**
 * 计算图片显示尺寸
 */
private fun calculateDisplaySize(
    bitmapWidth: Int,
    bitmapHeight: Int,
    density: Float,
    maxWidthRatio: Float,
    maxHeightRatio: Float,
    maintainAspectRatio: Boolean
): DisplaySize {
    // 原始图片尺寸（dp）
    val originalWidthDp = bitmapWidth / density
    val originalHeightDp = bitmapHeight / density
    
    return if (maintainAspectRatio) {
        // 保持宽高比，使用固定的最大尺寸
        val maxWidthDp = 1200f // 最大宽度1200dp
        val maxHeightDp = 800f // 最大高度800dp
        
        val scale = minOf(
            maxWidthDp / originalWidthDp,
            maxHeightDp / originalHeightDp
        ).coerceAtMost(1f) // 不放大图片，只缩小
        
        DisplaySize(
            width = (originalWidthDp * scale).toInt(),
            height = (originalHeightDp * scale).toInt()
        )
    } else {
        // 不保持宽高比，直接使用最大尺寸
        DisplaySize(
            width = (maxWidthRatio * 1200).toInt(),
            height = (maxHeightRatio * 800).toInt()
        )
    }
}

/**
 * 显示尺寸数据类
 */
data class DisplaySize(
    val width: Int,
    val height: Int
)

/**
 * 获取图片显示尺寸的扩展函数
 */
@Composable
fun ApplicationState.getImageDisplaySize(
    maxWidthRatio: Float = 0.8f,
    maxHeightRatio: Float = 0.8f
): DisplaySize? {
    val density = LocalDensity.current
    val image = currentImage?.toComposeImageBitmap()
    
    return image?.let {
        calculateDisplaySize(
            bitmapWidth = it.width,
            bitmapHeight = it.height,
            density = density.density,
            maxWidthRatio = maxWidthRatio,
            maxHeightRatio = maxHeightRatio,
            maintainAspectRatio = true
        )
    }
}
