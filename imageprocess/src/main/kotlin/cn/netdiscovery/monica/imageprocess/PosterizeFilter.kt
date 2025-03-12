package cn.netdiscovery.monica.imageprocess

import cn.netdiscovery.monica.imageprocess.filter.base.PointFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.PosterizeFilter
 * @author: Tony Shen
 * @date: 2025/3/12 13:47
 * @version: V1.0 <描述当前版本功能>
 */
class PosterizeFilter(private val numLevels:Int = 6): PointFilter() {

    private var levels: IntArray = IntArray(256)

    init{
        if (numLevels != 1)
            for (i in 0..255)
                levels[i] = 255 * (numLevels * i / 256) / (numLevels - 1)
    }

    override fun filterRGB(x: Int, y: Int, rgb: Int): Int {

        val a = rgb and 0xff000000.toInt()
        var r = (rgb shr 16) and 0xff
        var g = (rgb shr 8) and 0xff
        var b = rgb and 0xff
        r = levels[r]
        g = levels[g]
        b = levels[b]
        return a or (r shl 16) or (g shl 8) or b
    }
}