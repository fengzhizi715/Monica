package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.SepiaToneFilter
 * @author: Tony Shen
 * @date: 2024/5/1 11:09
 * @version: V1.0 SepiaTone 滤镜， 老照片特效
 */
class SepiaToneFilter : BaseFilter() {

    override fun doFilter(image: BufferedImage): BufferedImage {
        val inPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        getRGB(image, 0, 0, width, height, inPixels)

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

                val fr = colorBlend(noise(), tr * 0.393 + tg * 0.769 + tb * 0.189, tr).toInt()
                val fg = colorBlend(noise(), tr * 0.349 + tg * 0.686 + tb * 0.168, tg).toInt()
                val fb = colorBlend(noise(), tr * 0.272 + tg * 0.534 + tb * 0.131, tb).toInt()
                outPixels[index] = ta shl 24 or (clamp(fr) shl 16) or (clamp(fg) shl 8) or clamp(fb)
            }
        }

        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        setRGB(bufferedImage, 0, 0, width, height, outPixels)
        return bufferedImage
    }

    private fun noise(): Double = Math.random() * 0.5 + 0.5

    private fun colorBlend(scale: Double, dest: Double, src: Int): Double {
        return scale * dest + (1.0 - scale) * src
    }
}