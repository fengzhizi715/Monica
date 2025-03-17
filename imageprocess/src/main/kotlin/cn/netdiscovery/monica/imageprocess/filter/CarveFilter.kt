package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.CarveFilter
 * @author: Tony Shen
 * @date: 2025/3/17 12:36
 * @version: V1.0 <描述当前版本功能>
 */
class CarveFilter: ColorProcessorFilter()  {

    override fun doColorProcessor(dstImage: BufferedImage): BufferedImage {
        val output = Array(3) { ByteArray(R.size) }

        var index = 0
        for (row in 1..<height - 1) {
            val ta = 0
            var tr = 0
            var tg = 0
            var tb = 0
            for (col in 1..<width - 1) {
                // Index of the pixel in the array
                index = row * width + col
                val bidx = row * width + (col - 1)
                val aidx = row * width + (col + 1)

                val br = R[bidx].toInt() and 0xff
                val bg = G[bidx].toInt() and 0xff
                val bb = B[bidx].toInt() and 0xff

                val ar = R[aidx].toInt() and 0xff
                val ag = G[aidx].toInt() and 0xff
                val ab = B[aidx].toInt() and 0xff

                // calculate new RGB value
                tr = ar - br + 128
                tg = ag - bg + 128
                tb = ab - bb + 128

                output[0][index] = clamp(tr).toByte()
                output[1][index] = clamp(tg).toByte()
                output[2][index] = clamp(tb).toByte()
            }
        }

        R = output[0]
        G = output[1]
        B = output[2]

        return toBufferedImage(dstImage)
    }
}