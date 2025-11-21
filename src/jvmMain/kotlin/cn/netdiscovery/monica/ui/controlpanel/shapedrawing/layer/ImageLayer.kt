package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntSize
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController

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
        if (image != newImage) {
            image = newImage
            markDirty()
        }
    }

    fun updateTransform(newTransform: LayerTransform) {
        if (transform != newTransform) {
            transform = newTransform
            markDirty()
        }
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
        
        // 判断是否为背景图层（使用常量，避免硬编码）
        val isBackgroundLayer = name == EditorController.BACKGROUND_LAYER_NAME
        
        if (isBackgroundLayer) {
            // 背景图层：填充整个画布绘制区域，不保持宽高比（与涂鸦模块一致）
            drawScope.drawImage(
                bitmap,
                dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt()),
                alpha = opacity
            )
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
            
            // 适应后图像的中心点（在画布坐标系中）
            val adaptedImageCenter = Offset(
                centerOffsetX + scaledWidth / 2f,
                centerOffsetY + scaledHeight / 2f
            )
            
            // 计算图像中心点（在图像坐标系中）
            val imageCenter = Offset(bitmap.width / 2f, bitmap.height / 2f)
            
            // 计算 pivot（在图像坐标系中）
            val pivot = if (transform.pivot == Offset.Zero) {
                imageCenter
            } else {
                imageCenter + transform.pivot
            }
            
            // translation 存储的是相对于图像中心的偏移（在图像原始坐标系中）
            // 在 withTransform 中，后写的变换先执行，所以实际执行顺序是：
            // 1. 用户缩放（相对于 pivot，在图像原始坐标系中）
            // 2. 用户旋转（相对于 pivot，在图像原始坐标系中）
            // 3. 用户平移（translation，在图像原始坐标系中）
            // 4. 自动缩放（fitScale）- 将图像坐标系转换到适应后的坐标系
            // 5. 自动平移（centerOffset）- 在画布坐标系中
            val translation = transform.translation

            drawScope.withTransform({
                // 变换顺序（从外到内，withTransform 的执行顺序）：
                // 注意：withTransform 中后写的变换先执行，所以应该先写自动适应，再写用户变换
                
                // 1. 自动平移（centerOffset）- 最外层，最后执行
                translate(centerOffsetX, centerOffsetY)
                
                // 2. 自动缩放（fitScale）- 将图像坐标系转换到适应后的坐标系
                scale(fitScale, fitScale)
                
                // 3. 用户平移（在图像原始坐标系中，相对于图像中心）
                if (translation != Offset.Zero) {
                    translate(translation.x, translation.y)
                }
                
                // 4. 用户旋转（相对于 pivot，在图像原始坐标系中）
                if (transform.rotation != 0f) {
                    translate(pivot.x, pivot.y)
                    rotate(transform.rotation)
                    translate(-pivot.x, -pivot.y)
                }
                
                // 5. 用户缩放（相对于 pivot，在图像原始坐标系中）- 最内层，最先执行
                if (transform.scaleX != 1f || transform.scaleY != 1f) {
                    translate(pivot.x, pivot.y)
                    scale(transform.scaleX, transform.scaleY)
                    translate(-pivot.x, -pivot.y)
                }
            }) {
                // 应用裁剪区域（如果存在）
                val cropRect = transform.cropRect
                if (cropRect != null) {
                    // 裁剪区域是在图像坐标系中定义的
                    // 由于 withTransform 已经应用了 fitScale，裁剪区域也在当前坐标系中
                    // 使用 clipPath 来裁剪
                    val clipPath = Path().apply {
                        addRect(cropRect)
                    }
                    drawScope.clipPath(clipPath) {
                        drawScope.drawImage(bitmap, alpha = opacity)
                    }
                } else {
                    drawScope.drawImage(bitmap, alpha = opacity)
                }
            }
        }
    }
}

/**
 * 图层变换信息。
 * 
 * @param translation 相对于图像中心的偏移（在图像原始坐标系中）
 * @param scaleX X轴缩放比例
 * @param scaleY Y轴缩放比例
 * @param rotation 旋转角度（度）
 * @param pivot 旋转和缩放的枢轴点（在图像坐标系中，相对于图像中心）
 * @param cropRect 裁剪区域（在图像坐标系中，null表示不裁剪）
 */
data class LayerTransform(
    val translation: Offset = Offset.Zero,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f,
    val pivot: Offset = Offset.Zero,
    val cropRect: Rect? = null
)

