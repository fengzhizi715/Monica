package cn.netdiscovery.monica.imageprocess.filter.blur

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.blur.BoxBlurFilter
 * @author: Tony Shen
 * @date: 2024/4/27 13:36
 * @version: V1.0 <描述当前版本功能>
 */
class BoxBlurFilter(private val hRadius: Int =5, private val vRadius:Int=5, private val iterations:Int=1): BaseFilter() {

    override fun doFilter(image: BufferedImage): BufferedImage {
        var inPixels = IntArray(width * height)
        var outPixels = IntArray(width * height)
        getRGB(image, 0, 0, width, height, inPixels)

        for (i in 0 until iterations) {
            blur( inPixels, outPixels, width, height, hRadius )
            blur( outPixels, inPixels, height, width, vRadius )
        }

        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        setRGB(bufferedImage, 0, 0, width, height, inPixels)
        return bufferedImage
    }

    private fun blur(`in`: IntArray, out: IntArray, width: Int, height: Int, radius: Int) {
        val widthMinus1 = width - 1
        val tableSize = 2 * radius + 1
        val divide = IntArray(256 * tableSize)

        // the value scope will be 0 to 255, and number of 0 is table size
        // will get means from index not calculate result again since
        // color value must be  between 0 and 255.
        for (i in 0 until 256 * tableSize)
            divide[i] = i / tableSize

        var inIndex = 0

        //
        for (y in 0 until height) {
            var outIndex = y
            var ta = 0
            var tr = 0
            var tg = 0
            var tb = 0 // ARGB -> prepare for the alpha, red, green, blue color value.
            for (i in -radius..radius) {
                val rgb = `in`[inIndex + clamp(i, 0, width - 1)] // read input pixel data here. table size data.
                ta += rgb shr 24 and 0xff
                tr += rgb shr 16 and 0xff
                tg += rgb shr 8 and 0xff
                tb += rgb and 0xff
            }

            for (x in 0 until width) { // get output pixel data.
                out[outIndex] = divide[ta] shl 24 or (divide[tr] shl 16) or (divide[tg] shl 8) or divide[tb] // calculate the output data.
                var i1 = x + radius + 1
                if (i1 > widthMinus1) i1 = widthMinus1
                var i2 = x - radius
                if (i2 < 0) i2 = 0
                val rgb1 = `in`[inIndex + i1]
                val rgb2 = `in`[inIndex + i2]
                ta += (rgb1 shr 24 and 0xff) - (rgb2 shr 24 and 0xff)
                tr += (rgb1 and 0xff0000) - (rgb2 and 0xff0000) shr 16
                tg += (rgb1 and 0xff00) - (rgb2 and 0xff00) shr 8
                tb += (rgb1 and 0xff) - (rgb2 and 0xff)
                outIndex += height // per column or per row as cycle...
            }
            inIndex += width // next (i+ column number * n, n=1....n-1)
        }
    }

}