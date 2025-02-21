package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.NatureFilter
 * @author: Tony Shen
 * @date: 2024/7/11 14:06
 * @version: V1.0 <描述当前版本功能>
 */
class NatureFilter(val style:Int = 0) : ColorProcessorFilter() {

    val ATMOSPHERE_STYLE = 1
    val BURN_STYLE = 2
    val FOG_STYLE = 3
    val FREEZE_STYLE = 4
    val LAVA_STYLE = 5
    val METAL_STYLE = 6
    val OCEAN_STYLE = 7
    val WATER_STYLE = 8

    private lateinit var fogLookUp: IntArray

    init {
        buildFogLookupTable()
    }

    private fun buildFogLookupTable() {
        fogLookUp = IntArray(256)
        val fogLimit = 40
        for (i in fogLookUp.indices) {
            if (i > 127) {
                fogLookUp[i] = i - fogLimit
                if (fogLookUp[i] < 127) {
                    fogLookUp[i] = 127
                }
            } else {
                fogLookUp[i] = i + fogLimit
                if (fogLookUp[i] > 127) {
                    fogLookUp[i] = 127
                }
            }
        }
    }

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {
        val ta = 0
        var tr = 0
        var tg = 0
        var tb = 0
        for (i in 0 until size) {
            tr = R[i].toInt() and 0xff
            tg = G[i].toInt()  and 0xff
            tb = B[i].toInt()  and 0xff
            val onePixel: IntArray = processOnePixel(ta, tr, tg, tb)
            R[i] = onePixel[0].toByte()
            G[i] = onePixel[1].toByte()
            B[i] = onePixel[2].toByte()
        }

        return toBufferedImage(dstImage)
    }

    private fun processOnePixel(ta: Int, tr: Int, tg: Int, tb: Int): IntArray {
        val pixel = IntArray(4)
        pixel[0] = ta
        val gray = (tr + tg + tb) / 3
        when (style) {
            ATMOSPHERE_STYLE -> {
                pixel[1] = (tg + tb) / 2
                pixel[2] = (tr + tb) / 2
                pixel[3] = (tg + tr) / 2
            }

            BURN_STYLE -> {
                pixel[1] = clamp(gray * 3)
                pixel[2] = gray
                pixel[3] = gray / 3
            }

            FOG_STYLE -> {
                pixel[1] = fogLookUp[tr]
                pixel[2] = fogLookUp[tg]
                pixel[3] = fogLookUp[tb]
            }

            FREEZE_STYLE -> {
                pixel[1] = clamp(Math.abs((tr - tg - tb) * 1.5).toInt())
                pixel[2] = clamp(Math.abs((tg - tb - pixel[1]) * 1.5).toInt())
                pixel[3] = clamp(Math.abs((tb - pixel[1] - pixel[2]) * 1.5).toInt())
            }

            LAVA_STYLE -> {
                pixel[1] = gray
                pixel[2] = Math.abs(tb - 128)
                pixel[3] = Math.abs(tb - 128)
            }

            METAL_STYLE -> {
                var r = Math.abs(tr - 64).toFloat()
                var g = Math.abs(r - 64)
                var b = Math.abs(g - 64)
                val grayFloat = (222 * r + 707 * g + 71 * b) / 1000
                r = grayFloat + 70
                r = r + (r - 128) * 100 / 100f
                g = grayFloat + 65
                g = g + (g - 128) * 100 / 100f
                b = grayFloat + 75
                b = b + (b - 128) * 100 / 100f
                pixel[1] = clamp(r.toInt())
                pixel[2] = clamp(g.toInt())
                pixel[3] = clamp(b.toInt())
            }

            OCEAN_STYLE -> {
                pixel[1] = clamp(gray / 3)
                pixel[2] = gray
                pixel[3] = clamp(gray * 3)
            }

            WATER_STYLE -> {
                pixel[1] = clamp(gray - tg - tb)
                pixel[2] = clamp(gray - pixel[1] - tb)
                pixel[3] = clamp(gray - pixel[1] - pixel[2])
            }

            else -> {
                pixel[1] = (tg + tb) / 2
                pixel[2] = (tr + tb) / 2
                pixel[3] = (tg + tr) / 2
            }
        }
        return pixel
    }
}