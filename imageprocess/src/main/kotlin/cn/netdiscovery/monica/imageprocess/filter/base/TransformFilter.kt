package cn.netdiscovery.monica.imageprocess.filter.base

import cn.netdiscovery.monica.imageprocess.utils.bilinearInterpolate
import cn.netdiscovery.monica.imageprocess.utils.clamp
import cn.netdiscovery.monica.imageprocess.utils.mod
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.floor


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.base.TransformFilter
 * @author: Tony Shen
 * @date:  2025/3/8 13:45
 * @version: V1.0 <描述当前版本功能>
 */
abstract class TransformFilter: BaseFilter() {

    /**
     * Treat pixels off the edge as zero.
     */
    val ZERO: Int = 0

    /**
     * Clamp pixels to the image edges.
     */
    val CLAMP: Int = 1

    /**
     * Wrap pixels off the edge onto the oppsoite edge.
     */
    val WRAP: Int = 2

    /**
     * Clamp pixels RGB to the image edges, but zero the alpha. This prevents gray borders on your image.
     */
    val RGB_CLAMP: Int = 3

    /**
     * Use nearest-neighbout interpolation.
     */
    val NEAREST_NEIGHBOUR: Int = 0

    /**
     * Use bilinear interpolation.
     */
    val BILINEAR: Int = 1

    /**
     * The action to take for pixels off the image edge.
     */
    protected var edgeAction: Int = RGB_CLAMP

    /**
     * The type of interpolation to use.
     */
    protected var interpolation: Int = BILINEAR

    /**
     * The output image rectangle.
     */
    protected var transformedSpace: Rectangle? = null

    /**
     * The input image rectangle.
     */
    protected var originalSpace: Rectangle? = null

//    /**
//     * Set the action to perform for pixels off the edge of the image.
//     * @param edgeAction one of ZERO, CLAMP or WRAP
//     * @see .getEdgeAction
//     */
//    fun setEdgeAction(edgeAction: Int) {
//        this.edgeAction = edgeAction
//    }
//
//    /**
//     * Get the action to perform for pixels off the edge of the image.
//     * @return one of ZERO, CLAMP or WRAP
//     * @see .setEdgeAction
//     */
//    fun getEdgeAction(): Int {
//        return edgeAction
//    }

//    /**
//     * Set the type of interpolation to perform.
//     * @param interpolation one of NEAREST_NEIGHBOUR or BILINEAR
//     * @see .getInterpolation
//     */
//    fun setInterpolation(interpolation: Int) {
//        this.interpolation = interpolation
//    }
//
//    /**
//     * Get the type of interpolation to perform.
//     * @return one of NEAREST_NEIGHBOUR or BILINEAR
//     * @see .setInterpolation
//     */
//    fun getInterpolation(): Int {
//        return interpolation
//    }

    /**
     * Inverse transform a point. This method needs to be overriden by all subclasses.
     * @param x the X position of the pixel in the output image
     * @param y the Y position of the pixel in the output image
     * @param out the position of the pixel in the input image
     */
    abstract fun transformInverse(x: Int, y: Int, out: FloatArray)

    /**
     * Forward transform a rectangle. Used to determine the size of the output image.
     * @param rect the rectangle to transform
     */
    protected fun transformSpace(rect: Rectangle?) {
    }

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {
        var dst = dstImage
        val width = srcImage.width
        val height = srcImage.height
        val type = srcImage.type
        val srcRaster = srcImage.raster

        originalSpace = Rectangle(0, 0, width, height)
        transformedSpace = Rectangle(0, 0, width, height)
        transformSpace(transformedSpace)

        val dstRaster = dst.raster

        val inPixels: IntArray = getRGB(srcImage, 0, 0, width, height, null)

        if (interpolation == NEAREST_NEIGHBOUR) return filterPixelsNN(dst, width, height, inPixels, transformedSpace!!)

        val srcWidth = width
        val srcHeight = height
        val srcWidth1 = width - 1
        val srcHeight1 = height - 1
        val outWidth = transformedSpace!!.width
        val outHeight = transformedSpace!!.height
        val index = 0
        val outPixels = IntArray(outWidth)

        val outX = transformedSpace!!.x
        val outY = transformedSpace!!.y
        val out = FloatArray(2)

        for (y in 0 until outHeight) {
            for (x in 0 until outWidth) {
                transformInverse(outX + x, outY + y, out)
                val srcX = floor(out[0].toDouble()).toInt()
                val srcY = floor(out[1].toDouble()).toInt()
                val xWeight = out[0] - srcX
                val yWeight = out[1] - srcY
                var nw: Int
                var ne: Int
                var sw: Int
                var se: Int

                if (srcX >= 0 && srcX < srcWidth1 && srcY >= 0 && srcY < srcHeight1) {
                    // Easy case, all corners are in the image
                    val i = srcWidth * srcY + srcX
                    nw = inPixels[i]
                    ne = inPixels[i + 1]
                    sw = inPixels[i + srcWidth]
                    se = inPixels[i + srcWidth + 1]
                } else {
                    // Some of the corners are off the image
                    nw = getPixel(inPixels, srcX, srcY, srcWidth, srcHeight)
                    ne = getPixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight)
                    sw = getPixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight)
                    se = getPixel(inPixels, srcX + 1, srcY + 1, srcWidth, srcHeight)
                }
                outPixels[x] = bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se)
            }
            setRGB(dst, 0, y, transformedSpace!!.width, 1, outPixels)
        }
        return dst
    }

    private fun getPixel(pixels: IntArray, x: Int, y: Int, width: Int, height: Int): Int {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return when (edgeAction) {
                ZERO -> 0
                WRAP -> pixels[mod(y, height) * width + mod(x, width)]
                CLAMP -> pixels[clamp(y, 0, height - 1) * width + clamp(x, 0, width - 1)]
                RGB_CLAMP -> pixels[clamp(y, 0, height - 1) * width + clamp(x, 0, width - 1)] and 0x00ffffff

                else -> 0
            }
        }
        return pixels[y * width + x]
    }

    protected fun filterPixelsNN(
        dst: BufferedImage,
        width: Int,
        height: Int,
        inPixels: IntArray,
        transformedSpace: Rectangle
    ): BufferedImage {
        val srcWidth = width
        val srcHeight = height
        val outWidth = transformedSpace.width
        val outHeight = transformedSpace.height
        var srcX: Int
        var srcY: Int
        val outPixels = IntArray(outWidth)

        val outX = transformedSpace.x
        val outY = transformedSpace.y
        val rgb = IntArray(4)
        val out = FloatArray(2)

        for (y in 0 until outHeight) {
            for (x in 0 until outWidth) {
                transformInverse(outX + x, outY + y, out)
                srcX = out[0].toInt()
                srcY = out[1].toInt()
                // int casting rounds towards zero, so we check out[0] < 0, not srcX < 0
                if (out[0] < 0 || srcX >= srcWidth || out[1] < 0 || srcY >= srcHeight) {
                    var p = when (edgeAction) {
                        ZERO -> 0
                        WRAP -> inPixels[mod(srcY, srcHeight) * srcWidth + mod(srcX, srcWidth)]
                        CLAMP -> inPixels[clamp(srcY, 0, srcHeight - 1) * srcWidth + clamp(srcX, 0, srcWidth - 1)]
                        RGB_CLAMP -> inPixels[clamp(srcY, 0, srcHeight - 1) * srcWidth + clamp(srcX, 0, srcWidth - 1)] and 0x00ffffff

                        else -> 0
                    }
                    outPixels[x] = p
                } else {
                    val i = srcWidth * srcY + srcX
                    rgb[0] = inPixels[i]
                    outPixels[x] = inPixels[i]
                }
            }
            setRGB(dst, 0, y, transformedSpace.width, 1, outPixels)
        }
        return dst
    }
}