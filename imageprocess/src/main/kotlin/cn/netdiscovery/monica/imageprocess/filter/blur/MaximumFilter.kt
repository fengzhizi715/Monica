package cn.netdiscovery.monica.imageprocess.filter.blur

import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import java.awt.image.BufferedImage
import java.util.*


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.blur.MaximumFilter
 * @author: Tony Shen
 * @date: 2025/3/24 14:41
 * @version: V1.0 <描述当前版本功能>
 */
class MaximumFilter: ColorProcessorFilter() {

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {
        val numOfPixels = width * height
        val output: Array<ByteArray> = Array(3) { ByteArray(numOfPixels) }

        val size: Int = 1 * 2 + 1
        val total = size * size
        var r = 0
        var g = 0
        var b = 0
        for (row in 0..<height) {
            for (col in 0..<width) {

                // 统计滤波器
                val subpixels = Array(3) { IntArray(total) }
                var index = 0
                for (i in -1..1) {
                    var roffset: Int = row + i
                    roffset = if (roffset < 0) 0 else (if (roffset >= height) height - 1 else roffset)
                    for (j in -1..1) {
                        var coffset: Int = col + j
                        coffset = if (coffset < 0) 0 else (if (coffset >= width) width - 1 else coffset)
                        subpixels[0][index] = R[roffset * width + coffset].toInt() and 0xff
                        subpixels[1][index] = G[roffset * width + coffset].toInt() and 0xff
                        subpixels[2][index] = B[roffset * width + coffset].toInt() and 0xff
                        index++
                    }
                }

                Arrays.sort(subpixels[0])
                Arrays.sort(subpixels[1])
                Arrays.sort(subpixels[2])

                r = subpixels[0][total - 1]
                g = subpixels[1][total - 1]
                b = subpixels[2][total - 1]

                output[0][row * width + col] = r.toByte()
                output[1][row * width + col] = g.toByte()
                output[2][row * width + col] = b.toByte()
            }
        }

        R = output[0]
        G = output[1]
        B = output[2]

        return toBufferedImage(dstImage)
    }

}