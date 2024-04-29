package cn.netdiscovery.monica.utils.extension

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import javax.imageio.ImageIO


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extension.Extension
 * @author: Tony Shen
 * @date: 2024/4/26 11:14
 * @version: V1.0 <描述当前版本功能>
 */
fun Float.to2fStr(): String = DecimalFormat("#.##").format(this)

@Throws(IOException::class)
fun writeImageFile(bi: BufferedImage) {
    val outputfile = File("saved.png")
    ImageIO.write(bi, "png", outputfile)
}
