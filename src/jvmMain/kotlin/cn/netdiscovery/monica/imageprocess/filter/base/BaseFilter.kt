package cn.netdiscovery.monica.imageprocess.filter.base

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.Transformer
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
 * @author: Tony Shen
 * @date: 2024/4/27 13:32
 * @version: V1.0 <描述当前版本功能>
 */
abstract class BaseFilter: Transformer {

    protected var width = 0
    protected var height = 0
    protected var type = 0

    override fun transform(image: BufferedImage): BufferedImage {
        width  = image.width
        height = image.height
        type = image.type
        val dstImage = BufferedImages.create(width,height,type)

        return doFilter(image,dstImage)
    }

    abstract fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     */
    fun getRGB(image: BufferedImage, x: Int, y: Int, width: Int, height: Int, pixels: IntArray?): IntArray {
        val type = image.type
        return if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) image.raster.getDataElements(
            x,
            y,
            width,
            height,
            pixels
        ) as IntArray else image.getRGB(x, y, width, height, pixels, 0, width)
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    fun setRGB(image: BufferedImage, x: Int, y: Int, width: Int, height: Int, pixels: IntArray) {
        val type = image.type
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) image.raster.setDataElements(
            x,
            y,
            width,
            height,
            pixels
        ) else image.setRGB(x, y, width, height, pixels, 0, width)
    }
}