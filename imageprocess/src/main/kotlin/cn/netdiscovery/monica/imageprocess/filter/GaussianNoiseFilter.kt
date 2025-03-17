package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.image.BufferedImage
import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.GaussianNoiseFilter
 * @author: Tony Shen
 * @date: 2025/3/17 12:18
 * @version: V1.0 <描述当前版本功能>
 */
class GaussianNoiseFilter(private val sigma:Int = 25): ColorProcessorFilter() {

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {

        var r = 0
        var g = 0
        var b = 0

        val total = width * height
        val random = Random()
        for (i in 0..<total) {
            r = R[i].toInt() and 0xff
            g = G[i].toInt() and 0xff
            b = B[i].toInt() and 0xff

            // add Gaussian noise
            r = (r + sigma * random.nextGaussian()).toInt()
            g = (g + sigma * random.nextGaussian()).toInt()
            b = (b + sigma * random.nextGaussian()).toInt()

            R[i] = clamp(r).toByte()
            G[i] = clamp(g).toByte()
            B[i] = clamp(b).toByte()
        }

        return toBufferedImage(dstImage)
    }
}