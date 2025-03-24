package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.PointFilter
import cn.netdiscovery.monica.imageprocess.math.PI
import java.awt.Color



/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.HSBAdjustFilter
 * @author: Tony Shen
 * @date: 2025/3/24 11:29
 * @version: V1.0 <描述当前版本功能>
 */
class HSBAdjustFilter(private val hFactor:Float = 0f, private val sFactor:Float = 0f, private val bFactor:Float = 0f): PointFilter() {

    private val hsb = FloatArray(3)

    init {
        canFilterIndexColorModel = true
    }

    override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
        val a = rgb and 0xff000000.toInt()
        val r = (rgb shr 16) and 0xff
        val g = (rgb shr 8) and 0xff
        val b = rgb and 0xff
        Color.RGBtoHSB(r, g, b, hsb)
        hsb[0] += hFactor
        while (hsb[0] < 0)
                hsb[0] += PI * 2
        hsb[1] += sFactor
        if (hsb[1] < 0)
            hsb[1] = 0f
        else if (hsb[1] > 1.0)
            hsb[1] = 1.0f
        hsb[2] += bFactor

        if (hsb[2] < 0)
            hsb[2] = 0f
        else if (hsb[2] > 1.0)
            hsb[2] = 1.0f

        return a or (Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) and 0xffffff)
    }
}