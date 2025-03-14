package cn.netdiscovery.monica.imageprocess.filter.blur

import cn.netdiscovery.monica.imageprocess.filter.base.BaseFilter
import cn.netdiscovery.monica.imageprocess.math.FFT
import cn.netdiscovery.monica.imageprocess.math.mod
import java.awt.image.BufferedImage
import kotlin.math.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.blur.LensBlurFilter
 * @author: Tony Shen
 * @date: 2025/3/14 15:39
 * @version: V1.0 <描述当前版本功能>
 */
class LensBlurFilter(private val radius:Float = 10f,
                     private val bloom:Float = 2f,
                     private val bloomThreshold:Float = 255f,
                     private val angle:Float = 0f,
                     private val sides:Int = 5): BaseFilter() {

    override fun doFilter(srcImage: BufferedImage, dstImage: BufferedImage): BufferedImage {

        var rows = 1
        var cols = 1
        var log2rows = 0
        var log2cols = 0
        val iradius = ceil(radius.toDouble()).toInt()
        var tileWidth = 128
        var tileHeight = tileWidth

//        val adjustedWidth = width + iradius * 2
//        val adjustedHeight = height + iradius * 2

        tileWidth = if (iradius < 32) Math.min(128, width + 2 * iradius) else Math.min(256, width + 2 * iradius)
        tileHeight = if (iradius < 32) Math.min(128, height + 2 * iradius) else Math.min(256, height + 2 * iradius)

        while (rows < tileHeight) {
            rows *= 2
            log2rows++
        }
        while (cols < tileWidth) {
            cols *= 2
            log2cols++
        }
        val w = cols
        val h = rows

        tileWidth = w
        tileHeight = h // FIXME - tileWidth, w, 和 cols 始终相同

        val fft = FFT(max(log2rows, log2cols))

        val rgb = IntArray(w * h)
        val mask = Array(2) { FloatArray(w * h) }
        val gb = Array(2) { FloatArray(w * h) }
        val ar = Array(2) { FloatArray(w * h) }

        // 创建核函数
        val polyAngle = Math.PI / sides
        val polyScale = 1.0 / Math.cos(polyAngle)
        val r2 = radius * radius
        val rangle = Math.toRadians(angle.toDouble())
        var total = 0f
        var i = 0
        for (y in 0 until h) {
            for (x in 0 until w) {
                val dx:Double = (x - w / 2f).toDouble()
                val dy:Double = (y - h / 2f).toDouble()
                var r:Double = dx * dx + dy * dy
                var f = if (r < r2) 1.0 else 0.0
                if (f != 0.0) {
                    r = Math.sqrt(r)
                    f = if (sides != 0) {
                        var a = Math.atan2(dy, dx) + rangle
                        a = mod(a, polyAngle * 2) - polyAngle
                        Math.cos(a) * polyScale
                    } else {
                        1.0
                    }
                    f = if (f * r < radius) 1.0 else 0.0
                }
                total += f.toFloat()
                mask[0][i] = f.toFloat()
                mask[1][i] = 0f
                i++
            }
        }

        // 归一化核函数
        i = 0
        for (y in 0 until h) {
            for (x in 0 until w) {
                mask[0][i] /= total
                i++
            }
        }

        fft.transform2D(mask[0], mask[1], w, h, true)

        var tileY = -iradius
        while (tileY < height) {
            var tileX = -iradius
            while (tileX < width) {
                // 裁剪 tile 区域到图像范围内
                var tx = tileX
                var ty = tileY
                var tw = tileWidth
                var th = tileHeight
                var fx = 0
                var fy = 0
                if (tx < 0) {
                    tw += tx
                    fx -= tx
                    tx = 0
                }
                if (ty < 0) {
                    th += ty
                    fy -= ty
                    ty = 0
                }
                if (tx + tw > width)
                    tw = width - tx
                if (ty + th > height)
                    th = height - ty

                srcImage.getRGB(tx, ty, tw, th, rgb, fy * w + fx, w)

                // 根据像素创建浮点数组，图像边界之外的像素使用边缘像素值填充
                i = 0
                for (y in 0 until h) {
                    val imageY = y + tileY
                    val j = when {
                        imageY < 0 -> fy
                        imageY > height -> fy + th - 1
                        else -> y
                    } * w
                    for (x in 0 until w) {
                        val imageX = x + tileX
                        val k = when {
                            imageX < 0 -> fx
                            imageX > width -> fx + tw - 1
                            else -> x
                        } + j

                        ar[0][i] = ((rgb[k] shr 24) and 0xff).toFloat()
                        var rPixel = ((rgb[k] shr 16) and 0xff).toFloat()
                        var gPixel = ((rgb[k] shr 8) and 0xff).toFloat()
                        var bPixel = (rgb[k] and 0xff).toFloat()

                        // Bloom 处理
                        if (rPixel > bloomThreshold)
                            rPixel *= bloom
                        if (gPixel > bloomThreshold)
                            gPixel *= bloom
                        if (bPixel > bloomThreshold)
                            bPixel *= bloom

                        ar[1][i] = rPixel
                        gb[0][i] = gPixel
                        gb[1][i] = bPixel

                        i++
                    }
                }

                // 转换到频域
                fft.transform2D(ar[0], ar[1], cols, rows, true)
                fft.transform2D(gb[0], gb[1], cols, rows, true)

                // 将变换后的像素与变换后的核函数相乘
                i = 0
                for (y in 0 until h) {
                    for (x in 0 until w) {
                        val re = ar[0][i]
                        val im = ar[1][i]
                        val rem = mask[0][i]
                        val imm = mask[1][i]
                        ar[0][i] = re * rem - im * imm
                        ar[1][i] = re * imm + im * rem

                        val reGb = gb[0][i]
                        val imGb = gb[1][i]
                        gb[0][i] = reGb * rem - imGb * imm
                        gb[1][i] = reGb * imm + imGb * rem
                        i++
                    }
                }

                // 逆变换回空域
                fft.transform2D(ar[0], ar[1], cols, rows, false)
                fft.transform2D(gb[0], gb[1], cols, rows, false)

                // 将频域数据转换回 RGB 像素，并进行象限重新映射
                val row_flip = w shr 1
                val col_flip = h shr 1
                var index = 0
                for (y in 0 until w) {
                    val ym = y xor row_flip
                    val yi = ym * cols
                    for (x in 0 until w) {
                        val xm = yi + (x xor col_flip)
                        var a = ar[0][xm].toInt()
                        var r = ar[1][xm].toInt()
                        var g = gb[0][xm].toInt()
                        var b = gb[1][xm].toInt()

                        // 限制 Bloom 后过高的像素值
                        if (r > 255) r = 255
                        if (g > 255) g = 255
                        if (b > 255) b = 255
                        val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
                        rgb[index++] = argb
                    }
                }

                // 将处理后的 tile 裁剪写回输出图像
                tx = tileX + iradius
                ty = tileY + iradius
                tw = tileWidth - 2 * iradius
                th = tileHeight - 2 * iradius
                if (tx + tw > width)
                    tw = width - tx
                if (ty + th > height)
                    th = height - ty
                dstImage.setRGB(tx, ty, tw, th, rgb, iradius * w + iradius, w)

                tileX += tileWidth - 2 * iradius
            }
            tileY += tileHeight - 2 * iradius
        }

        return dstImage
    }
}