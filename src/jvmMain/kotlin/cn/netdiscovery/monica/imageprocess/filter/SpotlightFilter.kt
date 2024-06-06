package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import java.awt.image.BufferedImage
import kotlin.math.sqrt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.SpotlightFilter
 * @author: Tony Shen
 * @date: 2024/4/29 15:23
 * @version: V1.0 <描述当前版本功能>
 */
class SpotlightFilter(private val factor:Int = 1): BaseFilter() {

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val outPixels = IntArray(width * height)

        var index = 0
        val centerX = width / 2
        val centerY = height / 2
        val maxDistance = sqrt((centerX * centerX + centerY * centerY).toDouble())
        for (row in 0 until height) {
            var ta = 0
            var tr = 0
            var tg = 0
            var tb = 0
            for (col in 0 until width) {
                index = row * width + col
                ta = inPixels[index] shr 24 and 0xff
                tr = inPixels[index] shr 16 and 0xff
                tg = inPixels[index] shr 8 and 0xff
                tb = inPixels[index] and 0xff
                var scale: Double = 1.0 - getDistance(centerX, centerY, col, row) / maxDistance
                for (i in 0 until factor) {
                    scale = scale * scale
                }
                tr = (scale * tr).toInt()
                tg = (scale * tg).toInt()
                tb = (scale * tb).toInt()
                outPixels[index] = ta shl 24 or (tr shl 16) or (tg shl 8) or tb
            }
        }

        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }

    private fun getDistance(centerX: Int, centerY: Int, px: Int, py: Int): Double {
        val xx = ((centerX - px) * (centerX - px)).toDouble()
        val yy = ((centerY - py) * (centerY - py)).toDouble()
        return sqrt(xx + yy).toInt().toDouble()
    }
}