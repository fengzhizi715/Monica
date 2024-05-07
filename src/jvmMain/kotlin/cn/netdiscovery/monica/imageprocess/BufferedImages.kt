package cn.netdiscovery.monica.imageprocess

import client
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
class BufferedImages {

    companion object {
        fun create(width: Int, height: Int, type: Int): BufferedImage =
            BufferedImage(
                if (width > 0) width else 1,
                if (height > 0) height else 1,
                type)

        fun load(input: InputStream): BufferedImage = ImageIO.read(input)

        fun load(file: File): BufferedImage = ImageIO.read(file)

        fun load(path: String): BufferedImage = load(File(path))

        fun loadUrl(url:String): BufferedImage? =  client.getImage(url)
    }
}