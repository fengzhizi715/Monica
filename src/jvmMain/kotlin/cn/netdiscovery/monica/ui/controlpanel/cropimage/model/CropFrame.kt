package cn.netdiscovery.monica.ui.controlpanel.cropimage.model

import androidx.compose.runtime.Immutable

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropFrame
 * @author: Tony Shen
 * @date: 2024/5/26 15:23
 * @version: V1.0 <描述当前版本功能>
 */
@Immutable
data class CropFrame(
    val outlineType: OutlineType,
    val editable: Boolean = false,
    val cropOutlineContainer: CropOutlineContainer<out CropOutline>
) {
    var selectedIndex: Int
        get() = cropOutlineContainer.selectedIndex
        set(value) {
            cropOutlineContainer.selectedIndex = value
        }

    val outlines: List<CropOutline>
        get() = cropOutlineContainer.outlines

    val outlineCount: Int
        get() = cropOutlineContainer.size

    fun addOutline(outline: CropOutline): CropFrame {
        outlines.toMutableList().add(outline)
        return this
    }
}

@Suppress("UNCHECKED_CAST")
fun getOutlineContainer(
    outlineType: OutlineType,
    index: Int,
    outlines: List<CropOutline>
): CropOutlineContainer<out CropOutline> {
    return when (outlineType) {
        OutlineType.RoundedRect -> {
            RoundedRectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<RoundedCornerCropShape>
            )
        }
        OutlineType.CutCorner -> {
            CutCornerRectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<CutCornerCropShape>
            )
        }

        OutlineType.Oval -> {
            OvalOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<OvalCropShape>
            )
        }

        OutlineType.Polygon -> {
            PolygonOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<PolygonCropShape>
            )
        }

        OutlineType.Diamond -> {
            DiamondOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<DiamondShape>
            )
        }

        OutlineType.Ticket -> {
            TicketOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<TicketShape>
            )
        }

        OutlineType.Custom -> {
            CustomOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<CustomPathOutline>
            )
        }

        OutlineType.ImageMask -> {
            ImageMaskOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<ImageMaskOutline>
            )
        }
        else -> {
            RectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<RectCropShape>
            )
        }
    }
}


enum class OutlineType {
    Rect, RoundedRect, CutCorner, Oval, Polygon, Parallelogram, Diamond, Ticket, Custom, ImageMask
}
