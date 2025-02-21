package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.GammaFilter
 * @author: Tony Shen
 * @date: 2024/4/29 17:34
 * @version: V1.0 <描述当前版本功能>
 */
class GammaFilter(private val gamma:Double = 0.5): BaseFilter() {

    private val lut: IntArray = IntArray(256)

    init {
        setupGammaLut()
    }

    private fun setupGammaLut() {
        for (i in 0..255) {
            lut[i] = (Math.exp(Math.log(i / 255.0) * gamma) * 255.0).toInt()
        }
    }

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val outPixels = IntArray(width * height)

        var index = 0
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

                // LUT search
                tr = lut[tr]
                tg = lut[tg]
                tb = lut[tb]
                outPixels[index] = ta shl 24 or (clamp(tr) shl 16) or (clamp(tg) shl 8) or clamp(tb)
            }
        }

        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }
}