package cn.netdiscovery.monica.imageprocess.filter.base

import cn.netdiscovery.monica.imageprocess.utils.clamp


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.base.TransferFilter
 * @author: Tony Shen
 * @date: 2025/3/12 17:58
 * @version: V1.0 <描述当前版本功能>
 */
abstract class TransferFilter : PointFilter(){

    protected lateinit var rTable: IntArray
    protected lateinit var gTable: IntArray
    protected lateinit var bTable: IntArray

    init {
        canFilterIndexColorModel = true
    }

    private fun makeTable(): IntArray {
        val table = IntArray(256)
        for (i in 0..255) {
            table[i] = clamp((255 * transferFunction(i / 255.0f)).toInt())
        }

        return table
    }

    abstract fun transferFunction(v: Float): Float

    override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
        rTable = makeTable()
        gTable = rTable
        bTable = rTable

        val a = rgb and 0xff000000.toInt()
        var r = (rgb shr 16) and 0xff
        var g = (rgb shr 8) and 0xff
        var b = rgb and 0xff

        r = rTable[r]
        g = gTable[g]
        b = bTable[b]
        return a or (r shl 16) or (g shl 8) or b
    }

//    fun getLUT(): IntArray {
//        val lut = IntArray(256)
//        for (i in 0..255) {
//            lut[i] = filterRGB(0, 0, (i shl 24) or (i shl 16) or (i shl 8) or i)
//        }
//        return lut
//    }
}