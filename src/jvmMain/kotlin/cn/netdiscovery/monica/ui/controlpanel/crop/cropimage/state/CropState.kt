package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropProperties
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropType

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.state.CropState
 * @author: Tony Shen
 * @date: 2024/5/26 12:04
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun rememberCropState(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    cropProperties: CropProperties,
    vararg keys: Any?
): CropState {

    // Properties of crop state
    val handleSize = cropProperties.handleSize
    val cropType = cropProperties.cropType
    val aspectRatio = cropProperties.aspectRatio
    val overlayRatio = cropProperties.overlayRatio
    val maxZoom = cropProperties.maxZoom
    val fling = cropProperties.fling
    val zoomable = cropProperties.zoomable
    val pannable = cropProperties.pannable
    val rotatable = cropProperties.rotatable
    val fixedAspectRatio = cropProperties.fixedAspectRatio
    val minDimension = cropProperties.minDimension

    return remember(*keys) {
        when (cropType) {
            CropType.Static -> {
                StaticCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    drawAreaSize = drawAreaSize,
                    aspectRatio = aspectRatio,
                    overlayRatio = overlayRatio,
                    maxZoom = maxZoom,
                    fling = fling,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = false
                )
            }
            else -> {

                DynamicCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    drawAreaSize = drawAreaSize,
                    aspectRatio = aspectRatio,
                    overlayRatio = overlayRatio,
                    maxZoom = maxZoom,
                    handleSize = handleSize,
                    fling = fling,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = true,
                    fixedAspectRatio = fixedAspectRatio,
                    minDimension = minDimension,
                )
            }
        }
    }
}