package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.imageprocess.math.mod
import cn.netdiscovery.monica.imageprocess.math.smoothStep
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.ColorHalftoneFilter
 * @author: Tony Shen
 * @date: 2025/3/19 13:36
 * @version: V1.0 <描述当前版本功能>
 */
class ColorHalftoneFilter(private val dotRadius:Float = 2f): BaseFilter() {

    private val cyanScreenAngle = Math.toRadians(108.0).toFloat()
    private val magentaScreenAngle = Math.toRadians(162.0).toFloat()
    private val yellowScreenAngle = Math.toRadians(90.0).toFloat()

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        val gridSize = 2 * dotRadius * 1.414f
        val angles = floatArrayOf(cyanScreenAngle, magentaScreenAngle, yellowScreenAngle)
        val mx = floatArrayOf(0f, -1f, 1f, 0f, 0f)
        val my = floatArrayOf(0f, 0f, 0f, -1f, 1f)
        val halfGridSize = gridSize / 2
        val outPixels = IntArray(width)
        val inPixels = getRGB(srcImage, 0, 0, width, height, null)
        for (y in 0..<height) {
            var x = 0
            var ix = y * width
            while (x < width) {
                outPixels[x] = (inPixels[ix] and -0x1000000) or 0xffffff
                x++
                ix++
            }
            for (channel in 0..2) {
                val shift = 16 - 8 * channel
                val mask = 0x000000ff shl shift
                val angle = angles[channel]
                val sin = sin(angle.toDouble()).toFloat()
                val cos = cos(angle.toDouble()).toFloat()

                for (x in 0..<width) {
                    // Transform x,y into halftone screen coordinate space
                    var tx = x * cos + y * sin
                    var ty = -x * sin + y * cos


                    // Find the nearest grid point
                    tx = tx - mod(tx - halfGridSize, gridSize) + halfGridSize
                    ty = ty - mod(ty - halfGridSize, gridSize) + halfGridSize

                    var f = 1f

                    // TODO: Efficiency warning: Because the dots overlap, we need to check neighbouring grid squares.
                    // We check all four neighbours, but in practice only one can ever overlap any given point.
                    for (i in 0..4) {
                        // Find neigbouring grid point
                        val ttx = tx + mx[i] * gridSize
                        val tty = ty + my[i] * gridSize
                        // Transform back into image space
                        val ntx = ttx * cos - tty * sin
                        val nty = ttx * sin + tty * cos
                        // Clamp to the image
                        val nx: Int = clamp(ntx.toInt(), 0, width - 1)
                        val ny: Int = clamp(nty.toInt(), 0, height - 1)
                        val argb = inPixels[ny * width + nx]
                        val nr = (argb shr shift) and 0xff
                        var l = nr / 255.0f
                        l = 1 - l * l
                        l *= (halfGridSize * 1.414).toFloat()
                        val dx = x - ntx
                        val dy = y - nty
                        val dx2 = dx * dx
                        val dy2 = dy * dy
                        val R = sqrt((dx2 + dy2).toDouble()).toFloat()
                        val f2: Float = 1 - smoothStep(R, R + 1, l)
                        f = min(f.toDouble(), f2.toDouble()).toFloat()
                    }

                    var v = (255 * f).toInt()
                    v = v shl shift
                    v = v xor mask.inv()
                    v = v or -0x1000000
                    outPixels[x] = outPixels[x] and v
                }
            }
            setRGB(dstImage, 0, y, width, 1, outPixels)
        }

        return dstImage
    }
}