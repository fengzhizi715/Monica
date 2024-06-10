package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.AspectRatio
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.CropOutline
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.OutlineType
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropDefaults
 * @author: Tony Shen
 * @date: 2024/5/26 12:20
 * @version: V1.0 <描述当前版本功能>
 */
enum class CropType {
    Static, Dynamic
}

object CropDefaults {

    val DefaultBackgroundColor = Color(0x99000000)
    val DefaultOverlayColor = Color.Gray
    val DefaultHandleColor = Color.White

    /**
     * Properties effect crop behavior that should be passed to [CropState]
     */
    fun properties(
        cropType: CropType = CropType.Dynamic,
        handleSize: Float,
        maxZoom: Float = 10f,
        contentScale: ContentScale = ContentScale.Fit,
        cropOutlineProperty: CropOutlineProperty,
        aspectRatio: AspectRatio = aspectRatios[2].aspectRatio,
        overlayRatio: Float = .9f,
        pannable: Boolean = true,
        fling: Boolean = false,
        zoomable: Boolean = true,
        rotatable: Boolean = false,
        fixedAspectRatio: Boolean = false,
        requiredSize: IntSize? = null,
        minDimension: IntSize? = null,
    ): CropProperties {
        return CropProperties(
            cropType = cropType,
            handleSize = handleSize,
            contentScale = contentScale,
            cropOutlineProperty = cropOutlineProperty,
            maxZoom = maxZoom,
            aspectRatio = aspectRatio,
            overlayRatio = overlayRatio,
            pannable = pannable,
            fling = fling,
            zoomable = zoomable,
            rotatable = rotatable,
            fixedAspectRatio = fixedAspectRatio,
            requiredSize = requiredSize,
            minDimension = minDimension,
        )
    }

    /**
     * Style is cosmetic changes that don't effect how [CropState] behaves because of that
     * none of these properties are passed to [CropState]
     */
    fun style(
        drawOverlay: Boolean = true,
        drawGrid: Boolean = true,
        strokeWidth: Dp = 1.dp,
        overlayColor: Color = DefaultOverlayColor,
        handleColor: Color = DefaultHandleColor,
        backgroundColor: Color = DefaultBackgroundColor
    ): CropStyle {
        return CropStyle(
            drawOverlay = drawOverlay,
            drawGrid = drawGrid,
            strokeWidth = strokeWidth,
            overlayColor = overlayColor,
            handleColor = handleColor,
            backgroundColor = backgroundColor
        )
    }
}

/**
 * Data class for selecting cropper properties. Fields of this class control inner work
 * of [CropState] while some such as [cropType], [aspectRatio], [handleSize]
 * is shared between ui and state.
 */
@Immutable
data class CropProperties internal constructor(
    val cropType: CropType,
    val handleSize: Float,
    val contentScale: ContentScale,
    val cropOutlineProperty: CropOutlineProperty,
    val aspectRatio: AspectRatio,
    val overlayRatio: Float,
    val pannable: Boolean,
    val fling: Boolean,
    val rotatable: Boolean,
    val zoomable: Boolean,
    val maxZoom: Float,
    val fixedAspectRatio: Boolean = false,
    val requiredSize: IntSize? = null,
    val minDimension: IntSize? = null,
)

/**
 * Data class for cropper styling only. None of the properties of this class is used
 * by [CropState] or [Modifier.crop]
 */
@Immutable
data class CropStyle internal constructor(
    val drawOverlay: Boolean,
    val drawGrid: Boolean,
    val strokeWidth: Dp,
    val overlayColor: Color,
    val handleColor: Color,
    val backgroundColor: Color,
    val cropTheme: CropTheme = CropTheme.Dark
)

/**
 * Property for passing [CropOutline] between settings UI to [ImageCropper]
 */
@Immutable
data class CropOutlineProperty(
    val outlineType: OutlineType,
    val cropOutline: CropOutline
)

/**
 * Light, Dark or system controlled theme
 */
enum class CropTheme{
    Light,
    Dark,
    System
}