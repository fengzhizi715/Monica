package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransformFilter
import java.awt.image.BufferedImage



/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.OffsetFilter
 * @author: Tony Shen
 * @date: 2025/3/24 09:53
 * @version: V1.0 <描述当前版本功能>
 */
class OffsetFilter(private var xOffset:Int = 0, private var yOffset:Int = 0, private val wrap:Boolean = true): TransformFilter() {

    init {
        edgeAction = ZERO
    }

    override fun transformInverse(x: Int, y: Int, out: FloatArray) {
        if ( wrap ) {
            out[0] = ((x+width-xOffset) % width).toFloat()
            out[1] = ((y+height-yOffset) % height).toFloat()
        } else {
            out[0] = (x-xOffset).toFloat()
            out[1] = (y-yOffset).toFloat()
        }
    }

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        if (wrap) {
            while (xOffset < 0) xOffset += width
            while (yOffset < 0) yOffset += height
            xOffset %= width
            yOffset %= height
        }
        return super.doFilter(srcImage, dstImage)
    }
}