package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.utils.createRectShape

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.CropAspectRatio
 * @author: Tony Shen
 * @date: 2024/6/10 17:29
 * @version: V1.0 <描述当前版本功能>
 */
val aspectRatios = listOf(
    CropAspectRatio(
        title = "Original",
        shape = createRectShape(AspectRatio.Original),
        aspectRatio = AspectRatio.Original
    ),
    CropAspectRatio(
        title = "9:16",
        shape = createRectShape(AspectRatio(9 / 16f)),
        aspectRatio = AspectRatio(9 / 16f)
    ),
    CropAspectRatio(
        title = "2:3",
        shape = createRectShape(AspectRatio(2 / 3f)),
        aspectRatio = AspectRatio(2 / 3f)
    ),
    CropAspectRatio(
        title = "1:1",
        shape = createRectShape(AspectRatio(1 / 1f)),
        aspectRatio = AspectRatio(1 / 1f)
    ),
    CropAspectRatio(
        title = "16:9",
        shape = createRectShape(AspectRatio(16 / 9f)),
        aspectRatio = AspectRatio(16 / 9f)
    ),
    CropAspectRatio(
        title = "1.91:1",
        shape = createRectShape(AspectRatio(1.91f / 1f)),
        aspectRatio = AspectRatio(1.91f / 1f)
    ),
    CropAspectRatio(
        title = "3:2",
        shape = createRectShape(AspectRatio(3 / 2f)),
        aspectRatio = AspectRatio(3 / 2f)
    ),
    CropAspectRatio(
        title = "3:4",
        shape = createRectShape(AspectRatio(3 / 4f)),
        aspectRatio = AspectRatio(3 / 4f)
    ),
    CropAspectRatio(
        title = "3:5",
        shape = createRectShape(AspectRatio(3 / 5f)),
        aspectRatio = AspectRatio(3 / 5f)
    )
)

@Immutable
data class CropAspectRatio(
    val title: String,
    val shape: Shape,
    val aspectRatio: AspectRatio = AspectRatio.Original,
    val icons: List<Int> = listOf()
)

/**
 * Value class for containing aspect ratio
 * and [AspectRatio.Original] for comparing
 */
@Immutable
data class AspectRatio(val value: Float) {
    companion object {
        val Original = AspectRatio(-1f)
    }
}