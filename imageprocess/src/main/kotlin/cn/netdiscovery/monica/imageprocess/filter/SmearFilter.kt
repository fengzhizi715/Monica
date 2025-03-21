package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.WholeImageFilter
import cn.netdiscovery.monica.imageprocess.math.mixColors
import java.awt.Rectangle
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.SmearFilter
 * @author: Tony Shen
 * @date: 2025/3/21 16:27
 * @version: V1.0 <描述当前版本功能>
 */
class SmearFilter(private var angle:Float = 0f,
        private var density:Float = 0.5f,
        private var distance:Int = 8,
        private var shape: Int = CIRCLES,
        private var mix:Float = 0.5f): WholeImageFilter() {

    companion object {
        val CROSSES: Int = 0
        val LINES:   Int = 1
        val CIRCLES: Int = 2
        val SQUARES: Int = 3
    }

    private var seed: Long = 567
    private var randomGenerator = Random()
    private var background = false

    override fun filterPixels(width: Int, height: Int, inPixels: IntArray, transformedSpace: Rectangle): IntArray {
        val outPixels = IntArray(width * height)

        randomGenerator.setSeed(seed)
        val sinAngle = sin(angle.toDouble()).toFloat()
        val cosAngle = cos(angle.toDouble()).toFloat()

        var i = 0
        val numShapes: Int

        for (y in 0..<height) for (x in 0..<width) {
            outPixels[i] = if (background) -0x1 else inPixels[i]
            i++
        }

        when (shape) {
            CROSSES -> {
                //Crosses
                numShapes = (2 * density * width * height / (distance + 1)).toInt()
                i = 0
                while (i < numShapes) {
                    val x = (randomGenerator.nextInt() and 0x7fffffff) % width
                    val y = (randomGenerator.nextInt() and 0x7fffffff) % height
                    val length = randomGenerator.nextInt() % distance + 1
                    val rgb = inPixels[y * width + x]
                    var x1 = x - length
                    while (x1 < x + length + 1) {
                        if (x1 >= 0 && x1 < width) {
                            val rgb2 = if (background) -0x1 else outPixels[y * width + x1]
                            outPixels[y * width + x1] = mixColors(mix, rgb2, rgb)
                        }
                        x1++
                    }
                    var y1 = y - length
                    while (y1 < y + length + 1) {
                        if (y1 >= 0 && y1 < height) {
                            val rgb2 = if (background) -0x1 else outPixels[y1 * width + x]
                            outPixels[y1 * width + x] = mixColors(mix, rgb2, rgb)
                        }
                        y1++
                    }
                    i++
                }
            }

            LINES -> {
                numShapes = (2 * density * width * height / 2).toInt()

                i = 0
                while (i < numShapes) {
                    val sx = (randomGenerator.nextInt() and 0x7fffffff) % width
                    val sy = (randomGenerator.nextInt() and 0x7fffffff) % height
                    val rgb = inPixels[sy * width + sx]
                    val length = (randomGenerator.nextInt() and 0x7fffffff) % distance
                    var dx = (length * cosAngle).toInt()
                    var dy = (length * sinAngle).toInt()

                    val x0 = sx - dx
                    val y0 = sy - dy
                    val x1 = sx + dx
                    val y1 = sy + dy
                    var d: Int
                    val incrE: Int
                    val incrNE: Int

                    val ddx = if (x1 < x0) -1
                    else 1
                    val ddy = if (y1 < y0) -1
                    else 1
                    dx = x1 - x0
                    dy = y1 - y0
                    dx = abs(dx.toDouble()).toInt()
                    dy = abs(dy.toDouble()).toInt()
                    var x = x0
                    var y = y0

                    if (x < width && x >= 0 && y < height && y >= 0) {
                        val rgb2 = if (background) -0x1 else outPixels[y * width + x]
                        outPixels[y * width + x] = mixColors(mix, rgb2, rgb)
                    }
                    if (abs(dx.toDouble()) > abs(dy.toDouble())) {
                        d = 2 * dy - dx
                        incrE = 2 * dy
                        incrNE = 2 * (dy - dx)

                        while (x != x1) {
                            if (d <= 0) d += incrE
                            else {
                                d += incrNE
                                y += ddy
                            }
                            x += ddx
                            if (x < width && x >= 0 && y < height && y >= 0) {
                                val rgb2 = if (background) -0x1 else outPixels[y * width + x]
                                outPixels[y * width + x] = mixColors(mix, rgb2, rgb)
                            }
                        }
                    } else {
                        d = 2 * dx - dy
                        incrE = 2 * dx
                        incrNE = 2 * (dx - dy)

                        while (y != y1) {
                            if (d <= 0) d += incrE
                            else {
                                d += incrNE
                                x += ddx
                            }
                            y += ddy
                            if (x < width && x >= 0 && y < height && y >= 0) {
                                val rgb2 = if (background) -0x1 else outPixels[y * width + x]
                                outPixels[y * width + x] = mixColors(mix, rgb2, rgb)
                            }
                        }
                    }
                    i++
                }
            }

            SQUARES, CIRCLES -> {
                val radius = distance + 1
                val radius2 = radius * radius
                numShapes = (2 * density * width * height / radius).toInt()
                i = 0
                while (i < numShapes) {
                    val sx = (randomGenerator.nextInt() and 0x7fffffff) % width
                    val sy = (randomGenerator.nextInt() and 0x7fffffff) % height
                    val rgb = inPixels[sy * width + sx]
                    var x = sx - radius
                    while (x < sx + radius + 1) {
                        var y = sy - radius
                        while (y < sy + radius + 1) {
                            val f = if (shape === CIRCLES) (x - sx) * (x - sx) + (y - sy) * (y - sy)
                            else 0
                            if (x >= 0 && x < width && y >= 0 && y < height && f <= radius2) {
                                val rgb2 = if (background) -0x1 else outPixels[y * width + x]
                                outPixels[y * width + x] = mixColors(mix, rgb2, rgb)
                            }
                            y++
                        }
                        x++
                    }
                    i++
                }
            }
        }

        return outPixels
    }
}