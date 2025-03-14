package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.blur.GaussianFilter
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.HighPassFilter
 * @author: Tony Shen
 * @date: 2024/5/5 14:00
 * @version: V1.0 <描述当前版本功能>
 */
class HighPassFilter(override val radius: Float =10f): GaussianFilter(radius) {

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val outPixels = IntArray(width * height)

        if (radius > 0) {
            convolveAndTranspose(kernel, inPixels, outPixels, width, height, alpha, alpha && premultiplyAlpha, false, CLAMP_EDGES)
            convolveAndTranspose(kernel, outPixels, inPixels, height, width, alpha, false, alpha && premultiplyAlpha, CLAMP_EDGES)
        }

        getRGB(srcImage, 0, 0, width, height, outPixels)

        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val rgb1 = outPixels[index]
                var r1 = rgb1 shr 16 and 0xff
                var g1 = rgb1 shr 8 and 0xff
                var b1 = rgb1 and 0xff
                val rgb2 = inPixels[index]
                val r2 = rgb2 shr 16 and 0xff
                val g2 = rgb2 shr 8 and 0xff
                val b2 = rgb2 and 0xff
                r1 = (r1 + 255 - r2) / 2
                g1 = (g1 + 255 - g2) / 2
                b1 = (b1 + 255 - b2) / 2
                inPixels[index] = (rgb1 and 0xff000000.toInt()) or (r1 shl 16) or (g1 shl 8) or b1
                index++
            }
        }

        setRGB(dstImage, 0, 0, width, height, inPixels)
        return dstImage
    }
}