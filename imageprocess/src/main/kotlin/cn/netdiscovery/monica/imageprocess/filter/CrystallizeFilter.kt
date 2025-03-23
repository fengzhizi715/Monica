package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.math.mixColors
import cn.netdiscovery.monica.imageprocess.math.smoothStep
import cn.netdiscovery.monica.imageprocess.utils.clamp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.CrystallizeFilter
 * @author: Tony Shen
 * @date:  2025/3/22 16:50
 * @version: V1.0 <描述当前版本功能>
 */
class CrystallizeFilter(private val edgeThickness:Float = 0.4f,
                        override var scale:Float = 16f,
                        override var randomness:Float = 0f,
                        override var gridType:Int = HEXAGONAL) : CellularFilter(scale = scale, gridType = gridType) {

    private var fadeEdges = false
    private var edgeColor = 0xff000000.toInt()

    override fun getPixel(x: Int, y: Int, inPixels: IntArray, width: Int, height: Int): Int {
        var nx: Float = m00 * x + m01 * y
        var ny: Float = m10 * x + m11 * y
        nx /= scale
        ny /= scale * stretch
        nx += 1000f
        ny += 1000f // Reduce artifacts around 0,0
        var f: Float = evaluate(nx, ny)

        val f1: Float = results.get(0)!!.distance
        val f2: Float = results.get(1)!!.distance
        var srcx: Int = clamp(((results.get(0)!!.x - 1000) * scale).toInt(), 0, width - 1)
        var srcy: Int = clamp(((results.get(0)!!.y - 1000) * scale).toInt(), 0, height - 1)
        var v = inPixels[srcy * width + srcx]
        f = (f2 - f1) / edgeThickness
        f = smoothStep(0f, edgeThickness, f)
        if (fadeEdges) {
            srcx = clamp(((results.get(1)!!.x - 1000) * scale).toInt(), 0, width - 1)
            srcy = clamp(((results.get(1)!!.y - 1000) * scale).toInt(), 0, height - 1)
            var v2 = inPixels[srcy * width + srcx]
            v2 = mixColors(0.5f, v2, v)
            v = mixColors(f, v2, v)
        } else v = mixColors(f, edgeColor, v)
        return v
    }
}