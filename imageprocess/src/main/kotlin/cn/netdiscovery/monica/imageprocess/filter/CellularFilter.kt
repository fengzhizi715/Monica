package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.Colormap
import cn.netdiscovery.monica.imageprocess.domain.Gradient
import cn.netdiscovery.monica.imageprocess.filter.base.WholeImageFilter
import cn.netdiscovery.monica.imageprocess.math.Function2D
import cn.netdiscovery.monica.imageprocess.math.Noise
import cn.netdiscovery.monica.imageprocess.math.mixColors
import cn.netdiscovery.monica.imageprocess.math.smoothStep
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.Rectangle
import java.util.*
import kotlin.math.*


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.CellularFilter
 * @author: Tony Shen
 * @date:  2025/3/22 14:53
 * @version: V1.0 <描述当前版本功能>
 */
open class CellularFilter : WholeImageFilter(), Function2D, Cloneable {

    companion object {
        private var probabilities: ByteArray? = null
        const val RANDOM: Int = 0
        const val SQUARE: Int = 1
        const val HEXAGONAL: Int = 2
        const val OCTAGONAL: Int = 3
        const val TRIANGULAR: Int = 4
    }

    protected var scale: Float = 32f
    protected var stretch: Float = 1.0f
    protected var angle: Float = 0.0f
    var amount: Float = 1.0f
    var turbulence: Float = 1.0f
    var gain: Float = 0.5f
    var bias: Float = 0.5f
    var distancePower: Float = 2f
    var useColor: Boolean = false
    protected var colormap: Colormap = Gradient()
    protected var coefficients: FloatArray = floatArrayOf(1f, 0f, 0f, 0f)
    protected var angleCoefficient: Float = 0f
    protected var random: Random = Random()
    protected var m00: Float = 1.0f
    protected var m01: Float = 0.0f
    protected var m10: Float = 0.0f
    protected var m11: Float = 1.0f
    protected var results: Array<Point?>
    protected var randomness: Float = 0f
    protected var gridType: Int = HEXAGONAL
    private val min = 0f
    private val max = 0f
    private var gradientCoefficient = 0f

    init {
        results = arrayOfNulls(3)
        for (j in results.indices) results[j] = Point()
        if (probabilities == null) {
            probabilities = ByteArray(8192)
            var factorial = 1f
            var total = 0f
            val mean = 2.5f
            for (i in 0..9) {
                if (i > 1) factorial *= i.toFloat()
                val probability = mean.pow(i) * exp(-mean.toDouble()).toFloat() / factorial
                val start = (total * 8192).toInt()
                total += probability
                val end = (total * 8192).toInt()
                for (j in start until end) probabilities!![j] = i.toByte()
            }
        }
    }

//    /**
//     * Specifies the angle of the texture.
//     * @param angle the angle of the texture.
//     * @angle
//     * @see .getAngle
//     */
//    fun setAngle(angle: Float) {
//        this.angle = angle
//        val cos = cos(angle.toDouble()).toFloat()
//        val sin = sin(angle.toDouble()).toFloat()
//        m00 = cos
//        m01 = sin
//        m10 = -sin
//        m11 = cos
//    }

    inner class Point {
        var index: Int = 0
        var x: Float = 0f
        var y: Float = 0f
        var dx: Float = 0f
        var dy: Float = 0f
        var cubeX: Float = 0f
        var cubeY: Float = 0f
        var distance: Float = 0f
    }

    private fun checkCube(x: Float, y: Float, cubeX: Int, cubeY: Int, results: Array<Point?>?): Float {
        random.setSeed((571 * cubeX + 23 * cubeY).toLong())
        val numPoints = when (gridType) {
            RANDOM -> probabilities!![random.nextInt() and 0x1fff].toInt()
            SQUARE -> 1
            HEXAGONAL -> 1
            OCTAGONAL -> 2
            TRIANGULAR -> 2
            else -> probabilities!![random.nextInt() and 0x1fff].toInt()
        }
        for (i in 0 until numPoints) {
            var px = 0f
            var py = 0f
            var weight = 1.0f
            when (gridType) {
                RANDOM -> {
                    px = random.nextFloat()
                    py = random.nextFloat()
                }

                SQUARE -> {
                    py = 0.5f
                    px = py
                    if (randomness != 0f) {
                        px += (randomness * (random.nextFloat() - 0.5)).toFloat()
                        py += (randomness * (random.nextFloat() - 0.5)).toFloat()
                    }
                }

                HEXAGONAL -> {
                    if ((cubeX and 1) == 0) {
                        px = 0.75f
                        py = 0f
                    } else {
                        px = 0.75f
                        py = 0.5f
                    }
                    if (randomness != 0f) {
                        px += randomness * Noise.noise2(271 * (cubeX + px), 271 * (cubeY + py))
                        py += randomness * Noise.noise2(271 * (cubeX + px) + 89, 271 * (cubeY + py) + 137)
                    }
                }

                OCTAGONAL -> {
                    when (i) {
                        0 -> {
                            px = 0.207f
                            py = 0.207f
                        }

                        1 -> {
                            px = 0.707f
                            py = 0.707f
                            weight = 1.6f
                        }
                    }
                    if (randomness != 0f) {
                        px += randomness * Noise.noise2(271 * (cubeX + px), 271 * (cubeY + py))
                        py += randomness * Noise.noise2(271 * (cubeX + px) + 89, 271 * (cubeY + py) + 137)
                    }
                }

                TRIANGULAR -> {
                    if ((cubeY and 1) == 0) {
                        if (i == 0) {
                            px = 0.25f
                            py = 0.35f
                        } else {
                            px = 0.75f
                            py = 0.65f
                        }
                    } else {
                        if (i == 0) {
                            px = 0.75f
                            py = 0.35f
                        } else {
                            px = 0.25f
                            py = 0.65f
                        }
                    }
                    if (randomness != 0f) {
                        px += randomness * Noise.noise2(271 * (cubeX + px), 271 * (cubeY + py))
                        py += randomness * Noise.noise2(271 * (cubeX + px) + 89, 271 * (cubeY + py) + 137)
                    }
                }
            }
            var dx = abs((x - px).toDouble()).toFloat()
            var dy = abs((y - py).toDouble()).toFloat()
            dx *= weight
            dy *= weight
            var d = if (distancePower == 1.0f) dx + dy
            else if (distancePower == 2.0f) sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            else (dx.pow(distancePower) + dy.pow(distancePower)).pow((1 / distancePower))

            // Insertion sort the long way round to speed it up a bit
            if (d < results!![0]!!.distance) {
                val p = results[2]
                results[2] = results[1]
                results[1] = results[0]
                results[0] = p
                p!!.distance = d
                p.dx = dx
                p.dy = dy
                p.x = cubeX + px
                p.y = cubeY + py
            } else if (d < results[1]!!.distance) {
                val p = results[2]
                results[2] = results[1]
                results[1] = p
                p!!.distance = d
                p.dx = dx
                p.dy = dy
                p.x = cubeX + px
                p.y = cubeY + py
            } else if (d < results[2]!!.distance) {
                val p = results[2]
                p!!.distance = d
                p.dx = dx
                p.dy = dy
                p.x = cubeX + px
                p.y = cubeY + py
            }
        }
        return results!![2]!!.distance
    }

    override fun evaluate(x: Float, y: Float): Float {
        for (j in results.indices)
            results[j]!!.distance = Float.POSITIVE_INFINITY

        val ix = x.toInt()
        val iy = y.toInt()
        val fx = x - ix
        val fy = y - iy

        var d = checkCube(fx, fy, ix, iy, results)
        if (d > fy) d = checkCube(fx, fy + 1, ix, iy - 1, results)
        if (d > 1 - fy) d = checkCube(fx, fy - 1, ix, iy + 1, results)
        if (d > fx) {
            checkCube(fx + 1, fy, ix - 1, iy, results)
            if (d > fy) d = checkCube(fx + 1, fy + 1, ix - 1, iy - 1, results)
            if (d > 1 - fy) d = checkCube(fx + 1, fy - 1, ix - 1, iy + 1, results)
        }
        if (d > 1 - fx) {
            d = checkCube(fx - 1, fy, ix + 1, iy, results)
            if (d > fy) d = checkCube(fx - 1, fy + 1, ix + 1, iy - 1, results)
            if (d > 1 - fy) d = checkCube(fx - 1, fy - 1, ix + 1, iy + 1, results)
        }

        var t = 0f
        for (i in 0..2) t += coefficients[i] * results!![i]!!.distance
        if (angleCoefficient != 0f) {
            var angle =
                atan2((y - results!![0]!!.y).toDouble(), (x - results!![0]!!.x).toDouble()).toFloat()
            if (angle < 0) angle += 2 * Math.PI.toFloat()
            angle /= 4 * Math.PI.toFloat()
            t += angleCoefficient * angle
        }
        if (gradientCoefficient != 0f) {
            val a = 1 / (results!![0]!!.dy + results!![0]!!.dx)
            t += gradientCoefficient * a
        }
        return t
    }

    private fun turbulence2(x: Float, y: Float, freq: Float): Float {
        var t = 0.0f

        var f = 1.0f
        while (f <= freq) {
            t += evaluate(f * x, f * y) / f
            f *= 2f
        }
        return t
    }

    open fun getPixel(x: Int, y: Int, inPixels: IntArray, width: Int, height: Int): Int {
        var nx = m00 * x + m01 * y
        var ny = m10 * x + m11 * y
        nx /= scale
        ny /= scale * stretch
        nx += 1000f
        ny += 1000f // Reduce artifacts around 0,0
        var f = if (turbulence == 1.0f) evaluate(nx, ny) else turbulence2(nx, ny, turbulence)
        // Normalize to 0..1
//		f = (f-min)/(max-min);
        f *= 2f
        f *= amount
        val a = -0x1000000
        var v: Int
        if (colormap != null) {
            v = colormap.getColor(f)
            if (useColor) {
                val srcx: Int = clamp(((results!![0]!!.x - 1000) * scale).toInt(), 0, width - 1)
                val srcy: Int = clamp(((results!![0]!!.y - 1000) * scale).toInt(), 0, height - 1)
                v = inPixels[srcy * width + srcx]
                f =
                    (results!![1]!!.distance - results!![0]!!.distance) / (results!![1]!!.distance + results!![0]!!.distance)
                f = smoothStep(coefficients[1], coefficients[0], f)
                v = mixColors(f, -0x1000000, v)
            }
            return v
        } else {
            v = clamp((f * 255).toInt())
            val r = v shl 16
            val g = v shl 8
            val b = v
            return a or r or g or b
        }
    }

    override fun filterPixels(width: Int, height: Int, inPixels: IntArray, transformedSpace: Rectangle): IntArray {
//		float[] minmax = Noise.findRange(this, null);
//		min = minmax[0];
//		max = minmax[1];

        var index = 0
        val outPixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                outPixels[index++] = getPixel(x, y, inPixels, width, height)
            }
        }
        return outPixels
    }

    public override fun clone(): Any {
        val f = super.clone() as CellularFilter
        f.coefficients = coefficients.clone()
        f.results = results.clone()
        f.random = Random()
        //		if (colormap != null)
//			f.colormap = (Colormap)colormap.clone();
        return f
    }
}