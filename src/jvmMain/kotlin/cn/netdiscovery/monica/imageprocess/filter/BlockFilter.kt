package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import java.awt.image.BufferedImage


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.BlockFilter
 * @author: Tony Shen
 * @date: 2024/5/4 23:35
 * @version: V1.0 <描述当前版本功能>
 */
class BlockFilter(private val blockSize:Int =2): BaseFilter() {

    override fun doFilter(image: BufferedImage): BufferedImage {
        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

        val pixels = IntArray(blockSize * blockSize)

        for (y in 0 until height step blockSize){
            for (x in 0 until width step blockSize){
                val w = Math.min(blockSize, width - x)
                val h = Math.min(blockSize, height - y)
                val t = w * h
                getRGB(image, x, y, w, h, pixels)
                var r = 0
                var g = 0
                var b = 0
                var argb: Int
                var i = 0
                for (by in 0 until h) {
                    for (bx in 0 until w) {
                        argb = pixels[i]
                        r += argb shr 16 and 0xff
                        g += argb shr 8 and 0xff
                        b += argb and 0xff
                        i++
                    }
                }
                argb = r / t shl 16 or (g / t shl 8) or b / t
                i = 0
                for (by in 0 until h) {
                    for (bx in 0 until w) {
                        pixels[i] = pixels[i] and -0x1000000 or argb
                        i++
                    }
                }
                setRGB(bufferedImage, x, y, w, h, pixels)
            }
        }

        return bufferedImage
    }
}