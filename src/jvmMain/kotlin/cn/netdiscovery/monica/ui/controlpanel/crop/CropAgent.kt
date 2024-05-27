package cn.netdiscovery.monica.ui.controlpanel.crop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.subImage
import cn.netdiscovery.monica.ui.controlpanel.crop.model.CropImageMask
import cn.netdiscovery.monica.ui.controlpanel.crop.model.CropOutline
import cn.netdiscovery.monica.ui.controlpanel.crop.model.CropPath
import cn.netdiscovery.monica.ui.controlpanel.crop.model.CropShape
import org.jetbrains.skia.Matrix33
import java.awt.Image

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.CropAgent
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
    ): ImageBitmap {
        val tmp = croppedImageBitmap.toAwtImage().getScaledInstance(requiredWidth, requiredHeight, Image.SCALE_SMOOTH)
        val resizedImage = BufferedImages.create(requiredWidth, requiredHeight,croppedImageBitmap.toAwtImage().type)
        val g2d = resizedImage.createGraphics()
        try {
            g2d.drawImage(tmp, 0, 0, null)
        } finally {
            g2d.dispose()
        }

        return resizedImage.toComposeImageBitmap()
    }
}