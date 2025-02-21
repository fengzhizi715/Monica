package cn.netdiscovery.monica.imageprocess.filter.blur

import cn.netdiscovery.monica.imageprocess.IntIntegralImage
import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import cn.netdiscovery.monica.imageprocess.utils.clamp
import com.safframework.kotlin.coroutines.asyncInBackground
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.blur.FastBlur2D
 * @author: Tony Shen
 * @date: 2024/6/22 22:35
 * @version: V1.0 <描述当前版本功能>
 */

class FastBlur2D(private val ksize:Int = 5) : ColorProcessorFilter() {

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {

        val radius = ksize / 2

        runBlocking {
            listOf(
                asyncInBackground {
                    var output:ByteArray? = ByteArray(size)
                    val ii = IntIntegralImage()
                    System.arraycopy(R, 0, output, 0, size)
                    ii.setImage(R)
                    ii.calculate(width, height)
                    processSingleChannel(width, height,radius, ii, output!!)
                    System.arraycopy(output, 0, R, 0, size)
                    output = null
                },
                asyncInBackground {
                    var output:ByteArray? = ByteArray(size)
                    val ii = IntIntegralImage()
                    System.arraycopy(G, 0, output, 0, size)
                    ii.setImage(G)
                    ii.calculate(width, height)
                    processSingleChannel(width, height,radius, ii, output!!)
                    System.arraycopy(output, 0, G, 0, size)
                    output = null
                },
                asyncInBackground {
                    var output:ByteArray? = ByteArray(size)
                    val ii = IntIntegralImage()
                    System.arraycopy(B, 0, output, 0, size)
                    ii.setImage(B)
                    ii.calculate(width, height)
                    processSingleChannel(width, height,radius, ii, output!!)
                    System.arraycopy(output, 0, B, 0, size)
                    output = null
                }
            ).awaitAll()
        }

        return toBufferedImage(dstImage)
    }

    private fun processSingleChannel(w: Int, h: Int, radius:Int, ii: IntIntegralImage, output: ByteArray) {
        var x2 = 0
        var y2 = 0
        var x1 = 0
        var y1 = 0
        var cx = 0
        var cy = 0
        for (row in 0 until h + radius) {
            y2 = if (row + 1 > h) h else row + 1
            y1 = if (row - ksize < 0) 0 else row - ksize
            for (col in 0 until w + radius) {
                x2 = if (col + 1 > w) w else col + 1
                x1 = if (col - ksize < 0) 0 else col - ksize
                cx = if (col - radius < 0) 0 else col - radius
                cy = if (row - radius < 0) 0 else row - radius
                val num = (x2 - x1) * (y2 - y1)
                val s = ii.getBlockSum(x1, y1, x2, y2)
                output[cy * w + cx] = clamp(s / num).toByte()
            }
        }
    }
}