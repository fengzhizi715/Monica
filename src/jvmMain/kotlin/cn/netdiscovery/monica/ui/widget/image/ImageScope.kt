package cn.netdiscovery.monica.ui.widget.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import cn.netdiscovery.monica.imageprocess.utils.extension.subImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.image.ImageScope
 * @author: Tony Shen
 * @date: 2024/5/14 15:25
 * @version: V1.0 <描述当前版本功能>
 */
@Stable
interface ImageScope {
    /**
     * The constraints given by the parent layout in pixels.
     *
     * Use [minWidth], [maxWidth], [minHeight] or [maxHeight] if you need value in [Dp].
     */
    val constraints: Constraints

    /**
     * The minimum width in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val minWidth: Dp

    /**
     * The maximum width in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val maxWidth: Dp

    /**
     * The minimum height in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val minHeight: Dp

    /**
     * The maximum height in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val maxHeight: Dp

    /**
     * Width of area inside BoxWithConstraints that is scaled based on [ContentScale]
     * This is width of the [Canvas] draw [ImageBitmap]
     */
    val imageWidth: Dp

    /**
     * Height of area inside BoxWithConstraints that is scaled based on [ContentScale]
     * This is height of the [Canvas] draw [ImageBitmap]
     */
    val imageHeight: Dp

    /**
     * [IntRect] that covers boundaries of [ImageBitmap]
     */
    val rect: IntRect
}

internal data class ImageScopeImpl(
    private val density: Density,
    override val constraints: Constraints,
    override val imageWidth: Dp,
    override val imageHeight: Dp,
    override val rect: IntRect,
) : ImageScope {

    override val minWidth: Dp get() = with(density) { constraints.minWidth.toDp() }

    override val maxWidth: Dp
        get() = with(density) {
            if (constraints.hasBoundedWidth) constraints.maxWidth.toDp() else Dp.Infinity
        }

    override val minHeight: Dp get() = with(density) { constraints.minHeight.toDp() }

    override val maxHeight: Dp
        get() = with(density) {
            if (constraints.hasBoundedHeight) constraints.maxHeight.toDp() else Dp.Infinity
        }
}

@Composable
internal fun getScaledImageBitmap(
    imageWidth: Dp,
    imageHeight: Dp,
    rect: IntRect,
    bitmap: ImageBitmap,
    contentScale: ContentScale
): ImageBitmap {

    val scaledBitmap =
        remember(bitmap, rect, imageWidth, imageHeight, contentScale) {
            bitmap.toAwtImage().subImage(rect.left,rect.top,rect.width,rect.height).toComposeImageBitmap()
        }
    return scaledBitmap
}

@Composable
internal fun ImageScope.getScaledImageBitmap(
    bitmap: ImageBitmap,
    contentScale: ContentScale
): ImageBitmap {

    val scaledBitmap =
        remember(bitmap, rect, imageWidth, imageHeight, contentScale) {
            bitmap.toAwtImage().subImage(rect.left,rect.top,rect.width,rect.height).toComposeImageBitmap()
        }
    return scaledBitmap
}