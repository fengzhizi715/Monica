package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransformFilter
import cn.netdiscovery.monica.imageprocess.math.Noise
import cn.netdiscovery.monica.imageprocess.math.TWO_PI
import cn.netdiscovery.monica.imageprocess.utils.clamp
import kotlin.math.cos
import kotlin.math.sin


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.MarbleFilter
 * @author: Tony Shen
 * @date: 2025/3/11 14:35
 * @version: V1.0 <描述当前版本功能>
 */
class MarbleFilter(private val xScale:Float = 4f, private val yScale:Float = 4f, private val turbulence:Float = 1f): TransformFilter() {

    private lateinit var sinTable: FloatArray
    private lateinit var cosTable: FloatArray

    init {
        edgeAction = CLAMP
        initialize()
    }

    private fun initialize() {
        sinTable = FloatArray(256)
        cosTable = FloatArray(256)
        for (i in 0..255) {
            val angle: Float = TWO_PI * i / 256f * turbulence
            sinTable[i] = (-yScale * sin(angle.toDouble())).toFloat()
            cosTable[i] = (yScale * cos(angle.toDouble())).toFloat()
        }
    }

    override fun transformInverse(x: Int, y: Int, out: FloatArray) {
        val displacement: Int = displacementMap(x, y)
        out[0] = x + sinTable[displacement]
        out[1] = y + cosTable[displacement]
    }

    private fun displacementMap(x: Int, y: Int): Int {
        return clamp((127 * (1 + Noise.noise2(x / xScale, y / xScale))).toInt())
    }
}