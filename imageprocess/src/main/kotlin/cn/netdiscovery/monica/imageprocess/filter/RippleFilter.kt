package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransformFilter
import cn.netdiscovery.monica.imageprocess.math.Noise
import cn.netdiscovery.monica.imageprocess.math.mod
import cn.netdiscovery.monica.imageprocess.math.triangle
import java.awt.Rectangle
import kotlin.math.sin

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.RippleFilter
 * @author: Tony Shen
 * @date: 2025/3/10 11:06
 * @version: V1.0 <描述当前版本功能>
 */
class RippleFilter(private val xAmplitude:Float = 5.0f, private val yAmplitude:Float = 0.0f,
                   private val xWavelength:Float = 16.0f, private val yWavelength:Float = 16.0f,
                   private val waveType:Int = 0): TransformFilter() {

    /**
     * Sine wave ripples.
     */
    val SINE: Int = 0

    /**
     * Sawtooth wave ripples.
     */
    val SAWTOOTH: Int = 1

    /**
     * Triangle wave ripples.
     */
    val TRIANGLE: Int = 2

    /**
     * Noise ripples.
     */
    val NOISE: Int = 3

    override fun transformSpace(rect: Rectangle) {
        if (edgeAction == ZERO) {
            rect.x -= xAmplitude.toInt()
            rect.width += (2 * xAmplitude).toInt()
            rect.y -= yAmplitude.toInt()
            rect.height += (2 * yAmplitude).toInt()
        }
    }

    override fun transformInverse(x: Int, y: Int, out: FloatArray) {
        val nx = y.toFloat() / xWavelength
        val ny = x.toFloat() / yWavelength
        val fx: Float
        val fy: Float
        when (waveType) {
            SINE -> {
                fx = sin(nx.toDouble()).toFloat()
                fy = sin(ny.toDouble()).toFloat()
            }

            SAWTOOTH -> {
                fx = mod(nx, 1.0f)
                fy = mod(ny, 1.0f)
            }

            TRIANGLE -> {
                fx = triangle(nx)
                fy = triangle(ny)
            }

            NOISE -> {
                fx = Noise.noise1(nx)
                fy = Noise.noise1(ny)
            }

            else -> {
                fx = sin(nx.toDouble()).toFloat()
                fy = sin(ny.toDouble()).toFloat()
            }
        }
        out[0] = x + xAmplitude * fx
        out[1] = y + yAmplitude * fy
    }
}