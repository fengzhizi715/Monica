package cn.netdiscovery.monica.editor.layer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform

/**
 * 图像图层，负责持有位图及其变换信息。
 */
class ImageLayer(
    name: String,
    image: ImageBitmap? = null,
    transform: LayerTransform = LayerTransform()
) : Layer(
    type = LayerType.IMAGE,
    name = name
) {

    var image by mutableStateOf(image)
        private set

    var transform by mutableStateOf(transform)
        private set

    fun updateImage(newImage: ImageBitmap?) {
        image = newImage
    }

    fun updateTransform(newTransform: LayerTransform) {
        transform = newTransform
    }

    override fun render(drawScope: DrawScope) {
        val bitmap = image ?: return

        // 获取画布尺寸
        val canvasWidth = drawScope.size.width
        val canvasHeight = drawScope.size.height
        
        // 判断是否为背景图层（图像尺寸与画布尺寸匹配，误差在1像素内）
        val isBackgroundLayer = kotlin.math.abs(bitmap.width - canvasWidth) <= 1f && 
                                kotlin.math.abs(bitmap.height - canvasHeight) <= 1f
        
        if (isBackgroundLayer) {
            // 背景图层：直接填充画布，不进行缩放和居中
            drawScope.withTransform({
                // 应用用户定义的变换（如果有）
                if (transform.translation != Offset.Zero) {
                    translate(transform.translation.x, transform.translation.y)
                }
                if (transform.rotation != 0f) {
                    rotate(transform.rotation, transform.pivot)
                }
                if (transform.scaleX != 1f || transform.scaleY != 1f) {
                    scale(transform.scaleX, transform.scaleY, transform.pivot)
                }
            }) {
                drawImage(bitmap, alpha = opacity)
            }
        } else {
            // 用户添加的图像层：适应画布并居中显示
            // 计算图像缩放比例，保持宽高比，适应画布（不放大，只缩小）
            val scaleX = canvasWidth / bitmap.width
            val scaleY = canvasHeight / bitmap.height
            val fitScale = minOf(scaleX, scaleY).coerceAtMost(1f)
            
            // 计算缩放后的图像尺寸
            val scaledWidth = bitmap.width * fitScale
            val scaledHeight = bitmap.height * fitScale
            
            // 计算居中位置
            val centerOffsetX = (canvasWidth - scaledWidth) / 2f
            val centerOffsetY = (canvasHeight - scaledHeight) / 2f

            drawScope.withTransform({
                // 先应用用户定义的变换（如果有）
                if (transform.translation != Offset.Zero) {
                    translate(transform.translation.x, transform.translation.y)
                }
                if (transform.rotation != 0f) {
                    rotate(transform.rotation, transform.pivot)
                }
                if (transform.scaleX != 1f || transform.scaleY != 1f) {
                    scale(transform.scaleX, transform.scaleY, transform.pivot)
                }
                // 然后应用自动适应和居中
                translate(centerOffsetX, centerOffsetY)
                scale(fitScale, fitScale)
            }) {
                drawImage(bitmap, alpha = opacity)
            }
        }
    }
}

/**
 * 图层变换信息。
 */
data class LayerTransform(
    val translation: Offset = Offset.Zero,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f,
    val pivot: Offset = Offset.Zero
)

