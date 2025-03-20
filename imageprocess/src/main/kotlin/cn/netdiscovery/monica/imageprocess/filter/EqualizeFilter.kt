package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.domain.Histogram
import cn.netdiscovery.monica.imageprocess.filter.base.WholeImageFilter
import java.awt.Rectangle

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.EqualizeFilter
 * @author: Tony Shen
 * @date: 2025/3/20 19:58
 * @version: V1.0 <描述当前版本功能>
 */
class EqualizeFilter: WholeImageFilter() {

    private var lut: Array<IntArray>?=null

    override fun filterPixels(width: Int, height: Int, inPixels: IntArray, transformedSpace: Rectangle): IntArray {
        val histogram = Histogram(inPixels, width, height, 0, width)

        var i: Int
        var j: Int

        if (histogram.getNumSamples() > 0) {
            val scale: Float = 255.0f / histogram.getNumSamples()
            lut = Array(3) { IntArray(256) }
            i = 0
            while (i < 3) {
                lut!![i][0] = histogram.getFrequency(i, 0)
                j = 1
                while (j < 256) {
                    lut!![i][j] = lut!![i][j - 1] + histogram.getFrequency(i, j)
                    j++
                }
                j = 0
                while (j < 256) {
                    lut!![i][j] = Math.round(lut!![i][j] * scale)
                    j++
                }
                i++
            }
        } else lut = null

        i = 0
        for (y in 0..<height) {
            for (x in 0..<width) {
                inPixels[i] = filterRGB(x, y, inPixels[i])
                i++
            }
        }

        lut = null

        return inPixels
    }


    private fun filterRGB(x: Int, y: Int, rgb: Int): Int {
        if (lut != null) {
            val a = rgb and 0xff000000.toInt()
            val r = lut!![Histogram.RED][(rgb shr 16) and 0xff]
            val g = lut!![Histogram.GREEN][(rgb shr 8) and 0xff]
            val b = lut!![Histogram.BLUE][rgb and 0xff]

            return a or (r shl 16) or (g shl 8) or b
        }
        return rgb
    }

}