package cn.netdiscovery.monica.imageprocess

import cn.netdiscovery.monica.imageprocess.utils.extension.convertToRGB
import cn.netdiscovery.monica.imageprocess.utils.extension.getImageFormat
import cn.netdiscovery.monica.imageprocess.utils.loadFixedSvgAsImage
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.BufferedImages
 * @author: Tony Shen
 * @date: 2024/5/7 10:46
 * @version: V1.0 <描述当前版本功能>
 */

data class ImageInfo(val width:Int, val height:Int, val byteArray:ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageInfo

        if (width != other.width) return false
        if (height != other.height) return false
        return byteArray.contentEquals(other.byteArray)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}

class BufferedImages {

    companion object {
        fun create(width: Int, height: Int, type: Int): BufferedImage =
            BufferedImage(
                if (width > 0) width else 1,
                if (height > 0) height else 1,
                type)

        fun toBufferedImage(pixels: IntArray, width: Int, height: Int, type: Int): BufferedImage {
            val bi = BufferedImage(width, height, type)
            bi.setRGB(0, 0, width, height, pixels, 0, width)
            return bi
        }

        fun load(input: InputStream): BufferedImage = ImageIO.read(input)

        fun load(file: File): BufferedImage {

            val format = file.getImageFormat()?:"jpg"

//            println("format: $format")

            return when(format) {
                "svg" -> loadFixedSvgAsImage(file)?:ImageIO.read(file)
                "hdr","HDR" -> {
                    ImageIO.read(file).convertToRGB()
                }
                else -> ImageIO.read(file)
            }
        }

        fun load(path: String): BufferedImage = load(File(path))
    }
}