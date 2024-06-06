package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.EmbossFilter
 * @author: Tony Shen
 * @date: 2024/5/9 11:01
 * @version: V1.0 <描述当前版本功能>
 */
class EmbossFilter(private val colorConstant:Int = 100, private val out:Boolean = false): ColorProcessorFilter() {

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {
        val outPixels = IntArray(width * height)

        var offset = 0
        var r1 = 0
        var g1 = 0
        var b1 = 0
        var r2 = 0
        var g2 = 0
        var b2 = 0
        var r = 0
        var g = 0
        var b = 0

        for (y in 1 until height - 1) {
            offset = y * width
            var ta = 0
            for (x in 1 until width - 1) {
                r1 = R[offset].toInt() and 0xff
                g1 = G[offset].toInt() and 0xff
                b1 = B[offset].toInt() and 0xff
                r2 = R[offset + width].toInt() and 0xff
                g2 = G[offset + width].toInt() and 0xff
                b2 = B[offset + width].toInt() and 0xff
                if (out) {
                    r = r1 - r2
                    g = g1 - g2
                    b = b1 - b2
                } else {
                    r = r2 - r1
                    g = g2 - g1
                    b = b2 - b1
                }
                r = clamp(r + colorConstant)
                g = clamp(g + colorConstant)
                b = clamp(b + colorConstant)

                ta = inPixels[offset] shr 24 and 0xff
//                outPixels[offset] = ta shl 24 or (r shl 16) or (g shl 8) or b
                R[offset] = r.toByte()
                G[offset] = g.toByte()
                B[offset] = b.toByte()

                offset++
            }
        }

//        setRGB(dstImage, 0, 0, width, height, outPixels)
        setRGB(width, height, outPixels, R, G, B)
        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }
}