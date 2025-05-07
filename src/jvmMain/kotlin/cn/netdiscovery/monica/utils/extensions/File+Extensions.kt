package cn.netdiscovery.monica.utils.extensions

import java.io.File
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extensions.`File+Extension`
 * @author: Tony Shen
 * @date:  2025/2/28 23:03
 * @version: V1.0 <描述当前版本功能>
 */

fun File.getImageFormat(): String? {
    val inputStream: ImageInputStream = ImageIO.createImageInputStream(this) ?: return null
    val readers: Iterator<ImageReader> = ImageIO.getImageReaders(inputStream)
    return if (readers.hasNext()) {
        val reader = readers.next()
        val format = reader.formatName
        reader.dispose()
        format
    } else {
        null
    }
}
