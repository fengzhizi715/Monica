package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransformFilter
import cn.netdiscovery.monica.imageprocess.math.TWO_PI
import java.awt.image.BufferedImage
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.WaterFilter
 * @author: Tony Shen
 * @date: 2025/3/24 15:45
 * @version: V1.0 <描述当前版本功能>
 */
class WaterFilter(private val wavelength:Float = 16f,
                  private val amplitude:Float = 10f,
                  private val phase:Float = 0f,
                  private val centreX:Float = 0.5f,
                  private val centreY:Float = 0.5f,
                  private var radius:Float = 50f): TransformFilter() {

    private var radius2 = 0f
    private var icentreX = 0f
    private var icentreY = 0f

    init {
        edgeAction = CLAMP
    }

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        icentreX = width * centreX
        icentreY = height * centreY
        if (radius == 0f)
            radius = min(icentreX.toDouble(), icentreY.toDouble()).toFloat()
        radius2 = radius * radius
        return super.doFilter(srcImage, dstImage)
    }

    override fun transformInverse(x: Int, y: Int, out: FloatArray) {
        val dx = x - icentreX
        val dy = y - icentreY
        val distance2 = dx * dx + dy * dy
        if (distance2 > radius2) {
            out[0] = x.toFloat()
            out[1] = y.toFloat()
        } else {
            val distance = sqrt(distance2.toDouble()).toFloat()
            var amount = amplitude * sin(distance / wavelength * TWO_PI - phase)
            amount *= (radius - distance) / radius
            if (distance != 0f) amount *= wavelength / distance
            out[0] = x + dx * amount
            out[1] = y + dy * amount
        }
    }
}