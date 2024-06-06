package cn.netdiscovery.monica.imageprocess.filter.blur

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.utils.clamp
import java.awt.image.BufferedImage
import java.awt.image.Kernel
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.exp


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.blur.GaussianFilter
 * @author: Tony Shen
 * @date: 2024/4/29 17:40
 * @version: V1.0 <描述当前版本功能>
 */
open class GaussianFilter(open val radius:Float = 5.0f): BaseFilter() {

    /**
     * Treat pixels off the edge as zero.
     */
    var ZERO_EDGES = 0

    /**
     * Clamp pixels off the edge to the nearest edge.
     */
    var CLAMP_EDGES = 1

    /**
     * Wrap pixels off the edge to the opposite edge.
     */
    var WRAP_EDGES = 2

    /**
     * Whether to convolve alpha.
     */
    protected var alpha = true

    /**
     * Whether to promultiply the alpha before convolving.
     */
    protected var premultiplyAlpha = true

    /**
     * The convolution kernel.
     */
    protected var kernel: Kernel

    init {
        kernel = makeKernel(radius)
    }

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        var outPixels = IntArray(width * height)

        if ( radius > 0 ) {
            convolveAndTranspose(kernel, inPixels, outPixels, width, height, alpha, alpha && premultiplyAlpha, false, CLAMP_EDGES)
            convolveAndTranspose(kernel, outPixels, inPixels, height, width, alpha, false, alpha && premultiplyAlpha, CLAMP_EDGES)
        }

        setRGB(dstImage, 0, 0, width, height, inPixels)
        return dstImage
    }

    /**
     * Blur and transpose a block of ARGB pixels.
     * @param kernel the blur kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width of the pixel array
     * @param height the height of the pixel array
     * @param alpha whether to blur the alpha channel
     * @param edgeAction what to do at the edges
     */
    fun convolveAndTranspose(
        kernel: Kernel,
        inPixels: IntArray,
        outPixels: IntArray,
        width: Int,
        height: Int,
        alpha: Boolean,
        premultiply: Boolean,
        unpremultiply: Boolean,
        edgeAction: Int
    ) {
        val matrix = kernel.getKernelData(null)
        val cols = kernel.width
        val cols2 = cols / 2
        for (y in 0 until height) {
            var index = y
            val ioffset = y * width
            for (x in 0 until width) {
                var r = 0f
                var g = 0f
                var b = 0f
                var a = 0f
                for (col in -cols2..cols2) {
                    val f = matrix[cols2 + col]
                    if (f != 0f) {
                        var ix = x + col
                        if (ix < 0) {
                            if (edgeAction == CLAMP_EDGES) ix = 0 else if (edgeAction == WRAP_EDGES) ix =
                                (x + width) % width
                        } else if (ix >= width) {
                            if (edgeAction == CLAMP_EDGES) ix = width - 1 else if (edgeAction == WRAP_EDGES) ix =
                                (x + width) % width
                        }
                        val rgb = inPixels[ioffset + ix]
                        val pa = rgb shr 24 and 0xff
                        var pr = rgb shr 16 and 0xff
                        var pg = rgb shr 8 and 0xff
                        var pb = rgb and 0xff
                        if (premultiply) {
                            val a255 = pa * (1.0f / 255.0f)
                            pr = (pr * a255).toInt()
                            pg = (pg * a255).toInt()
                            pb = (pb * a255).toInt()
                        }
                        a += f * pa
                        r += f * pr
                        g += f * pg
                        b += f * pb
                    }
                }
                if (unpremultiply && a != 0f && a != 255f) {
                    val f = 255.0f / a
                    r *= f
                    g *= f
                    b *= f
                }
                val ia = if (alpha) clamp((a + 0.5).toInt()) else 0xff
                val ir: Int = clamp((r + 0.5).toInt())
                val ig: Int = clamp((g + 0.5).toInt())
                val ib: Int = clamp((b + 0.5).toInt())
                outPixels[index] = ia shl 24 or (ir shl 16) or (ig shl 8) or ib
                index += height
            }
        }
    }

    private fun makeKernel(radius: Float): Kernel {
        val r = ceil(radius.toDouble()).toInt()
        val rows = r * 2 + 1
        val matrix = FloatArray(rows)
        val sigma = radius / 3
        val sigma22 = 2 * sigma * sigma
        val sigmaPi2: Float = 2 * PI.toFloat() * sigma
        val sqrtSigmaPi2 = Math.sqrt(sigmaPi2.toDouble()).toFloat()
        val radius2 = radius * radius
        var total = 0f
        for ((index, row) in (-r..r).withIndex()) {
            val distance = (row * row).toFloat()
            if (distance > radius2) matrix[index] = 0f else matrix[index] =
                exp((-distance / sigma22).toDouble()).toFloat() / sqrtSigmaPi2
            total += matrix[index]
        }
        for (i in 0 until rows) matrix[i] /= total
        return Kernel(rows, 1, matrix)
    }
}