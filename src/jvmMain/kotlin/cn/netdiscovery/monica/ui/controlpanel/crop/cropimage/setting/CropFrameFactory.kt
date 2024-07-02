package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.*
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.utils.createPolygonShape

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropFrameFactory
 * @author: Tony Shen
 * @date: 2024/5/28 18:27
 * @version: V1.0 <描述当前版本功能>
 */
class CropFrameFactory(private val defaultImages: List<ImageBitmap>) {

    private val cropFrames = mutableStateListOf<CropFrame>()

    fun getCropFrames(): List<CropFrame> {
        if (cropFrames.isEmpty()) {
            val temp = mutableListOf<CropFrame>()
            OutlineType.values().forEach {
                temp.add(getCropFrame(it))
            }
            cropFrames.addAll(temp)
        }
        return cropFrames
    }

    fun getCropFrame(outlineType: OutlineType): CropFrame {
        return cropFrames
            .firstOrNull { it.outlineType == outlineType } ?: createDefaultFrame(outlineType)
    }

    private fun createDefaultFrame(outlineType: OutlineType): CropFrame {
        return when (outlineType) {
            OutlineType.Rect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = false,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.RoundedRect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.CutCorner -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Oval -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Polygon -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Parallelogram -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Diamond -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Ticket -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Custom -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.ImageMask -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }
        }
    }

    private fun createCropOutlineContainer(
        outlineType: OutlineType
    ): CropOutlineContainer<out CropOutline> {
        return when (outlineType) {
            OutlineType.Rect -> {
                RectOutlineContainer(
                    outlines = listOf(RectCropShape(id = 0, title = "Rect"))
                )
            }

            OutlineType.RoundedRect -> {
                RoundedRectOutlineContainer(
                    outlines = listOf(RoundedCornerCropShape(id = 0, title = "Rounded"))
                )
            }

            OutlineType.CutCorner -> {
                CutCornerRectOutlineContainer(
                    outlines = listOf(CutCornerCropShape(id = 0, title = "CutCorner"))
                )
            }

            OutlineType.Oval -> {
                OvalOutlineContainer(
                    outlines = listOf(OvalCropShape(id = 0, title = "Oval"))
                )
            }

            OutlineType.Polygon -> {
                PolygonOutlineContainer(
                    outlines = listOf(
                        PolygonCropShape(
                            id = 0,
                            title = "Polygon"
                        ),
                        PolygonCropShape(
                            id = 1,
                            title = "Triangle",
                            polygonProperties = PolygonProperties(sides = 3, 0f),
                            shape = createPolygonShape(3, 30f)
                        ),
                        PolygonCropShape(
                            id = 2,
                            title = "Pentagon",
                            polygonProperties = PolygonProperties(sides = 5, 0f),
                            shape = createPolygonShape(5, 0f)
                        ),
                        PolygonCropShape(
                            id = 3,
                            title = "Heptagon",
                            polygonProperties = PolygonProperties(sides = 7, 0f),
                            shape = createPolygonShape(7, 0f)
                        ),
                        PolygonCropShape(
                            id = 4,
                            title = "Octagon",
                            polygonProperties = PolygonProperties(sides = 8, 0f),
                            shape = createPolygonShape(8, 0f)
                        )
                    )
                )
            }

            OutlineType.Parallelogram -> {
                ParallelogramOutlineContainer(
                    outlines = listOf(ParallelogramShape(id = 0, title = "Parallelogram"))
                )
            }

            OutlineType.Diamond -> {
                DiamondOutlineContainer(
                    outlines = listOf(DiamondShape(id = 0, title = "Diamond"))
                )
            }

            OutlineType.Ticket -> {
                TicketOutlineContainer(
                    outlines = listOf(TicketShape(id = 0, title = "Ticket"))
                )
            }

            OutlineType.Custom -> {
                CustomOutlineContainer(
                    outlines = listOf(
                        CustomPathOutline(id = 0, title = "Custom", path = Paths.Favorite),
                        CustomPathOutline(id = 1, title = "Star", path = Paths.Star)
                    )
                )
            }

            OutlineType.ImageMask -> {

                val outlines = defaultImages.mapIndexed { index, image ->
                    ImageMaskOutline(id = index, title = "ImageMask", image = image)

                }
                ImageMaskOutlineContainer(
                    outlines = outlines
                )
            }
        }
    }

    fun editCropFrame(cropFrame: CropFrame) {
        val indexOf = cropFrames.indexOfFirst { it.outlineType == cropFrame.outlineType }
        cropFrames[indexOf] = cropFrame
    }
}