package cn.netdiscovery.monica.imageprocess.filter.blur

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.imageprocess.utils.premultiply
import cn.netdiscovery.monica.imageprocess.utils.unpremultiply
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.blur.VariableBlurFilter
 * @author: Tony Shen
 * @date:  2024/5/4 15:04
 * @version: V1.0 <描述当前版本功能>
 */
class VariableBlurFilter(private val hRadius: Int =5, private val vRadius:Int=5, private val iterations:Int=1, private val premultiplyAlpha: Boolean = true): BaseFilter() {

    private var blurMask: BufferedImage? = null
        set(value) {
            field = value
        }

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        val outPixels = IntArray(width * height)

        if (premultiplyAlpha) premultiply(inPixels, 0, inPixels.size)

        for (i in 0 until iterations) {
            blur(inPixels, outPixels, width, height, hRadius, 1)
            blur(outPixels, inPixels, height, width, vRadius, 2)
        }

        if (premultiplyAlpha) unpremultiply(inPixels, 0, inPixels.size)

        setRGB(dstImage, 0, 0, width, height, inPixels)
        return dstImage
    }

    fun blur(`in`: IntArray, out: IntArray, width: Int, height: Int, radius: Int, pass: Int) {
        val widthMinus1 = width - 1
        val r = IntArray(width)
        val g = IntArray(width)
        val b = IntArray(width)
        val a = IntArray(width)
        val mask = IntArray(width)

        var inIndex = 0

        for (y in 0 until height) {
            var outIndex = y

            if (blurMask != null) {
                if (pass == 1) getRGB(blurMask!!, 0, y, width, 1, mask)
                else getRGB(blurMask!!, y, 0, 1, width, mask)
            }

            for (x in 0 until width) {
                val argb = `in`[inIndex + x]
                a[x] = (argb shr 24) and 0xff
                r[x] = (argb shr 16) and 0xff
                g[x] = (argb shr 8) and 0xff
                b[x] = argb and 0xff
                if (x != 0) {
                    a[x] += a[x - 1]
                    r[x] += r[x - 1]
                    g[x] += g[x - 1]
                    b[x] += b[x - 1]
                }
            }

            for (x in 0 until width) {
                // Get the blur radius at x, y
                var ra = if (blurMask != null) {
                    if (pass == 1) ((mask[x] and 0xff) * hRadius / 255f).toInt()
                    else ((mask[x] and 0xff) * vRadius / 255f).toInt()
                } else {
                    if (pass == 1) (blurRadiusAt(x, y, width, height) * hRadius).toInt()
                    else (blurRadiusAt(y, x, height, width) * vRadius).toInt()
                }

                val divisor = 2 * ra + 1
                var ta = 0
                var tr = 0
                var tg = 0
                var tb = 0
                var i1 = x + ra
                if (i1 > widthMinus1) {
                    val f = i1 - widthMinus1
                    val l = widthMinus1
                    ta += (a[l] - a[l - 1]) * f
                    tr += (r[l] - r[l - 1]) * f
                    tg += (g[l] - g[l - 1]) * f
                    tb += (b[l] - b[l - 1]) * f
                    i1 = widthMinus1
                }
                var i2 = x - ra - 1
                if (i2 < 0) {
                    ta -= a[0] * i2
                    tr -= r[0] * i2
                    tg -= g[0] * i2
                    tb -= b[0] * i2
                    i2 = 0
                }

                ta += a[i1] - a[i2]
                tr += r[i1] - r[i2]
                tg += g[i1] - g[i2]
                tb += b[i1] - b[i2]
                out[outIndex] =
                    ((ta / divisor) shl 24) or ((tr / divisor) shl 16) or ((tg / divisor) shl 8) or (tb / divisor)

                outIndex += height
            }
            inIndex += width
        }
    }

    /**
     * Override this to get a different blur radius at eahc point.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the image
     * @param height the height of the image
     * @return the blur radius
     */
    private fun blurRadiusAt(x: Int, y: Int, width: Int, height: Int): Float {
        return x.toFloat() / width
    }
}