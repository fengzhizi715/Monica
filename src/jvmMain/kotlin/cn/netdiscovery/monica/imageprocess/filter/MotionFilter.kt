package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.MotionFilter
 * @author: Tony Shen
 * @date: 2024/5/1 10:52
 * @version: V1.0 <描述当前版本功能>
 */
class MotionFilter(private val distance:Float = 0f,private val angle:Float = 0f,private val zoom:Float = 0.4f): BaseFilter() {

    override fun doFilter(image: BufferedImage): BufferedImage {
        var inPixels = IntArray(width * height)
        var outPixels = IntArray(width * height)

        getRGB(image, 0, 0, width, height, inPixels)
        var index = 0
        val cx = width / 2
        val cy = height / 2

        // calculate the triangle geometry value
        val sinAngle = sin(angle / 180.0f * PI).toFloat()
        val coseAngle = cos(angle / 180.0f * PI).toFloat()

        // calculate the distance, same as box blur
        val imageRadius = sqrt((cx * cx + cy * cy).toDouble()).toFloat()
        val maxDistance: Float = distance + imageRadius * zoom

        val iteration = maxDistance.toInt()
        for (row in 0 until height) {
            var ta = 0
            var tr = 0
            var tg = 0
            var tb = 0
            for (col in 0 until width) {
                var newX = col
                var count = 0
                var newY = row

                // iterate the source pixels according to distance
                var m11 = 0.0f
                var m22 = 0.0f
                for (i in 0 until iteration) {
                    newX = col
                    newY = row

                    // calculate the operator source pixel
                    if (distance > 0) {
                        newY = Math.floor((newY + i * sinAngle).toDouble()).toInt()
                        newX = Math.floor((newX + i * coseAngle).toDouble()).toInt()
                    }
                    val f = i.toFloat() / iteration
                    if (newX < 0 || newX >= width) {
                        break
                    }
                    if (newY < 0 || newY >= height) {
                        break
                    }

                    // scale the pixels
                    val scale = 1 - zoom * f
                    m11 = cx - cx * scale
                    m22 = cy - cy * scale
                    newY = (newY * scale + m22).toInt()
                    newX = (newX * scale + m11).toInt()

                    // blur the pixels, here
                    count++
                    val rgb = inPixels[newY * width + newX]
                    ta += rgb shr 24 and 0xff
                    tr += rgb shr 16 and 0xff
                    tg += rgb shr 8 and 0xff
                    tb += rgb and 0xff
                }

                // fill the destination pixel with final RGB value
                if (count == 0) {
                    outPixels[index] = inPixels[index]
                } else {
                    ta = clamp((ta / count))
                    tr = clamp((tr / count))
                    tg = clamp((tg / count))
                    tb = clamp((tb / count))
                    outPixels[index] = ta shl 24 or (tr shl 16) or (tg shl 8) or tb
                }
                index++
            }
        }

        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        setRGB(bufferedImage, 0, 0, width, height, outPixels)
        return bufferedImage
    }
}