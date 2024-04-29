package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.ConBriFilter
 * @author: Tony Shen
 * @date: 2024/4/27 13:57
 * @version: V1.0 <描述当前版本功能>
 */
class ConBriFilter(private val contrast:Float = 1.5f,private val brightness:Float =1.0f): BaseFilter() {

    override fun doFilter(image: BufferedImage): BufferedImage {
        val inPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        getRGB(image, 0, 0, width, height, inPixels)

        // calculate RED, GREEN, BLUE means of pixel
        var index = 0
        val rgbmeans = IntArray(3)
        var redSum = 0.0
        var greenSum = 0.0
        var blueSum = 0.0
        val total = (height * width).toDouble()
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
                redSum += tr.toDouble()
                greenSum += tg.toDouble()
                blueSum += tb.toDouble()
            }
        }

        rgbmeans[0] = (redSum / total).toInt()
        rgbmeans[1] = (greenSum / total).toInt()
        rgbmeans[2] = (blueSum / total).toInt()

        // adjust contrast and brightness algorithm, here
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

                // remove means
                tr -= rgbmeans[0]
                tg -= rgbmeans[1]
                tb -= rgbmeans[2]

                tr *= contrast.toInt()
                tg *= contrast.toInt()
                tb *= contrast.toInt()

                tr += rgbmeans[0] * brightness.toInt()
                tg += rgbmeans[1] * brightness.toInt()
                tb += rgbmeans[2] * brightness.toInt()
                outPixels[index] = ta shl 24 or (clamp(tr) shl 16) or (clamp(tg) shl 8) or clamp(tb)
            }
        }

        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        setRGB(bufferedImage, 0, 0, width, height, outPixels)
        return bufferedImage
    }
}