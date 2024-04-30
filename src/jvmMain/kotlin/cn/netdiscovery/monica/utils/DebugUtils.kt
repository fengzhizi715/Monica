package cn.netdiscovery.monica.utils

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.DebugUtils
 * @author: Tony Shen
 * @date: 2024/4/30 12:43
 * @version: V1.0 <描述当前版本功能>
 */
@Throws(IOException::class)
fun writeImageFile(bi: BufferedImage, fileName:String) {
    ImageIO.write(bi, "png", File(fileName))
}