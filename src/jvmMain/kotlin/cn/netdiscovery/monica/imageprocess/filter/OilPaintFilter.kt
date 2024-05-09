package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import java.awt.image.BufferedImage


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.OilPaintFilter
 * @author: Tony Shen
 * @date: 2024/5/8 20:38
 * @version: V1.0 <描述当前版本功能>
 */
class OilPaintFilter(private val ksize:Int = 10,private val intensity:Int = 40): BaseFilter() {

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val inPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        getRGB(srcImage, 0, 0, width, height, inPixels)
        var index = 0
        val subradius: Int = this.ksize / 2
        val intensityCount = IntArray((intensity + 1))
        val ravg = IntArray((intensity + 1))
        val gavg = IntArray((intensity + 1))
        val bavg = IntArray((intensity + 1))
        for (i in 0..intensity) {
            intensityCount[i] = 0
            ravg[i] = 0
            gavg[i] = 0
            bavg[i] = 0
        }
        for (row in 0 until height) {
            val ta = 0
            var tr = 0
            var tg = 0
            var tb = 0
            for (col in 0 until width) {
                for (subRow in -subradius..subradius) {
                    for (subCol in -subradius..subradius) {
                        var nrow = row + subRow
                        var ncol = col + subCol
                        if (nrow >= height || nrow < 0) {
                            nrow = 0
                        }
                        if (ncol >= width || ncol < 0) {
                            ncol = 0
                        }
                        index = nrow * width + ncol
                        tr = inPixels[index] shr 16 and 0xff
                        tg = inPixels[index] shr 8 and 0xff
                        tb = inPixels[index] and 0xff
                        val curIntensity = (((tr + tg + tb) / 3).toDouble() * intensity / 255.0f).toInt()
                        intensityCount[curIntensity]++
                        ravg[curIntensity] += tr
                        gavg[curIntensity] += tg
                        bavg[curIntensity] += tb
                    }
                }

                // find the max number of same gray level pixel
                var maxCount = 0
                var maxIndex = 0
                for (m in intensityCount.indices) {
                    if (intensityCount[m] > maxCount) {
                        maxCount = intensityCount[m]
                        maxIndex = m
                    }
                }

                // get average value of the pixel
                val nr = ravg[maxIndex] / maxCount
                val ng = gavg[maxIndex] / maxCount
                val nb = bavg[maxIndex] / maxCount
                index = row * width + col
                outPixels[index] = ta shl 24 or (nr shl 16) or (ng shl 8) or nb

                // post clear values for next pixel
                for (i in 0..intensity) {
                    intensityCount[i] = 0
                    ravg[i] = 0
                    gavg[i] = 0
                    bavg[i] = 0
                }
            }
        }
        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }
}