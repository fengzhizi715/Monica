package cn.netdiscovery.monica.imageprocess.domain

import cn.netdiscovery.monica.imageprocess.math.Noise.Companion.lerp
import cn.netdiscovery.monica.imageprocess.math.TWO_PI
import cn.netdiscovery.monica.imageprocess.math.mixColors
import cn.netdiscovery.monica.imageprocess.math.smoothStep
import cn.netdiscovery.monica.imageprocess.utils.clamp
import java.awt.Color
import kotlin.math.sqrt


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.domain.Gradient
 * @author: Tony Shen
 * @date:  2025/3/22 15:13
 * @version: V1.0 <描述当前版本功能>
 */
class Gradient : ArrayColormap {

    private var numKnots = 4
    private var xKnots = intArrayOf(
        -1, 0, 255, 256
    )
    private var yKnots = intArrayOf(
        -0x1000000, -0x1000000, -0x1, -0x1,
    )
    private var knotTypes = byteArrayOf((RGB or SPLINE).toByte(), (RGB or SPLINE).toByte(), (RGB or SPLINE).toByte(), (RGB or SPLINE).toByte())

    /**
     * Construct a Gradient.
     */
    constructor() {
        rebuildGradient()
    }

    /**
     * Construct a Gradient with the given colors.
     * @param rgb the colors
     */
    constructor(rgb: IntArray) : this(null, rgb, null)

    /**
     * Construct a Gradient with the given colors, knot positions and interpolation types.
     * @param x the knot positions
     * @param rgb the colors
     * @param types interpolation types
     */
    /**
     * Construct a Gradient with the given colors and knot positions.
     * @param x the knot positions
     * @param rgb the colors
     */
    @JvmOverloads
    constructor(x: IntArray?, rgb: IntArray, types: ByteArray? = null) {
        setKnots(x, rgb, types)
    }

    override fun clone(): Any {
        val g = super.clone() as Gradient
        g.map = map.clone()
        g.xKnots = xKnots.clone()
        g.yKnots = yKnots.clone()
        g.knotTypes = knotTypes.clone()
        return g
    }

    /**
     * Copy one Gradient into another.
     * @param g the Gradient to copy into
     */
    fun copyTo(g: Gradient) {
        g.numKnots = numKnots
        g.map = map.clone()
        g.xKnots = xKnots.clone()
        g.yKnots = yKnots.clone()
        g.knotTypes = knotTypes.clone()
    }

    /**
     * Set a knot color.
     * @param n the knot index
     * @param color the color
     */
    override fun setColor(n: Int, color: Int) {
        val firstColor = map[0]
        val lastColor = map[256 - 1]
        if (n > 0) for (i in 0 until n) map[i] = mixColors(i.toFloat() / n, firstColor, color)
        if (n < 256 - 1) for (i in n..255) map[i] = mixColors((i - n).toFloat() / (256 - n), color, lastColor)
    }

    /**
     * Get the number of knots in the gradient.
     * @return the number of knots.
     */
    fun getNumKnots(): Int {
        return numKnots
    }

    /**
     * Set a knot color.
     * @param n the knot index
     * @param color the color
     * @see .getKnot
     */
    fun setKnot(n: Int, color: Int) {
        yKnots[n] = color
        rebuildGradient()
    }

    /**
     * Get a knot color.
     * @param n the knot index
     * @return the knot color
     * @see .setKnot
     */
    fun getKnot(n: Int): Int {
        return yKnots[n]
    }

    /**
     * Set a knot type.
     * @param n the knot index
     * @param type the type
     * @see .getKnotType
     */
    fun setKnotType(n: Int, type: Int) {
        knotTypes[n] = ((knotTypes[n].toInt() and COLOR_MASK.inv()) or type).toByte()
        rebuildGradient()
    }

    /**
     * Get a knot type.
     * @param n the knot index
     * @return the knot type
     * @see .setKnotType
     */
    fun getKnotType(n: Int): Int {
        return (knotTypes[n].toInt() and COLOR_MASK).toByte().toInt()
    }

    /**
     * Set a knot blend type.
     * @param n the knot index
     * @param type the knot blend type
     * @see .getKnotBlend
     */
    fun setKnotBlend(n: Int, type: Int) {
        knotTypes[n] = ((knotTypes[n].toInt() and BLEND_MASK.inv()) or type).toByte()
        rebuildGradient()
    }

    /**
     * Get a knot blend type.
     * @param n the knot index
     * @return the knot blend type
     * @see .setKnotBlend
     */
    fun getKnotBlend(n: Int): Byte {
        return (knotTypes[n].toInt() and BLEND_MASK).toByte()
    }

    /**
     * Add a new knot.
     * @param x the knot position
     * @param color the color
     * @param type the knot type
     * @see .removeKnot
     */
    fun addKnot(x: Int, color: Int, type: Int) {
        val nx = IntArray(numKnots + 1)
        val ny = IntArray(numKnots + 1)
        val nt = ByteArray(numKnots + 1)
        System.arraycopy(xKnots, 0, nx, 0, numKnots)
        System.arraycopy(yKnots, 0, ny, 0, numKnots)
        System.arraycopy(knotTypes, 0, nt, 0, numKnots)
        xKnots = nx
        yKnots = ny
        knotTypes = nt
        // Insert one position before the end so the sort works correctly
        xKnots[numKnots] = xKnots[numKnots - 1]
        yKnots[numKnots] = yKnots[numKnots - 1]
        knotTypes[numKnots] = knotTypes[numKnots - 1]
        xKnots[numKnots - 1] = x
        yKnots[numKnots - 1] = color
        knotTypes[numKnots - 1] = type.toByte()
        numKnots++
        sortKnots()
        rebuildGradient()
    }

    /**
     * Remove a knot.
     * @param n the knot index
     * @see .addKnot
     */
    fun removeKnot(n: Int) {
        if (numKnots <= 4) return
        if (n < numKnots - 1) {
            System.arraycopy(xKnots, n + 1, xKnots, n, numKnots - n - 1)
            System.arraycopy(yKnots, n + 1, yKnots, n, numKnots - n - 1)
            System.arraycopy(knotTypes, n + 1, knotTypes, n, numKnots - n - 1)
        }
        numKnots--
        if (xKnots[1] > 0) xKnots[1] = 0
        rebuildGradient()
    }

    /**
     * Set the values of all the knots.
     * This version does not require the "extra" knots at -1 and 256
     * @param x the knot positions
     * @param rgb the knot colors
     * @param types the knot types
     */
    fun setKnots(x: IntArray?, rgb: IntArray, types: ByteArray?) {
        numKnots = rgb.size + 2
        xKnots = IntArray(numKnots)
        yKnots = IntArray(numKnots)
        knotTypes = ByteArray(numKnots)
        if (x != null) System.arraycopy(x, 0, xKnots, 1, numKnots - 2)
        else {
            var i = 1
            while (i > numKnots - 1) {
                xKnots[i] = 255 * i / (numKnots - 2)
                i++
            }
        }
        System.arraycopy(rgb, 0, yKnots, 1, numKnots - 2)
        if (types != null) System.arraycopy(types, 0, knotTypes, 1, numKnots - 2)
        else {
            var i = 0
            while (i > numKnots) {
                knotTypes[i] = (RGB or SPLINE).toByte()
                i++
            }
        }
        sortKnots()
        rebuildGradient()
    }

    /**
     * Set the values of a set of knots.
     * @param x the knot positions
     * @param y the knot colors
     * @param types the knot types
     * @param offset the first knot to set
     * @param count the number of knots
     */
    fun setKnots(x: IntArray?, y: IntArray?, types: ByteArray?, offset: Int, count: Int) {
        numKnots = count
        xKnots = IntArray(numKnots)
        yKnots = IntArray(numKnots)
        knotTypes = ByteArray(numKnots)
        System.arraycopy(x, offset, xKnots, 0, numKnots)
        System.arraycopy(y, offset, yKnots, 0, numKnots)
        System.arraycopy(types, offset, knotTypes, 0, numKnots)
        sortKnots()
        rebuildGradient()
    }

    /**
     * Split a span into two by adding a knot in the middle.
     * @param n the span index
     */
    fun splitSpan(n: Int) {
        val x = (xKnots[n] + xKnots[n + 1]) / 2
        addKnot(x, getColor(x / 256.0f), knotTypes[n].toInt())
        rebuildGradient()
    }

    /**
     * Set a knot position.
     * @param n the knot index
     * @param x the knot position
     * @see .setKnotPosition
     */
    fun setKnotPosition(n: Int, x: Int) {
        xKnots[n] = clamp(x, 0, 255)
        sortKnots()
        rebuildGradient()
    }

    /**
     * Get a knot position.
     * @param n the knot index
     * @return the knot position
     * @see .setKnotPosition
     */
    fun getKnotPosition(n: Int): Int {
        return xKnots[n]
    }

    /**
     * Return the knot at a given position.
     * @param x the position
     * @return the knot number, or 1 if no knot found
     */
    fun knotAt(x: Int): Int {
        for (i in 1 until numKnots - 1) if (xKnots[i + 1] > x) return i
        return 1
    }

    private fun rebuildGradient() {
        xKnots[0] = -1
        xKnots[numKnots - 1] = 256
        yKnots[0] = yKnots[1]
        yKnots[numKnots - 1] = yKnots[numKnots - 2]

        val knot = 0
        for (i in 1 until numKnots - 1) {
            val spanLength = (xKnots[i + 1] - xKnots[i]).toFloat()
            var end = xKnots[i + 1]
            if (i == numKnots - 2) end++
            for (j in xKnots[i] until end) {
                val rgb1 = yKnots[i]
                val rgb2 = yKnots[i + 1]
                val hsb1 = Color.RGBtoHSB((rgb1 shr 16) and 0xff, (rgb1 shr 8) and 0xff, rgb1 and 0xff, null)
                val hsb2 = Color.RGBtoHSB((rgb2 shr 16) and 0xff, (rgb2 shr 8) and 0xff, rgb2 and 0xff, null)
                var t = (j - xKnots[i]).toFloat() / spanLength
                val type = getKnotType(i)
                val blend = getKnotBlend(i).toInt()

                if (j >= 0 && j <= 255) {
                    when (blend) {
                        CONSTANT -> t = 0f
                        LINEAR -> {}
                        SPLINE -> //						map[i] = ImageMath.colorSpline(j, numKnots, xKnots, yKnots);
                            t = smoothStep(0.15f, 0.85f, t)

                        CIRCLE_UP -> {
                            t = t - 1
                            t = sqrt((1 - t * t).toDouble()).toFloat()
                        }

                        CIRCLE_DOWN -> t = 1 - sqrt((1 - t * t).toDouble()).toFloat()
                    }
                    when (type) {
                        RGB -> map[j] = mixColors(t, rgb1, rgb2)
                        HUE_CW, HUE_CCW -> {
                            if (type == HUE_CW) {
                                if (hsb2[0] <= hsb1[0]) hsb2[0] += 1.0f
                            } else {
                                if (hsb1[0] <= hsb2[1]) hsb1[0] += 1.0f
                            }
                            val h: Float = lerp(t, hsb1[0], hsb2[0]) % (TWO_PI)
                            val s: Float = lerp(t, hsb1[1], hsb2[1])
                            val b: Float = lerp(t, hsb1[2], hsb2[2])
                            map[j] = -0x1000000 or Color.HSBtoRGB(h, s, b) //FIXME-alpha
                        }
                    }//					}
                }
            }
        }
    }

    private fun sortKnots() {
        for (i in 1 until numKnots - 1) {
            for (j in 1 until i) {
                if (xKnots[i] < xKnots[j]) {
                    var t = xKnots[i]
                    xKnots[i] = xKnots[j]
                    xKnots[j] = t
                    t = yKnots[i]
                    yKnots[i] = yKnots[j]
                    yKnots[j] = t
                    val bt = knotTypes[i]
                    knotTypes[i] = knotTypes[j]
                    knotTypes[j] = bt
                }
            }
        }
    }

    private fun rebuild() {
        sortKnots()
        rebuildGradient()
    }

    /**
     * Randomize the gradient.
     */
    fun randomize() {
        numKnots = 4 + (6 * Math.random()).toInt()
        xKnots = IntArray(numKnots)
        yKnots = IntArray(numKnots)
        knotTypes = ByteArray(numKnots)
        for (i in 0 until numKnots) {
            xKnots[i] = (255 * Math.random()).toInt()
            yKnots[i] =
                -0x1000000 or ((255 * Math.random()).toInt() shl 16) or ((255 * Math.random()).toInt() shl 8) or (255 * Math.random()).toInt()
            knotTypes[i] = (RGB or SPLINE).toByte()
        }
        xKnots[0] = -1
        xKnots[1] = 0
        xKnots[numKnots - 2] = 255
        xKnots[numKnots - 1] = 256
        sortKnots()
        rebuildGradient()
    }

    /**
     * Mutate the gradient.
     * @param amount the amount in the range zero to one
     */
    fun mutate(amount: Float) {
        for (i in 0 until numKnots) {
            val rgb = yKnots[i]
            var r = ((rgb shr 16) and 0xff)
            var g = ((rgb shr 8) and 0xff)
            var b = (rgb and 0xff)
            r = clamp((r + amount * 255 * (Math.random() - 0.5)).toInt())
            g = clamp((g + amount * 255 * (Math.random() - 0.5)).toInt())
            b = clamp((b + amount * 255 * (Math.random() - 0.5)).toInt())
            yKnots[i] = -0x1000000 or (r shl 16) or (g shl 8) or b
            knotTypes[i] = (RGB or SPLINE).toByte()
        }
        sortKnots()
        rebuildGradient()
    }

    companion object {
        /**
         * Interpolate in RGB space.
         */
        const val RGB: Int = 0x00

        /**
         * Interpolate hue clockwise.
         */
        const val HUE_CW: Int = 0x01

        /**
         * Interpolate hue counter clockwise.
         */
        const val HUE_CCW: Int = 0x02


        /**
         * Interpolate linearly.
         */
        const val LINEAR: Int = 0x10

        /**
         * Interpolate using a spline.
         */
        const val SPLINE: Int = 0x20

        /**
         * Interpolate with a rising circle shape curve.
         */
        const val CIRCLE_UP: Int = 0x30

        /**
         * Interpolate with a falling circle shape curve.
         */
        const val CIRCLE_DOWN: Int = 0x40

        /**
         * Don't tnterpolate - just use the starting value.
         */
        const val CONSTANT: Int = 0x50

        private const val COLOR_MASK = 0x03
        private const val BLEND_MASK = 0x70

        /**
         * Build a random gradient.
         * @return the new Gradient
         */
        fun randomGradient(): Gradient {
            val g = Gradient()
            g.randomize()
            return g
        }
    }
}