package cn.netdiscovery.monica.utils

import cn.netdiscovery.monica.ui.controlpanel.cropimage.setting.CropProperties
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.Typealiases
 * @author: Tony Shen
 * @date:  2024/9/21 14:56
 * @version: V1.0 <描述当前版本功能>
 */
typealias CVAction = (byteArray:ByteArray) -> IntArray

typealias CVSuccess = (image: BufferedImage)->Unit

typealias CVFailure = (e:Exception) -> Unit

typealias OnCropPropertiesChange = (cropProperties: CropProperties) -> Unit

typealias Action = () -> Unit