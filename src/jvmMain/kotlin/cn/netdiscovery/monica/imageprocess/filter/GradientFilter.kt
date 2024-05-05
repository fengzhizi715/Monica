package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.GradientFilter
 * @author: Tony Shen
 * @date: 2024/5/1 10:24
 * @version: V1.0 <描述当前版本功能>
 */
// prewitt operator
val PREWITT_X = arrayOf(intArrayOf(-1, 0, 1), intArrayOf(-1, 0, 1), intArrayOf(-1, 0, 1))
val PREWITT_Y = arrayOf(intArrayOf(-1, -1, -1), intArrayOf(0, 0, 0), intArrayOf(1, 1, 1))

// sobel operator
val SOBEL_X = arrayOf(intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1))
val SOBEL_Y = arrayOf(intArrayOf(-1, -2, -1), intArrayOf(0, 0, 0), intArrayOf(1, 2, 1))

// direction parameter
val X_DIRECTION = 0
val Y_DIRECTION = 2
val XY_DIRECTION = 4

class GradientFilter(val direction: Int = XY_DIRECTION, val isSobel:Boolean = true): BaseFilter() {

    override fun doFilter(image: BufferedImage, dstImage: BufferedImage): BufferedImage {
        val inPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        getRGB(image, 0, 0, width, height, inPixels)
        var index = 0
        var index2 = 0
        var xred = 0.0
        var xgreen = 0.0
        var xblue = 0.0
        var yred = 0.0
        var ygreen = 0.0
        var yblue = 0.0
        var newRow: Int
        var newCol: Int
        for (row in 0 until height) {
            val ta = 255
            var tr = 0
            var tg = 0
            var tb = 0
            for (col in 0 until width) {
                index = row * width + col
                for (subrow in -1..1) {
                    for (subcol in -1..1) {
                        newRow = row + subrow
                        newCol = col + subcol
                        if (newRow < 0 || newRow >= height) {
                            newRow = row
                        }
                        if (newCol < 0 || newCol >= width) {
                            newCol = col
                        }
                        index2 = newRow * width + newCol
                        tr = inPixels[index2] shr 16 and 0xff
                        tg = inPixels[index2] shr 8 and 0xff
                        tb = inPixels[index2] and 0xff
                        if (isSobel) {
                            xred += SOBEL_X[subrow + 1][subcol + 1] * tr
                            xgreen += SOBEL_X[subrow + 1][subcol + 1] * tg
                            xblue += SOBEL_X[subrow + 1][subcol + 1] * tb
                            yred += SOBEL_Y[subrow + 1][subcol + 1] * tr
                            ygreen += SOBEL_Y[subrow + 1][subcol + 1] * tg
                            yblue += SOBEL_Y[subrow + 1][subcol + 1] * tb
                        } else {
                            xred += PREWITT_X[subrow + 1][subcol + 1] * tr
                            xgreen += PREWITT_X[subrow + 1][subcol + 1] * tg
                            xblue += PREWITT_X[subrow + 1][subcol + 1] * tb
                            yred += PREWITT_Y[subrow + 1][subcol + 1] * tr
                            ygreen += PREWITT_Y[subrow + 1][subcol + 1] * tg
                            yblue += PREWITT_Y[subrow + 1][subcol + 1] * tb
                        }
                    }
                }
                val mred = Math.sqrt(xred * xred + yred * yred)
                val mgreen = Math.sqrt(xgreen * xgreen + ygreen * ygreen)
                val mblue = Math.sqrt(xblue * xblue + yblue * yblue)
                if (XY_DIRECTION === direction) {
                    outPixels[index] =
                        ta shl 24 or (clamp(mred.toInt()) shl 16) or (clamp(mgreen.toInt()) shl 8) or clamp(mblue.toInt())
                } else if (X_DIRECTION === direction) {
                    outPixels[index] =
                        ta shl 24 or (clamp(yred.toInt()) shl 16) or (clamp(ygreen.toInt()) shl 8) or clamp(yblue.toInt())
                } else if (Y_DIRECTION === direction) {
                    outPixels[index] =
                        ta shl 24 or (clamp(xred.toInt()) shl 16) or (clamp(xgreen.toInt()) shl 8) or clamp(xblue.toInt())
                } else {
                    // as default, always XY gradient
                    outPixels[index] =
                        ta shl 24 or (clamp(mred.toInt()) shl 16) or (clamp(mgreen.toInt()) shl 8) or clamp(mblue.toInt())
                }

                // cleanup for next loop
                newCol = 0
                newRow = newCol
                xblue = 0.0
                xgreen = xblue
                xred = xgreen
                yblue = 0.0
                ygreen = yblue
                yred = ygreen
            }
        }

        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }
}