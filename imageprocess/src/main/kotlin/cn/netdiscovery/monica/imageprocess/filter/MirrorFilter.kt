package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import java.awt.*
import java.awt.image.BufferedImage


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.MirrorFilter
 * @author: Tony Shen
 * @date: 2025/3/18 20:25
 * @version: V1.0 <描述当前版本功能>
 */
class MirrorFilter(private val opacity:Float = 1.0f, private val centreY:Float = 0.5f, private val gap:Float = 0f): BaseFilter() {

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val h = (centreY * height).toInt()
        val d = (gap * height).toInt()

        val g: Graphics2D = dstImage.createGraphics()
//        val clip: Shape = g.clip
        g.clipRect(0, 0, width, h)
        g.drawRenderedImage(srcImage, null)
//        g.clip = clip
        g.clipRect(0, h + d, width, height - h - d)
        g.translate(0, 2 * h + d)
        g.scale(1.0, -1.0)
        g.drawRenderedImage(srcImage, null)
        g.paint = GradientPaint(0f, 0f, Color(1.0f, 0.0f, 0.0f, 0.0f), 0f, h.toFloat(), Color(0.0f, 1.0f, 0.0f, opacity))
        g.composite = AlphaComposite.getInstance(AlphaComposite.DST_IN)
        g.fillRect(0, 0, width, h)
//        g.clip = clip
        g.dispose()

        return dstImage
    }
}