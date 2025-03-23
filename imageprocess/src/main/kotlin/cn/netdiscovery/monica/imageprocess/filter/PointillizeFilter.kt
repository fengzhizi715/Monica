package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.math.mixColors
import cn.netdiscovery.monica.imageprocess.math.smoothStep
import cn.netdiscovery.monica.imageprocess.utils.clamp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.PointillizeFilter
 * @author: Tony Shen
 * @date: 2025/3/23 17:59
 * @version: V1.0 <描述当前版本功能>
 */
class PointillizeFilter(private val edgeThickness:Float = 0.4f,
                        private val fuzziness:Float  = 0.1f,
                        override var scale:Float = 16f,
                        override var randomness:Float = 0f,
                        override var gridType:Int = HEXAGONAL): CellularFilter(scale = scale, randomness = randomness, gridType = gridType) {

    private var fadeEdges = false
    private var edgeColor = 0xff000000.toInt()

    override fun getPixel(x: Int, y: Int, inPixels: IntArray, width: Int, height: Int): Int {
        var nx = m00 * x + m01 * y
        var ny = m10 * x + m11 * y
        nx /= scale
        ny /= scale * stretch
        nx += 1000f
        ny += 1000f // Reduce artifacts around 0,0
        var f = evaluate(nx, ny)

        val f1 = results[0]!!.distance
        var srcx: Int = clamp(((results[0]!!.x - 1000) * scale).toInt(), 0, width - 1)
        var srcy: Int = clamp(((results[0]!!.y - 1000) * scale).toInt(), 0, height - 1)
        var v = inPixels[srcy * width + srcx]

        if (fadeEdges) {
            val f2 = results[1]!!.distance
            srcx = clamp(((results[1]!!.x - 1000) * scale).toInt(), 0, width - 1)
            srcy = clamp(((results[1]!!.y - 1000) * scale).toInt(), 0, height - 1)
            val v2 = inPixels[srcy * width + srcx]
            v = mixColors(0.5f * f1 / f2, v, v2)
        } else {
            f = 1 - smoothStep(edgeThickness, edgeThickness + fuzziness, f1)
            v = mixColors(f, edgeColor, v)
        }
        return v
    }

}