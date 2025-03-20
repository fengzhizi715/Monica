package cn.netdiscovery.monica.imageprocess.filter.base

import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.base.WholeImageFilter
 * @author: Tony Shen
 * @date: 2025/3/20 10:51
 * @version: V1.0 <描述当前版本功能>
 */
abstract class WholeImageFilter:BaseFilter() {

    /**
     * The output image bounds.
     */
    protected lateinit var transformedSpace: Rectangle

    /**
     * The input image bounds.
     */
    protected lateinit var originalSpace: Rectangle


    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        val srcRaster: WritableRaster = srcImage.raster

        originalSpace = Rectangle(0, 0, width, height)
        transformedSpace = Rectangle(0, 0, width, height)
        transformSpace(transformedSpace)

        val dstRaster: WritableRaster = dstImage.raster

        var inPixels = getRGB(srcImage, 0, 0, width, height, null)
        inPixels = filterPixels(width, height, inPixels, transformedSpace)
        setRGB(dstImage, 0, 0, transformedSpace.width, transformedSpace.height, inPixels)

        return dstImage
    }

    /**
     * Calculate output bounds for given input bounds.
     * @param rect input and output rectangle
     */
    open fun transformSpace(rect: Rectangle) {
    }

    /**
     * Actually filter the pixels.
     * @param width the image width
     * @param height the image height
     * @param inPixels the image pixels
     * @param transformedSpace the output bounds
     * @return the output pixels
     */
    protected abstract fun filterPixels(
        width: Int,
        height: Int,
        inPixels: IntArray,
        transformedSpace: Rectangle
    ): IntArray
}