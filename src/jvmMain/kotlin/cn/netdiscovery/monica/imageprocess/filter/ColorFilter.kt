package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import cn.netdiscovery.monica.imageprocess.lut.*
import cn.netdiscovery.monica.imageprocess.lut.SummerLUT.SUMMER_LUT
import java.awt.image.BufferedImage


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.ColorFilter
 * @author: Tony Shen
 * @date: 2024/6/17 14:23
 * @version: V1.0 <描述当前版本功能>
 */

class ColorFilter : ColorProcessorFilter() {

    private var style: Int

    init {
        style = SUMMER_STYLE
    }

    private fun getStyleLUT(style: Int): Array<IntArray> = getColorFilterLUT(style)

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {
        var tr = 0
        var tg = 0
        var tb = 0
        val lut = getStyleLUT(style)
        val size: Int = R.size
        for (i in 0 until size) {
            tr = R[i].toInt() and 0xff
            tg = G[i].toInt() and 0xff
            tb = B[i].toInt() and 0xff
            R[i] = lut[tr][0].toByte()
            G[i] = lut[tg][1].toByte()
            B[i] = lut[tb][2].toByte()
        }

        return toBufferedImage(dstImage)
    }
}