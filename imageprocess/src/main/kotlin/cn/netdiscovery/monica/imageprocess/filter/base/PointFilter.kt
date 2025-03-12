package cn.netdiscovery.monica.imageprocess.filter.base

import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.base.PointFilter
 * @author: Tony Shen
 * @date: 2025/3/11 21:00
 * @version: V1.0 <描述当前版本功能>
 */
abstract class PointFilter: BaseFilter() {

    protected var canFilterIndexColorModel: Boolean = false

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        setDimensions(width, height)

//        val srcRaster: WritableRaster = srcImage.raster
//        val dstRaster: WritableRaster = dstImage.raster
//
//        val inPixels = IntArray(width)
//        for (y in 0..<height) {
//            if (type == BufferedImage.TYPE_INT_ARGB) {
//                srcRaster.getDataElements(0, y, width, 1, inPixels)
//
//                for (x in 0..<width)
//                    inPixels[x] = filterRGB(x, y, inPixels[x])
//
//                dstRaster.setDataElements(0, y, width, 1, inPixels)
//            } else {
//                srcImage.getRGB(0, y, width, 1, inPixels, 0, width)
//
//                for (x in 0..<width)
//                    inPixels[x] = filterRGB(x, y, inPixels[x])
//
//                dstImage.setRGB(0, y, width, 1, inPixels, 0, width)
//            }
//        }

        val outPixels = IntArray(width * height)

        var index = 0
        for (row in 0 until height) {
            for (col in 0 until width) {
                index = row * width + col
                outPixels[index] = filterRGB(col, row, inPixels[index])
            }
        }

        setRGB(dstImage, 0, 0, width, height, outPixels)

        return dstImage
    }

    open fun setDimensions(width: Int, height: Int) {
    }

    abstract fun filterRGB(x: Int, y: Int, rgb: Int): Int
}