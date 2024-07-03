package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.utils

import androidx.compose.ui.graphics.GraphicsLayerScope
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.state.TransformState

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.ZoomUtils
 * @author: Tony Shen
 * @date: 2024/5/26 15:32
 * @version: V1.0 <描述当前版本功能>
 */
enum class ZoomLevel {
    Min, Mid, Max
}

internal fun getNextZoomLevel(zoomLevel: ZoomLevel): ZoomLevel = when (zoomLevel) {
    ZoomLevel.Mid -> ZoomLevel.Max
    ZoomLevel.Max -> ZoomLevel.Min
    else          -> ZoomLevel.Mid
}

/**
 * Update graphic layer with [transformState]
 */
internal fun GraphicsLayerScope.update(transformState: TransformState) {

    // Set zoom
    val zoom = transformState.zoom
    this.scaleX = zoom
    this.scaleY = zoom

    // Set pan
    val pan = transformState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY

    // Set rotation
    this.rotationZ = transformState.rotation
}