package cn.netdiscovery.monica.ui.controlpanel.cropimage

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.imageprocess.utils.extension.resize
import cn.netdiscovery.monica.imageprocess.utils.extension.subImage
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropImageMask
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropOutline
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropPath
import cn.netdiscovery.monica.ui.controlpanel.cropimage.model.CropShape
import org.jetbrains.skia.Matrix33

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.cropimage.CropAgent
 * @author: Tony Shen
 * @date: 2024/5/26 15:45
 * @version: V1.0 <描述当前版本功能>
 */
class CropAgent {

    private val imagePaint = Paint().apply {
        blendMode = BlendMode.SrcIn
    }

    private val paint = Paint()

    fun crop(
        imageBitmap: ImageBitmap,
        cropRect: Rect,
        cropOutline: CropOutline,
        layoutDirection: LayoutDirection,
        density: Density,
    ): ImageBitmap {

        val imageToCrop = imageBitmap.toAwtImage().subImage(cropRect.left.toInt(),cropRect.top.toInt(),cropRect.width.toInt(),cropRect.height.toInt()).toComposeImageBitmap()

        drawCroppedImage(cropOutline, cropRect, layoutDirection, density, imageToCrop)

        return imageToCrop
    }

    private fun drawCroppedImage(
        cropOutline: CropOutline,
        cropRect: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
        imageToCrop: ImageBitmap,
    ) {

        when (cropOutline) {
            is CropShape -> {

                val path = Path().apply {
                    val outline = cropOutline.shape.createOutline(cropRect.size, layoutDirection, density)
                    addOutline(outline)
                }

                Canvas(image = imageToCrop).run {
                    saveLayer(cropRect, imagePaint)

                    // Destination
                    drawPath(path, paint)

                    // Source
                    drawImage(
                        image = imageToCrop,
                        topLeftOffset = Offset.Zero,
                        paint = imagePaint
                    )
                    restore()
                }
            }
            is CropPath -> {

                val path = Path().apply {

                    addPath(cropOutline.path)

                    val pathSize = getBounds().size
                    val rectSize = cropRect.size

                    val matrix = Matrix33.makeScale(
                        rectSize.width / pathSize.width,
                        cropRect.height / pathSize.height
                    )
                    this.asSkiaPath().transform(matrix)

                    val left = getBounds().left
                    val top = getBounds().top

                    translate(Offset(-left, -top))
                }

                Canvas(image = imageToCrop).run {
                    saveLayer(cropRect, imagePaint)

                    // Destination
                    drawPath(path, paint)

                    // Source
                    drawImage(image = imageToCrop, topLeftOffset = Offset.Zero, imagePaint)
                    restore()
                }
            }
            is CropImageMask -> {

                val imageMask = cropOutline.image.toAwtImage().subImage(cropRect.left.toInt(),cropRect.top.toInt(),cropRect.width.toInt(),cropRect.height.toInt()).toComposeImageBitmap()

                Canvas(image = imageToCrop).run {
                    saveLayer(cropRect, imagePaint)

                    // Destination
                    drawImage(imageMask, topLeftOffset = Offset.Zero, paint)

                    // Source
                    drawImage(image = imageToCrop, topLeftOffset = Offset.Zero, imagePaint)

                    restore()
                }
            }
        }
    }

    fun resize(
        croppedImageBitmap: ImageBitmap,
        requiredWidth: Int,
        requiredHeight: Int
    ): ImageBitmap = croppedImageBitmap.toAwtImage().resize(requiredWidth,requiredHeight).toComposeImageBitmap()
}