package cn.netdiscovery.monica.ui.controlpanel.colorpick.widget

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import cn.netdiscovery.monica.ui.controlpanel.colorpick.OnColorChange
import cn.netdiscovery.monica.ui.controlpanel.colorpick.defaultThumbnailSize
import cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorData
import cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorNameParser
import cn.netdiscovery.monica.ui.controlpanel.colorpick.utils.calculateColorInPixel
import cn.netdiscovery.monica.ui.widget.image.ImageWithThumbnail
import cn.netdiscovery.monica.ui.widget.image.rememberThumbnailState
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ImageColorDetector
 * @author: Tony Shen
 * @date: 2024/6/13 20:13
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun ImageColorDetector(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = ContentScale.FillBounds,
    alignment: Alignment = Alignment.Center,
    colorNameParser: ColorNameParser,
    thumbnailSize: Dp = defaultThumbnailSize,
    thumbnailZoom: Int = 200,
    onColorChange: OnColorChange
) {

    var offset by remember(imageBitmap, contentScale) {
        mutableStateOf(Offset.Unspecified)
    }

    var center by remember(imageBitmap, contentScale) {
        mutableStateOf(Offset.Unspecified)
    }

    var color by remember { mutableStateOf(Color.Unspecified) }

    LaunchedEffect(key1 = colorNameParser) {

        snapshotFlow { color }
            .distinctUntilChanged()
            .mapLatest { color: Color ->
                colorNameParser.parseColorName(color)
            }
            .flowOn(IO)
            .collect { name: String ->
                onColorChange(ColorData(color, name))
            }
    }

    ImageWithThumbnail(
        imageBitmap = imageBitmap,
        modifier = modifier,
        contentDescription = "Image Color Detector",
        contentScale = contentScale,
        alignment = alignment,
        thumbnailState = rememberThumbnailState(
            size = DpSize(thumbnailSize, thumbnailSize),
            thumbnailZoom = thumbnailZoom,
        ),
        onThumbnailCenterChange = {
            center = it
        },
        onDown = {
            offset = it
        },
        onMove = {
            offset = it
        }
    ) {

        val density = LocalDensity.current.density

        if (offset.isSpecified && offset.isFinite) {
            color = calculateColorInPixel(
                offsetX = offset.x,
                offsetY = offset.y,
                startImageX = 0f,
                startImageY = 0f,
                rect = rect,
                width = imageWidth.value * density,
                height = imageHeight.value * density,
                bitmap = imageBitmap.asSkiaBitmap()
            )
        }

        ColorSelectionDrawing(
            modifier = Modifier.size(imageWidth, imageHeight),
            offset = offset,
            thumbnailCenter = center,
            color = color
        )
    }
}