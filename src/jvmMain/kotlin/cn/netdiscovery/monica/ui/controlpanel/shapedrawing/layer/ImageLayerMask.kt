package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import androidx.compose.ui.geometry.Rect

enum class ImageLayerMaskShape {
    RECTANGLE,
    ELLIPSE
}

data class ImageLayerMask(
    val rect: Rect,
    val shape: ImageLayerMaskShape = ImageLayerMaskShape.RECTANGLE
)
