package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.CropFilter
 * @author: Tony Shen
 * @date: 2024/5/5 13:14
 * @version: V1.0 <描述当前版本功能>
 */
class CropFilter(private val x:Int = 0,
                 private val y:Int = 0,
                 private val w:Int = 32,
                 private val h:Int = 32): BaseFilter() {

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val dst = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
        val g: Graphics2D = dst.createGraphics()
        g.drawRenderedImage(srcImage, AffineTransform.getTranslateInstance(-x.toDouble(), -y.toDouble()))
        g.dispose()
        return dst
    }
}