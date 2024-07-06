package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.IntIntegralImage
import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import java.awt.image.BufferedImage


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.MosaicFilter
 * @author: Tony Shen
 * @date:  2024/7/6 14:51
 * @version: V1.0 <描述当前版本功能>
 */
class MosaicFilter(val r:Int=3): ColorProcessorFilter() {

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {
        val size = (r * 2 + 1) * (r * 2 + 1)
        var tr = 0
        var tg = 0
        var tb = 0
        var output:Array<ByteArray>? = Array(3) { ByteArray(R.size) }

        val rii = IntIntegralImage()
        rii.setImage(R)
        rii.calculate(width, height)

        val gii = IntIntegralImage()
        gii.setImage(G)
        gii.calculate(width, height)

        val bii = IntIntegralImage()
        bii.setImage(B)
        bii.calculate(width, height)

        var x2 = 0
        var y2 = 0
        var x1 = 0
        var y1 = 0
        var index = 0
        for (row in 0 until height) {
            val dy = (row / size)
            y1 = dy * size
            y2 = if ((y1 + size) > height) (height - 1) else (y1 + size)
            index = row * width
            for (col in 0 until width) {
                val dx = (col / size)
                x1 = dx * size
                x2 = if ((x1 + size) > width) (width - 1) else (x1 + size)
                val sr = rii.getBlockSum(x1, y1, x2, y2)
                val sg = gii.getBlockSum(x1, y1, x2, y2)
                val sb = bii.getBlockSum(x1, y1, x2, y2)
                val num = (x2 - x1) * (y2 - y1)
                tr = sr / num
                tg = sg / num
                tb = sb / num
                output!![0][index + col] = tr.toByte()
                output[1][index + col] = tg.toByte()
                output[2][index + col] = tb.toByte()
            }
        }

        setRGB(dstImage, width,height, inPixels, output?.get(0)!!, output[1], output[2])

        output = null

        return dstImage
    }

}
