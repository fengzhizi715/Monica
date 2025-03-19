package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransformFilter
import cn.netdiscovery.monica.imageprocess.math.Noise
import kotlin.math.cos
import kotlin.math.sin


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.SwimFilter
 * @author: Tony Shen
 * @date: 2025/3/19 19:52
 * @version: V1.0 <描述当前版本功能>
 */
class SwimFilter(private val scale:Float = 32f, private val stretch:Float = 1.0f,
                 private val angle:Float = 0f, private val amount:Float = 1.0f,
                 private val turbulence:Float = 1.0f, private val time:Float = 0.0f): TransformFilter() {

    private var m00 = 1.0f
    private var m01 = 0.0f
    private var m10 = 0.0f
    private var m11 = 1.0f

    init {
        val cos = cos(angle.toDouble()).toFloat()
        val sin = sin(angle.toDouble()).toFloat()
        m00 = cos
        m01 = sin
        m10 = -sin
        m11 = cos
    }

    override fun transformInverse(x: Int, y: Int, out: FloatArray) {
        var nx = m00 * x + m01 * y
        var ny = m10 * x + m11 * y
        nx /= scale
        ny /= scale * stretch

        if (turbulence == 1.0f) {
            out[0] = x + amount * Noise.noise3(nx + 0.5f, ny, time)
            out[1] = y + amount * Noise.noise3(nx, ny + 0.5f, time)
        } else {
            out[0] = x + amount * Noise.turbulence3(nx + 0.5f, ny, turbulence, time)
            out[1] = y + amount * Noise.turbulence3(nx, ny + 0.5f, turbulence, time)
        }
    }
}