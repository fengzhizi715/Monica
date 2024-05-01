package cn.netdiscovery.monica.imageprocess.filter

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.ln

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.WhiteImageFilter
 * @author: Tony Shen
 * @date: 2024/5/1 12:27
 * @version: V1.0 <描述当前版本功能>
 */
class WhiteImageFilter(private val beta:Double = 1.1): BaseFilter() {

    override fun doFilter(image: BufferedImage): BufferedImage {
        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

        // make LUT
        val lut = IntArray(256)
        for (i in 0..255) {
            lut[i] = imageMath(i)
        }

        for (row in 0 until height) {
            for (col in 0 until width) {
                val rgb = image.getRGB(col,row)

                var r = rgb and (0x00ff0000 shr 16)
                var g = rgb and (0x0000ff00 shr 8)
                var b = rgb and 0x000000ff

                r = lut[r and 0xff]
                g = lut[g and 0xff]
                b = lut[b and 0xff]
                bufferedImage.setRGB(col, row, Color(r, g, b).rgb)
            }
        }

        return bufferedImage
    }

    private fun imageMath(gray: Int): Int {
        val scale = 255 / (ln(255 * (this.beta - 1) + 1) / ln(this.beta))
        val p1 = ln(gray * (this.beta - 1) + 1)
        val np = p1 / ln(this.beta)
        return (np * scale).toInt()
    }
}