package cn.netdiscovery.monica.imageprocess.filter.base

import cn.netdiscovery.monica.imageprocess.BufferedImages
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

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        val size = width * height

        R = ByteArray(size)
        G = ByteArray(size)
        B = ByteArray(size)
        backFillData(inPixels)

        return doColorProcessor(dstImage)
    }

    abstract fun doColorProcessor(dstImage: BufferedImage):BufferedImage

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

    fun setRGB(width: Int, height: Int, pixels: IntArray, R: ByteArray, G: ByteArray, B: ByteArray) {
        val size = width * height
        for (i in 0 until size)
            pixels[i] = -0x1000000 or (R[i].toInt() and 0xff shl 16) or (G[i].toInt() and 0xff shl 8) or (B[i].toInt() and 0xff)
    }

    fun toBitmap(bitmap:BufferedImage ?= null): BufferedImage {
        val pixels = IntArray(width * height)
        val dst = bitmap ?: BufferedImages.create(width,height,type)
        setRGB(width, height, pixels, R, G, B)
        setRGB(dst, 0, 0, width, height, pixels)
        return dst
    }
}