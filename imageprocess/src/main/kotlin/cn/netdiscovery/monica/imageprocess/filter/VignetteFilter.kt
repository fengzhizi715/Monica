package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.Color
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.VignetteFilter
 * @author: Tony Shen
 * @date: 2024/5/29 12:50
 * @version: V1.0 <描述当前版本功能>
 */
class VignetteFilter(
    private val fade:Int = 35,
    private val vignetteWidth:Int = 50
): BaseFilter() {

    private val vignetteColor: Color = Color.black

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val outPixels = IntArray(width * height)

        var index = 0
        for (row in 0 until height) {
            var ta = 0
            var tr = 0
            var tg = 0
            var tb = 0
            for (col in 0 until width) {
                val dX = Math.min(col, width - col)
                val dY = Math.min(row, height - row)
                index = row * width + col
                ta = inPixels[index] shr 24 and 0xff
                tr = inPixels[index] shr 16 and 0xff
                tg = inPixels[index] shr 8 and 0xff
                tb = inPixels[index] and 0xff
                if ((dY <= vignetteWidth) and (dX <= vignetteWidth)) {
                    val k = 1 - (dY.coerceAtMost(dX) - vignetteWidth + fade).toDouble() / fade.toDouble()
                    outPixels[index] = superpositionColor(ta, tr, tg, tb, k)
                    continue
                }
                if ((dX < vignetteWidth - fade) or (dY < vignetteWidth - fade)) {
                    outPixels[index] = ta shl 24 or (vignetteColor.red.toInt() shl 16) or (vignetteColor.green.toInt() shl 8) or vignetteColor.blue.toInt()
                } else {
                    if ((dX < vignetteWidth) and (dY > vignetteWidth)) {
                        val k = 1 - (dX - vignetteWidth + fade).toDouble() / fade.toDouble()
                        outPixels[index] = superpositionColor(ta, tr, tg, tb, k)
                    } else {
                        if ((dY < vignetteWidth) and (dX > vignetteWidth)) {
                            val k = 1 - (dY - vignetteWidth + fade).toDouble() / fade.toDouble()
                            outPixels[index] = superpositionColor(ta, tr, tg, tb, k)
                        } else {
                            outPixels[index] = ta shl 24 or (tr shl 16) or (tg shl 8) or tb
                        }
                    }
                }
            }
        }

        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }

    private fun superpositionColor(
        ta: Int,
        tr: Int,
        tg: Int,
        tb: Int,
        k: Double
    ): Int {
        var red = tr
        var green = tg
        var blue = tb
        red = (vignetteColor.red * k + red * (1.0 - k)).toInt()
        green = (vignetteColor.green * k + green * (1.0 - k)).toInt()
        blue = (vignetteColor.blue * k + blue * (1.0 - k)).toInt()
        return ta shl 24 or (clamp(red) shl 16) or (clamp(green) shl 8) or clamp(blue)
    }
}