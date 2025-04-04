package cn.netdiscovery.monica.imageprocess.utils

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.utils.ImageUtils
 * @author: Tony Shen
 * @date: 2025/2/21 18:16
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 把 BufferedImage 转换成文件，便于调试时使用
 */
@Throws(IOException::class)
fun writeImageFile(bi: BufferedImage, fileName:String, formatName:String = "png") {
    ImageIO.write(bi, formatName, File(fileName))
}

fun clamp(c: Int): Int {
    return if (c > 255) 255 else if (c < 0) 0 else c
}

fun clamp(x: Int, a: Int, b: Int): Int {
    return if (x < a) a else if (x > b) b else x
}

/**
 * Clamp a value to an interval.
 * @param a the lower clamp threshold
 * @param b the upper clamp threshold
 * @param x the input parameter
 * @return the clamped value
 */
fun clamp(x: Float, a: Float, b: Float): Float {
    return if (x < a) a else if (x > b) b else x
}

fun premultiply(p: IntArray, offset: Int, length: Int) {
    var length = length
    length += offset
    for (i in offset until length) {
        val rgb = p[i]
        val a = (rgb shr 24) and 0xff
        var r = (rgb shr 16) and 0xff
        var g = (rgb shr 8) and 0xff
        var b = rgb and 0xff
        val f = a * (1.0f / 255.0f)
        r = (r * f).toInt()
        g = (g * f).toInt()
        b = (b * f).toInt()
        p[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}

fun unpremultiply(p: IntArray, offset: Int, length: Int) {
    var length = length
    length += offset
    for (i in offset until length) {
        val rgb = p[i]
        val a = (rgb shr 24) and 0xff
        var r = (rgb shr 16) and 0xff
        var g = (rgb shr 8) and 0xff
        var b = rgb and 0xff
        if (a != 0 && a != 255) {
            val f = 255.0f / a
            r = (r * f).toInt()
            g = (g * f).toInt()
            b = (b * f).toInt()
            if (r > 255) r = 255
            if (g > 255) g = 255
            if (b > 255) b = 255
            p[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
    }
}