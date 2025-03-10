package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransformFilter
import cn.netdiscovery.monica.imageprocess.math.TWO_PI
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.DiffuseFilter
 * @author: Tony Shen
 * @date:  2025/3/8 16:05
 * @version: V1.0 <描述当前版本功能>
 */
class DiffuseFilter(private val scale: Float = 4f): TransformFilter() {

    private lateinit var sinTable: FloatArray
    private lateinit var cosTable: FloatArray

    init {
        edgeAction = CLAMP
    }

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        sinTable = FloatArray(256)
        cosTable = FloatArray(256)
        for (i in 0..255) {
            val angle: Float = TWO_PI * i / 256f
            sinTable[i] = (scale * sin(angle.toDouble())).toFloat()
            cosTable[i]= (scale * cos(angle.toDouble())).toFloat()
        }
        return super.doFilter(srcImage, dstImage)
    }

    override fun transformInverse(x: Int, y: Int, out: FloatArray) {
        val angle = (Math.random() * 255).toInt()
        val distance = Math.random().toFloat()
        out[0] = x + distance * sinTable[angle]
        out[1] = y + distance * cosTable.get(angle)
    }
}