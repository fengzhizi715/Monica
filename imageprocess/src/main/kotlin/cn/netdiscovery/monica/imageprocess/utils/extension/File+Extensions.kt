package cn.netdiscovery.monica.imageprocess.utils.extension

import java.io.File
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.utils.extension.`File+Extension`
 * @author: Tony Shen
 * @date:  2025/2/28 23:03
 * @version: V1.0 <描述当前版本功能>
 */

fun File.getImageFormat(): String? {

    if (extension.equals("svg", ignoreCase = true)) {
        return if (readText().trimStart().startsWith("<svg", ignoreCase = true)) {
            "svg"
        } else {
            null
        }
    }

    ImageIO.createImageInputStream(this)?.use { inputStream ->
        val readers: Iterator<ImageReader> = ImageIO.getImageReaders(inputStream)
        if (readers.hasNext()) {
            val reader = readers.next()
            try {
                reader.input = inputStream
                return reader.formatName
            } finally {
                reader.dispose()
            }
        }
    }

    return null // 无法识别格式
}
