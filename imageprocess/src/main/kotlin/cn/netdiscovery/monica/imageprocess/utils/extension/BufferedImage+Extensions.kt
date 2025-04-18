package cn.netdiscovery.monica.imageprocess.utils.extension

import cn.netdiscovery.monica.imageprocess.ImageInfo
import cn.netdiscovery.monica.imageprocess.utils.writeImageFile
import java.awt.Image
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.utils.extension.`BufferedImage+Extension`
 * @author: Tony Shen
 * @date: 2025/2/22 15:00
 * @version: V1.0 <描述当前版本功能>
 */
fun BufferedImage.image2ByteArray() : ByteArray {
    val outStream = ByteArrayOutputStream()
    ImageIO.write(this, "png", outStream)
    return outStream.toByteArray()
}

fun BufferedImage.toImageInfo(): ImageInfo {

    val width = this.width
    val height = this.height
    val byteArray = this.image2ByteArray()

    return ImageInfo(width,height,byteArray)
}

/**
 * 对两个图像按像素进行比较
 */
fun BufferedImage.isEqualTo(image: BufferedImage): Boolean {
    if (width != image.width || height != image.height)
        return false

    for (y in 0 until height) {
        for (x in 0 until width) {
            if (getRGB(x, y) != image.getRGB(x, y))
                return false
        }
    }

    return true
}


/**
 * 对图像裁剪 ROI 区域
 */
fun BufferedImage.subImage(x: Int, y: Int, w: Int, h: Int): BufferedImage {
    if (w < 0 || h < 0)
        throw IllegalArgumentException("Width and height should be non-negative: ($w; $h)")

    var x1 = x
    var x2 = x + w     // w >= 0 => x1 <= x2
    x1 = x1.coerceIn(0, width)
    x2 = x2.coerceIn(0, width)

    var y1 = y
    var y2 = y + h     // h >= 0 => y1 <= y2
    y1 = y1.coerceIn(0, height)
    y2 = y2.coerceIn(0, height)

    if (x2 - x1 == 0 || y2 - y1 == 0)
        return BufferedImage(1, 1, this.type)

    return getSubimage(x1, y1, x2 - x1, y2 - y1)
}

/**
 * 对图像水平翻转
 */
fun BufferedImage.flipHorizontally(): BufferedImage {
    val flipped = BufferedImage(width, height, type)
    val tran = AffineTransform.getTranslateInstance(width.toDouble(), 0.0)
    val flip = AffineTransform.getScaleInstance(-1.0, 1.0)

    tran.concatenate(flip)

    val g = flipped.createGraphics()
    g.transform = tran
    g.drawImage(this, 0, 0, null)
    g.dispose()

    return flipped
}

/**
 * 图像旋转
 */
fun BufferedImage.rotate(angle: Double): BufferedImage {
    val radian = Math.toRadians(angle)
    val sin = abs(sin(radian))
    val cos = abs(cos(radian))
    val newWidth = floor(width.toDouble() * cos + height.toDouble() * sin).toInt()
    val newHeight = floor(height.toDouble() * cos + width.toDouble() * sin).toInt()
    val rotatedImage = BufferedImage(newWidth, newHeight, type)
    val graphics = rotatedImage.createGraphics()
    graphics.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC
    )
    graphics.translate((newWidth - width) / 2, (newHeight - height) / 2)
    // rotation around the center point
    graphics.rotate(radian, (width / 2).toDouble(), (height / 2).toDouble())
    graphics.drawImage(this, 0, 0, null)
    graphics.dispose()
    return rotatedImage
}

/**
 * 对图像进行缩放
 */
fun BufferedImage.resize(width:Int, height:Int): BufferedImage {

    val tmp = this.getScaledInstance(width, height, Image.SCALE_SMOOTH)
    val resizedImage = BufferedImage(width, height, type)
    val g2d = resizedImage.createGraphics()
    try {
        g2d.drawImage(tmp, 0, 0, null)
    } finally {
        g2d.dispose()
    }
    return resizedImage
}