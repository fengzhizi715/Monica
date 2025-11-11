package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

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
        
        // 安全检查：防止除零错误
        if (bitmap.width <= 0 || bitmap.height <= 0 || canvasWidth <= 0 || canvasHeight <= 0) {
            return
        }
        
        // 判断是否为背景图层（通过图层名称判断，更可靠）
        val isBackgroundLayer = name == "背景图层"
        
        // 统一背景层和图像层的渲染逻辑：适应画布并居中显示
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
            if (isBackgroundLayer) {
                // 背景图层：只应用自动适应和居中，不应用用户定义的变换
                translate(centerOffsetX, centerOffsetY)
                scale(fitScale, fitScale)
            } else {
                // 用户添加的图像层：先应用自动适应和居中，再应用用户定义的变换
                translate(centerOffsetX, centerOffsetY)
                scale(fitScale, fitScale)
                // 然后应用用户定义的变换（如果有）
                if (transform.translation != Offset.Zero) {
                    translate(transform.translation.x, transform.translation.y)
                }
                if (transform.rotation != 0f) {
                    rotate(transform.rotation, transform.pivot)
                }
                if (transform.scaleX != 1f || transform.scaleY != 1f) {
                    scale(transform.scaleX, transform.scaleY, transform.pivot)
                }
            }
        }) {
            drawImage(bitmap, alpha = opacity)
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

