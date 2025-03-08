package cn.netdiscovery.monica.imageprocess

import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.Transformer
 * @author: Tony Shen
 * @date: 2024/4/27 13:32
 * @version: V1.0 图像转换的接口
 */
interface Transformer {

    fun transform(image: BufferedImage): BufferedImage
}