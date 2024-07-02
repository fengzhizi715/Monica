package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.CropOutlineContainer
 * @author: Tony Shen
 * @date: 2024/5/26 15:24
 * @version: V1.0 <描述当前版本功能>
 */
interface CropOutlineContainer<O : CropOutline> {
    var selectedIndex: Int
    val outlines: List<O>
    val selectedItem: O
        get() = outlines[selectedIndex]
    val size: Int
        get() = outlines.size
}

/**
 * Container for [RectCropShape]
 */
data class RectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RectCropShape>
) : CropOutlineContainer<RectCropShape>

/**
 * Container for [RoundedCornerCropShape]s
 */
data class RoundedRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RoundedCornerCropShape>
) : CropOutlineContainer<RoundedCornerCropShape>

/**
 * Container for [CutCornerCropShape]s
 */
data class CutCornerRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CutCornerCropShape>
) : CropOutlineContainer<CutCornerCropShape>

/**
 * Container for [OvalCropShape]s
 */
data class OvalOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<OvalCropShape>
) : CropOutlineContainer<OvalCropShape>


/**
 * Container for [PolygonCropShape]s
 */
data class PolygonOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<PolygonCropShape>
) : CropOutlineContainer<PolygonCropShape>

/**
 * Container for [ParallelogramShape]s
 */
data class ParallelogramOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<ParallelogramShape>
) : CropOutlineContainer<ParallelogramShape>


/**
 * Container for [DiamondShape]s
 */
data class DiamondOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<DiamondShape>
) : CropOutlineContainer<DiamondShape>


/**
 * Container for [TicketShape]s
 */
data class TicketOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<TicketShape>
) : CropOutlineContainer<TicketShape>

/**
 * Container for [CustomPathOutline]s
 */
data class CustomOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CustomPathOutline>
) : CropOutlineContainer<CustomPathOutline>

/**
 * Container for [ImageMaskOutline]s
 */
data class ImageMaskOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<ImageMaskOutline>
) : CropOutlineContainer<ImageMaskOutline>