package cn.netdiscovery.monica.imageprocess

import androidx.compose.ui.graphics.ImageBitmap

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.Transformer
 * @author: Tony Shen
 * @date: 2024/4/27 13:32
 * @version: V1.0 <描述当前版本功能>
 */
interface Transformer {

    fun transform(imageBitmap: ImageBitmap): ImageBitmap
}