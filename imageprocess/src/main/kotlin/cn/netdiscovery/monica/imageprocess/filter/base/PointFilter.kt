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