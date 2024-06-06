package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.StrokeAreaFilter
 * @author: Tony Shen
 * @date: 2024/5/25 22:00
 * @version: V1.0 <描述当前版本功能>
 */
class StrokeAreaFilter(private val size:Double = 10.0):BaseFilter() {

    private val d02 = (150 * 150).toDouble()

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val outPixels = IntArray(width * height)

        var index = 0
        var index2 = 0
        val semiRow = (size / 2).toInt()
        val semiCol = (size / 2).toInt()
        var newX: Int
        var newY: Int

        // initialize the color RGB array with zero...
        val rgb = IntArray(3)
        val rgb2 = IntArray(3)
        for (i in rgb.indices) {
            rgb2[i] = 0
            rgb[i] = rgb2[i]
        }

        // start the algorithm process here
        for (row in 0 until height) {
            var ta = 0
            for (col in 0 until width) {
                index = row * width + col
                ta = inPixels[index] shr 24 and 0xff
                rgb[0] = inPixels[index] shr 16 and 0xff
                rgb[1] = inPixels[index] shr 8 and 0xff
                rgb[2] = inPixels[index] and 0xff

                /* adjust region to fit in source image */
                // color difference and moment Image
                var moment = 0.0
                for (subRow in -semiRow..semiRow) {
                    for (subCol in -semiCol..semiCol) {
                        newY = row + subRow
                        newX = col + subCol
                        if (newY < 0) {
                            newY = 0
                        }
                        if (newX < 0) {
                            newX = 0
                        }
                        if (newY >= height) {
                            newY = height - 1
                        }
                        if (newX >= width) {
                            newX = width - 1
                        }
                        index2 = newY * width + newX
                        rgb2[0] = inPixels[index2] shr 16 and 0xff // red
                        rgb2[1] = inPixels[index2] shr 8 and 0xff // green
                        rgb2[2] = inPixels[index2] and 0xff // blue
                        moment += colorDiff(rgb, rgb2)
                    }
                }
                // calculate the output pixel value.
                val outPixelValue: Int = clamp((255.0 * moment / (size * size)).toInt())
                outPixels[index] = ta shl 24 or (outPixelValue shl 16) or (outPixelValue shl 8) or outPixelValue
            }
        }

        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }

    private fun colorDiff(rgb1: IntArray, rgb2: IntArray): Double {
        // (1-(d/d0)^2)^2
        val d2: Double
        val r2: Double
        d2 = colorDistance(rgb1, rgb2)
        if (d2 >= d02) return 0.0
        r2 = d2 / d02
        return (1.0 - r2) * (1.0 - r2)
    }

    private fun colorDistance(rgb1: IntArray, rgb2: IntArray): Double {
        val dr: Int
        val dg: Int
        val db: Int
        dr = rgb1[0] - rgb2[0]
        dg = rgb1[1] - rgb2[1]
        db = rgb1[2] - rgb2[2]
        return (dr * dr + dg * dg + db * db).toDouble()
    }
}