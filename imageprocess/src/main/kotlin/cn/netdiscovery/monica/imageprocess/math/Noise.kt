package cn.netdiscovery.monica.imageprocess.math

import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.math.Noise
 * @author: Tony Shen
 * @date: 2025/3/10 11:48
 * @version: V1.0 <描述当前版本功能>
 */
/**
 * Perlin Noise functions
 */
class Noise : Function1D, Function2D, Function3D {

    override fun evaluate(x: Float): Float {
        return noise1(x)
    }

    override fun evaluate(x: Float, y: Float): Float {
        return noise2(x, y)
    }

    override fun evaluate(x: Float, y: Float, z: Float): Float {
        return noise3(x, y, z)
    }

    companion object {
        private val randomGenerator: Random = Random()

        /**
         * Compute turbulence using Perlin noise.
         * @param x the x value
         * @param y the y value
         * @param octaves number of octaves of turbulence
         * @return turbulence value at (x,y)
         */
        fun turbulence2(x: Float, y: Float, octaves: Float): Float {
            var t = 0.0f

            var f = 1.0f
            while (f <= octaves) {
                t += abs(noise2(f * x, f * y)) / f
                f *= 2f
            }
            return t
        }

        /**
         * Compute turbulence using Perlin noise.
         * @param x the x value
         * @param y the y value
         * @param octaves number of octaves of turbulence
         * @return turbulence value at (x,y)
         */
        fun turbulence3(x: Float, y: Float, z: Float, octaves: Float): Float {
            var t = 0.0f

            var f = 1.0f
            while (f <= octaves) {
                t += abs(noise3(f * x, f * y, f * z)) / f
                f *= 2f
            }
            return t
        }

        internal const val B = 0x100
        private const val BM = 0xff
        private const val N = 0x1000

        var p: IntArray = IntArray(B + B + 2)
        var g3: Array<FloatArray> = Array(B + B + 2) { FloatArray(3) }
        var g2: Array<FloatArray> = Array(B + B + 2) { FloatArray(2) }
        var g1: FloatArray = FloatArray(B + B + 2)
        var start: Boolean = true

        private fun sCurve(t: Float): Float {
            return t * t * (3.0f - 2.0f * t)
        }

        /**
         * Compute 1-dimensional Perlin noise.
         * @param x the x value
         * @return noise value at x in the range -1..1
         */
        fun noise1(x: Float): Float {
            val bx0: Int
            val rx0: Float
            val v: Float

            if (start) {
                start = false
                init()
            }

            val t = x + N
            bx0 = (t.toInt()) and BM
            val bx1 = (bx0 + 1) and BM
            rx0 = t - t.toInt()
            val rx1 = rx0 - 1.0f

            val sx = sCurve(rx0)

            val u = rx0 * g1[p[bx0]]
            v = rx1 * g1[p[bx1]]
            return 2.3f * lerp(sx, u, v)
        }

        /**
         * Compute 2-dimensional Perlin noise.
         * @param x the x coordinate
         * @param y the y coordinate
         * @return noise value at (x,y)
         */
        fun noise2(x: Float, y: Float): Float {
            val bx0: Int
            val by0: Int
            val b00: Int
            val b10: Int
            val b01: Int
            val b11: Int
            val rx0: Float
            val ry0: Float
            val a: Float
            val b: Float
            var u: Float
            var v: Float
            val j: Int

            if (start) {
                start = false
                init()
            }

            var t = x + N
            bx0 = (t.toInt()) and BM
            val bx1 = (bx0 + 1) and BM
            rx0 = t - t.toInt()
            val rx1 = rx0 - 1.0f

            t = y + N
            by0 = (t.toInt()) and BM
            val by1 = (by0 + 1) and BM
            ry0 = t - t.toInt()
            val ry1 = ry0 - 1.0f

            val i = p[bx0]
            j = p[bx1]

            b00 = p[i + by0]
            b10 = p[j + by0]
            b01 = p[i + by1]
            b11 = p[j + by1]

            val sx = sCurve(rx0)
            val sy = sCurve(ry0)

            var q = g2[b00]
            u = rx0 * q[0] + ry0 * q[1]
            q = g2[b10]
            v = rx1 * q[0] + ry0 * q[1]
            a = lerp(sx, u, v)

            q = g2[b01]
            u = rx0 * q[0] + ry1 * q[1]
            q = g2[b11]
            v = rx1 * q[0] + ry1 * q[1]
            b = lerp(sx, u, v)

            return 1.5f * lerp(sy, a, b)
        }

        /**
         * Compute 3-dimensional Perlin noise.
         * @param x the x coordinate
         * @param y the y coordinate
         * @param y the y coordinate
         * @return noise value at (x,y,z)
         */
        fun noise3(x: Float, y: Float, z: Float): Float {
            val bx0: Int
            val by0: Int
            val bz0: Int
            val b00: Int
            val b10: Int
            val b01: Int
            val b11: Int
            val rx0: Float
            val ry0: Float
            val rz0: Float
            var a: Float
            var b: Float
            val c: Float
            val d: Float
            var u: Float
            var v: Float
            val j: Int

            if (start) {
                start = false
                init()
            }

            var t = x + N
            bx0 = (t.toInt()) and BM
            val bx1 = (bx0 + 1) and BM
            rx0 = t - t.toInt()
            val rx1 = rx0 - 1.0f

            t = y + N
            by0 = (t.toInt()) and BM
            val by1 = (by0 + 1) and BM
            ry0 = t - t.toInt()
            val ry1 = ry0 - 1.0f

            t = z + N
            bz0 = (t.toInt()) and BM
            val bz1 = (bz0 + 1) and BM
            rz0 = t - t.toInt()
            val rz1 = rz0 - 1.0f

            val i = p[bx0]
            j = p[bx1]

            b00 = p[i + by0]
            b10 = p[j + by0]
            b01 = p[i + by1]
            b11 = p[j + by1]

            t = sCurve(rx0)
            val sy = sCurve(ry0)
            val sz = sCurve(rz0)

            var q = g3[b00 + bz0]
            u = rx0 * q[0] + ry0 * q[1] + rz0 * q[2]
            q = g3[b10 + bz0]
            v = rx1 * q[0] + ry0 * q[1] + rz0 * q[2]
            a = lerp(t, u, v)

            q = g3[b01 + bz0]
            u = rx0 * q[0] + ry1 * q[1] + rz0 * q[2]
            q = g3[b11 + bz0]
            v = rx1 * q[0] + ry1 * q[1] + rz0 * q[2]
            b = lerp(t, u, v)

            c = lerp(sy, a, b)

            q = g3[b00 + bz1]
            u = rx0 * q[0] + ry0 * q[1] + rz1 * q[2]
            q = g3[b10 + bz1]
            v = rx1 * q[0] + ry0 * q[1] + rz1 * q[2]
            a = lerp(t, u, v)

            q = g3[b01 + bz1]
            u = rx0 * q[0] + ry1 * q[1] + rz1 * q[2]
            q = g3[b11 + bz1]
            v = rx1 * q[0] + ry1 * q[1] + rz1 * q[2]
            b = lerp(t, u, v)

            d = lerp(sy, a, b)

            return 1.5f * lerp(sz, c, d)
        }

        fun lerp(t: Float, a: Float, b: Float): Float {
            return a + t * (b - a)
        }

        private fun normalize2(v: FloatArray) {
            val s = sqrt((v[0] * v[0] + v[1] * v[1]).toDouble()).toFloat()
            v[0] = v[0] / s
            v[1] = v[1] / s
        }

        fun normalize3(v: FloatArray) {
            val s = sqrt((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]).toDouble()).toFloat()
            v[0] = v[0] / s
            v[1] = v[1] / s
            v[2] = v[2] / s
        }

        private fun random(): Int {
            return randomGenerator.nextInt() and 0x7fffffff
        }

        private fun init() {
            var j: Int
            var k: Int

            var i = 0
            while (i < B) {
                p[i] = i

                g1[i] = ((random() % (B + B)) - B).toFloat() / B

                j = 0
                while (j < 2) {
                    g2[i][j] = ((random() % (B + B)) - B).toFloat() / B
                    j++
                }
                normalize2(g2[i])

                j = 0
                while (j < 3) {
                    g3[i][j] = ((random() % (B + B)) - B).toFloat() / B
                    j++
                }
                normalize3(g3[i])
                i++
            }

            i = B - 1
            while (i >= 0) {
                k = p[i]
                p[i] = p[(random() % B).also { j = it }]
                p[j] = k
                i--
            }

            i = 0
            while (i < B + 2) {
                p[B + i] = p[i]
                g1[B + i] = g1[i]
                j = 0
                while (j < 2) {
                    g2[B + i][j] = g2[i][j]
                    j++
                }
                j = 0
                while (j < 3) {
                    g3[B + i][j] = g3[i][j]
                    j++
                }
                i++
            }
        }

        /**
         * Returns the minimum and maximum of a number of random values
         * of the given function. This is useful for making some stab at
         * normalising the function.
         */
        fun findRange(f: Function1D, minmax: FloatArray?): FloatArray {
            var minmax = minmax
            if (minmax == null) minmax = FloatArray(2)
            var min = 0f
            var max = 0f
            // Some random numbers here...
            var x = -100f
            while (x < 100) {
                val n: Float = f.evaluate(x)
                min = min(min.toDouble(), n.toDouble()).toFloat()
                max = max(max.toDouble(), n.toDouble()).toFloat()
                x += 1.27139.toFloat()
            }
            minmax[0] = min
            minmax[1] = max
            return minmax
        }

        /**
         * Returns the minimum and maximum of a number of random values
         * of the given function. This is useful for making some stab at
         * normalising the function.
         */
        fun findRange(f: Function2D, minmax: FloatArray?): FloatArray {
            var minmax = minmax
            if (minmax == null) minmax = FloatArray(2)
            var min = 0f
            var max = 0f
            // Some random numbers here...
            var y = -100f
            while (y < 100) {
                var x = -100f
                while (x < 100) {
                    val n: Float = f.evaluate(x, y)
                    min = min(min.toDouble(), n.toDouble()).toFloat()
                    max = max(max.toDouble(), n.toDouble()).toFloat()
                    x += 10.77139.toFloat()
                }
                y += 10.35173.toFloat()
            }
            minmax[0] = min
            minmax[1] = max
            return minmax
        }
    }
}