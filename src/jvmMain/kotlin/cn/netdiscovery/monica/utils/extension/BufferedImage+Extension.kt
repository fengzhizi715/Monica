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