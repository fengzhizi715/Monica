package cn.netdiscovery.monica.imageprocess.filter.base

import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.base.ColorProcessorFilter
 * @author: Tony Shen
 * @date: 2024/5/8 19:57
 * @version: V1.0 <描述当前版本功能>
 */
open abstract class ColorProcessorFilter:BaseFilter() {

    protected lateinit var R: ByteArray
    protected lateinit var G: ByteArray
    protected lateinit var B: ByteArray
    protected lateinit var inPixels: IntArray

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val size = width * height
        inPixels = IntArray(size)
        getRGB(srcImage, 0, 0, width, height, inPixels)

        R = ByteArray(size)
        G = ByteArray(size)
        B = ByteArray(size)
        backFillData(inPixels)

        return doColorProcessor(dstImage)
    }

    abstract fun doColorProcessor(dstImage: BufferedImage):BufferedImage

    fun getRed() = R

    fun getGreen() = G

    fun getBlue() = B

    private fun backFillData(input: IntArray) {
        var c = 0
        var r = 0
        var g = 0
        var b = 0
        val length = input.size
        for (i in 0 until length) {
            c = input[i]
            r = c and 0xff0000 shr 16
            g = c and 0xff00 shr 8
            b = c and 0xff
            R[i] = r.toByte()
            G[i] = g.toByte()
            B[i] = b.toByte()
        }
    }
}