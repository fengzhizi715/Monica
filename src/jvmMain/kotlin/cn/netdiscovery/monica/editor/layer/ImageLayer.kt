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

        drawScope.withTransform({
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

