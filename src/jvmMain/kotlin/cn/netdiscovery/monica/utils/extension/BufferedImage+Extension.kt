package cn.netdiscovery.monica.utils.extension

import cn.netdiscovery.monica.utils.closeQuietly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extension.`BufferedImage+Extension`
 * @author: Tony Shen
 * @date: 2024/5/5 14:09
 * @version: V1.0 <描述当前版本功能>
 */

suspend fun BufferedImage.saveImage(saveFile: File?, quality: Float = 0.8f) {

    withContext(Dispatchers.IO) {
        val outputStream = ImageIO.createImageOutputStream(saveFile)
        val jpgWriter: ImageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
        val jpgWriteParam: ImageWriteParam = jpgWriter.defaultWriteParam
        jpgWriteParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
        jpgWriteParam.compressionQuality = quality
        jpgWriter.output = outputStream
        val outputImage = IIOImage(this@saveImage, null, null)
        jpgWriter.write(null, outputImage, jpgWriteParam)
        jpgWriter.dispose()
        outputStream.flush()

        closeQuietly(outputStream)
    }
}

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