package cn.netdiscovery.monica.imageprocess.filter.base


import cn.netdiscovery.monica.imageprocess.utils.clamp
import cn.netdiscovery.monica.imageprocess.utils.premultiply
import cn.netdiscovery.monica.imageprocess.utils.unpremultiply
import java.awt.image.BufferedImage
import java.awt.image.Kernel

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.base.ConvolveFilter
 * @author: Tony Shen
 * @date: 2024/5/5 17:54
 * @version: V1.0 <描述当前版本功能>
 */
open class ConvolveFilter(private val kernel: Kernel): BaseFilter() {

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

    constructor():this(FloatArray(9)) {
    }

    constructor(matrix: FloatArray): this(Kernel(3, 3, matrix)) {
    }

    constructor(rows: Int, cols: Int, matrix: FloatArray) : this(Kernel(cols, rows, matrix))

    /**
     * What do do at the image edges.
     */
    private val edgeAction = CLAMP_EDGES

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        val inPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        getRGB(srcImage, 0, 0, width, height, inPixels)

        if (premultiplyAlpha)
            premultiply(inPixels, 0, inPixels.size)

        convolve(kernel!!, inPixels, outPixels, width, height, alpha, edgeAction)

        if (premultiplyAlpha)
            unpremultiply(outPixels, 0, outPixels.size)

        setRGB(dstImage, 0, 0, width, height, outPixels)
        return dstImage
    }

    /**
     * Convolve a block of pixels.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param edgeAction what to do at the edges
     */
    open fun convolve(
        kernel: Kernel,
        inPixels: IntArray,
        outPixels: IntArray,
        width: Int,
        height: Int,
        edgeAction: Int
    ) {
        convolve(kernel, inPixels, outPixels, width, height, true, edgeAction)
    }

    /**
     * Convolve a block of pixels.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    open fun convolve(
        kernel: Kernel,
        inPixels: IntArray,
        outPixels: IntArray,
        width: Int,
        height: Int,
        alpha: Boolean,
        edgeAction: Int
    ) {
        if (kernel.height == 1)
            convolveH(kernel, inPixels, outPixels, width, height, alpha, edgeAction)
        else if (kernel.width == 1)
            convolveV(kernel, inPixels, outPixels, width, height, alpha, edgeAction)
        else
            convolveHV(kernel, inPixels, outPixels, width, height, alpha, edgeAction)
    }

    /**
     * Convolve with a 2D kernel.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    open fun convolveHV(
        kernel: Kernel,
        inPixels: IntArray,
        outPixels: IntArray,
        width: Int,
        height: Int,
        alpha: Boolean,
        edgeAction: Int
    ) {
        var index = 0
        val matrix = kernel.getKernelData(null)
        val rows = kernel.height
        val cols = kernel.width
        val rows2 = rows / 2
        val cols2 = cols / 2
        for (y in 0 until height) {
            for (x in 0 until width) {
                var r = 0f
                var g = 0f
                var b = 0f
                var a = 0f
                for (row in -rows2..rows2) {
                    val iy = y + row
                    var ioffset: Int
                    ioffset =
                        if (0 <= iy && iy < height) iy * width else if (edgeAction == CLAMP_EDGES) y * width else if (edgeAction == WRAP_EDGES) (iy + height) % height * width else continue
                    val moffset = cols * (row + rows2) + cols2
                    for (col in -cols2..cols2) {
                        val f = matrix[moffset + col]
                        if (f != 0f) {
                            var ix = x + col
                            if (!(0 <= ix && ix < width)) {
                                ix =
                                    if (edgeAction == CLAMP_EDGES) x else if (edgeAction == WRAP_EDGES) (x + width) % width else continue
                            }
                            val rgb = inPixels[ioffset + ix]
                            a += f * (rgb shr 24 and 0xff)
                            r += f * (rgb shr 16 and 0xff)
                            g += f * (rgb shr 8 and 0xff)
                            b += f * (rgb and 0xff)
                        }
                    }
                }
                val ia = if (alpha) clamp((a + 0.5).toInt()) else 0xff
                val ir: Int = clamp((r + 0.5).toInt())
                val ig: Int = clamp((g + 0.5).toInt())
                val ib: Int = clamp((b + 0.5).toInt())
                outPixels[index++] = ia shl 24 or (ir shl 16) or (ig shl 8) or ib
            }
        }
    }

    /**
     * Convolve with a kernel consisting of one row.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    open fun convolveH(
        kernel: Kernel,
        inPixels: IntArray,
        outPixels: IntArray,
        width: Int,
        height: Int,
        alpha: Boolean,
        edgeAction: Int
    ) {
        var index = 0
        val matrix = kernel.getKernelData(null)
        val cols = kernel.width
        val cols2 = cols / 2
        for (y in 0 until height) {
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
                        a += f * (rgb shr 24 and 0xff)
                        r += f * (rgb shr 16 and 0xff)
                        g += f * (rgb shr 8 and 0xff)
                        b += f * (rgb and 0xff)
                    }
                }
                val ia = if (alpha) clamp((a + 0.5).toInt()) else 0xff
                val ir: Int = clamp((r + 0.5).toInt())
                val ig: Int = clamp((g + 0.5).toInt())
                val ib: Int = clamp((b + 0.5).toInt())
                outPixels[index++] = ia shl 24 or (ir shl 16) or (ig shl 8) or ib
            }
        }
    }

    /**
     * Convolve with a kernel consisting of one column.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    open fun convolveV(
        kernel: Kernel,
        inPixels: IntArray,
        outPixels: IntArray,
        width: Int,
        height: Int,
        alpha: Boolean,
        edgeAction: Int
    ) {
        var index = 0
        val matrix = kernel.getKernelData(null)
        val rows = kernel.height
        val rows2 = rows / 2
        for (y in 0 until height) {
            for (x in 0 until width) {
                var r = 0f
                var g = 0f
                var b = 0f
                var a = 0f
                for (row in -rows2..rows2) {
                    val iy = y + row
                    var ioffset: Int
                    ioffset = if (iy < 0) {
                        if (edgeAction == CLAMP_EDGES) 0 else if (edgeAction == WRAP_EDGES) (y + height) % height * width else iy * width
                    } else if (iy >= height) {
                        if (edgeAction == CLAMP_EDGES) (height - 1) * width else if (edgeAction == WRAP_EDGES) (y + height) % height * width else iy * width
                    } else iy * width
                    val f = matrix[row + rows2]
                    if (f != 0f) {
                        val rgb = inPixels[ioffset + x]
                        a += f * (rgb shr 24 and 0xff)
                        r += f * (rgb shr 16 and 0xff)
                        g += f * (rgb shr 8 and 0xff)
                        b += f * (rgb and 0xff)
                    }
                }
                val ia = if (alpha) clamp((a + 0.5).toInt()) else 0xff
                val ir: Int = clamp((r + 0.5).toInt())
                val ig: Int = clamp((g + 0.5).toInt())
                val ib: Int = clamp((b + 0.5).toInt())
                outPixels[index++] = ia shl 24 or (ir shl 16) or (ig shl 8) or ib
            }
        }
    }
}