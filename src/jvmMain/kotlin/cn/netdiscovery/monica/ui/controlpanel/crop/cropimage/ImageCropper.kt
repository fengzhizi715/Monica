package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import cn.netdiscovery.monica.config.KEY_CROP
import cn.netdiscovery.monica.config.KEY_CROP_FIRST
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.draw.DrawingOverlay
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.draw.ImageDrawCanvas
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.CropOutline
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropDefaults
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropProperties
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropStyle
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropType
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.state.DynamicCropState
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.state.rememberCropState
import cn.netdiscovery.monica.ui.widget.image.ImageWithConstraints
import cn.netdiscovery.monica.ui.widget.image.getScaledImageBitmap
import cn.netdiscovery.monica.utils.Default
import com.safframework.rxcache.domain.CacheStrategy
import com.safframework.rxcache.ext.get
import com.safframework.rxcache.ext.saveMemoryFunc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.ImageCropper
 * @author: Tony Shen
 * @date: 2024/5/26 12:00
 * @version: V1.0 <描述当前版本功能>
 */
val cropFlag:AtomicBoolean = AtomicBoolean(false)

@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentDescription: String?,
    cropStyle: CropStyle = CropDefaults.style(),
    cropProperties: CropProperties,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    crop: Boolean = false,
    backgroundColor: Color = Color.Black,
    onCropStart: () -> Unit,
    onCropSuccess: (ImageBitmap) -> Unit,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)? = null,
) {

    ImageWithConstraints(
        modifier = modifier.clipToBounds(),
        contentScale = cropProperties.contentScale,
        contentDescription = contentDescription,
        filterQuality = filterQuality,
        imageBitmap = imageBitmap,
        drawImage = false
    ) {

        // No crop operation is applied by ScalableImage so rect points to bounds of original
        // bitmap
        val scaledImageBitmap = getScaledImageBitmap(
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            rect = rect,
            bitmap = imageBitmap,
            contentScale = cropProperties.contentScale,
        )

        // Container Dimensions
        val containerWidthPx = constraints.maxWidth
        val containerHeightPx = constraints.maxHeight

        val containerWidth: Dp
        val containerHeight: Dp

        // Bitmap Dimensions
        val bitmapWidth = scaledImageBitmap.width
        val bitmapHeight = scaledImageBitmap.height

        // Dimensions of Composable that displays Bitmap
        val imageWidthPx: Int
        val imageHeightPx: Int

        with(LocalDensity.current) {
            imageWidthPx = imageWidth.roundToPx()
            imageHeightPx = imageHeight.roundToPx()
            containerWidth = containerWidthPx.toDp()
            containerHeight = containerHeightPx.toDp()
        }

        val cropType = cropProperties.cropType
        val contentScale = cropProperties.contentScale
        val fixedAspectRatio = cropProperties.fixedAspectRatio
        val cropOutline = cropProperties.cropOutlineProperty.cropOutline

        // these keys are for resetting cropper when image width/height, contentScale or
        // overlay aspect ratio changes
        val resetKeys =
            getResetKeys(
                scaledImageBitmap,
                imageWidthPx,
                imageHeightPx,
                contentScale,
                cropType,
                fixedAspectRatio
            )

        val cropState = rememberCropState(
            imageSize = IntSize(bitmapWidth, bitmapHeight),
            containerSize = IntSize(containerWidthPx, containerHeightPx),
            drawAreaSize = IntSize(imageWidthPx, imageHeightPx),
            cropProperties = cropProperties,
            keys = resetKeys
        )

        val isHandleTouched by remember(cropState) {
            derivedStateOf {
                cropState is DynamicCropState && handlesTouched(cropState.touchRegion)
            }
        }

        val pressedStateColor = remember(cropStyle.backgroundColor){
            cropStyle.backgroundColor
                .copy(cropStyle.backgroundColor.alpha * .7f)
        }

        val transparentColor by animateColorAsState(
            animationSpec = tween(300, easing = LinearEasing),
            targetValue = if (isHandleTouched) pressedStateColor else cropStyle.backgroundColor
        )

        if (!cropFlag.get()) {
            rxCache.saveMemoryFunc(KEY_CROP_FIRST) {
                cropFlag.set(true)
                cropState.cropRect
            }
        }

        val cachedRect = rxCache.get<Rect>(KEY_CROP_FIRST, CacheStrategy.MEMORY)?.data

        if (cachedRect?.left!=cropState.cropRect.left
            && cachedRect?.top!=cropState.cropRect.top
            && cachedRect?.right!=cropState.cropRect.right
            && cachedRect?.bottom!=cropState.cropRect.bottom) {
            rxCache.saveMemory(KEY_CROP, cropState.cropRect)
        }

        // Crops image when user invokes crop operation
        Crop(
            crop,
            scaledImageBitmap,
            cropState.cropRect,
            cropOutline,
            onCropStart,
            onCropSuccess,
            cropProperties.requiredSize
        )

        val imageModifier = Modifier
            .size(containerWidth, containerHeight)
            .crop(
                keys = resetKeys,
                cropState = cropState
            )

        LaunchedEffect(key1 = cropProperties) {
            cropState.updateProperties(cropProperties)
        }

        /// Create a MutableTransitionState<Boolean> for the AnimatedVisibility.
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(100)
            visible = true
        }

        ImageCropper(
            modifier = imageModifier,
            visible = visible,
            imageBitmap = imageBitmap,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            imageWidthPx = imageWidthPx,
            imageHeightPx = imageHeightPx,
            handleSize = cropProperties.handleSize,
            overlayRect = cropState.overlayRect,
            cropType = cropType,
            cropOutline = cropOutline,
            cropStyle = cropStyle,
            transparentColor = transparentColor,
            backgroundColor = backgroundColor,
            onDrawGrid = onDrawGrid,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ImageCropper(
    modifier: Modifier,
    visible: Boolean,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    handleSize: Float,
    cropType: CropType,
    cropOutline: CropOutline,
    cropStyle: CropStyle,
    overlayRect: Rect,
    transparentColor: Color,
    backgroundColor: Color,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(tween(500))
        ) {

            ImageCropperImpl(
                modifier = modifier,
                imageBitmap = imageBitmap,
                containerWidth = containerWidth,
                containerHeight = containerHeight,
                imageWidthPx = imageWidthPx,
                imageHeightPx = imageHeightPx,
                cropType = cropType,
                cropOutline = cropOutline,
                handleSize = handleSize,
                cropStyle = cropStyle,
                rectOverlay = overlayRect,
                transparentColor = transparentColor,
                onDrawGrid = onDrawGrid,
            )
        }
    }
}

@Composable
private fun ImageCropperImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    cropType: CropType,
    cropOutline: CropOutline,
    handleSize: Float,
    cropStyle: CropStyle,
    transparentColor: Color,
    rectOverlay: Rect,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {

    Box(contentAlignment = Alignment.Center) {

        // Draw Image
        ImageDrawCanvas(
            modifier = modifier,
            imageBitmap = imageBitmap,
            imageWidth = imageWidthPx,
            imageHeight = imageHeightPx
        )

        val drawOverlay = cropStyle.drawOverlay

        val drawGrid = cropStyle.drawGrid
        val overlayColor = cropStyle.overlayColor
        val handleColor = cropStyle.handleColor
        val drawHandles = cropType == CropType.Dynamic
        val strokeWidth = cropStyle.strokeWidth

        DrawingOverlay(
            modifier = Modifier.size(containerWidth, containerHeight),
            drawOverlay = drawOverlay,
            rect = rectOverlay,
            cropOutline = cropOutline,
            drawGrid = drawGrid,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            drawHandles = drawHandles,
            handleSize = handleSize,
            transparentColor = transparentColor,
            onDrawGrid = onDrawGrid,
        )
    }
}

@Composable
private fun Crop(
    crop: Boolean,
    scaledImageBitmap: ImageBitmap,
    rect: Rect,
    cropOutline: CropOutline,
    onCropStart: () -> Unit,
    onCropSuccess: (ImageBitmap) -> Unit,
    requiredSize: IntSize?,
) {
    val cropRect = rxCache.get<Rect>(KEY_CROP, CacheStrategy.MEMORY)?.data ?: rect
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // Crop Agent is responsible for cropping image
    val cropAgent = remember { CropAgent() }

    LaunchedEffect(crop) {
        if (crop) {
            flow {
                val croppedImageBitmap = cropAgent.crop(
                    scaledImageBitmap,
                    cropRect,
                    cropOutline,
                    layoutDirection,
                    density
                )
                if (requiredSize != null) {
                    emit(
                        cropAgent.resize(
                            croppedImageBitmap,
                            requiredSize.width,
                            requiredSize.height,
                        )
                    )
                } else {
                    emit(croppedImageBitmap)
                }
            }
                .flowOn(Default)
                .onStart {
                    onCropStart()
                    delay(400)
                }
                .onEach {
                    onCropSuccess(it)
                }
                .launchIn(this)
        }
    }
}

@Composable
private fun getResetKeys(
    scaledImageBitmap: ImageBitmap,
    imageWidthPx: Int,
    imageHeightPx: Int,
    contentScale: ContentScale,
    cropType: CropType,
    fixedAspectRatio: Boolean,
) = remember(
    scaledImageBitmap,
    imageWidthPx,
    imageHeightPx,
    contentScale,
    cropType,
    fixedAspectRatio,
) {
    arrayOf(
        scaledImageBitmap,
        imageWidthPx,
        imageHeightPx,
        contentScale,
        cropType,
        fixedAspectRatio,
    )
}