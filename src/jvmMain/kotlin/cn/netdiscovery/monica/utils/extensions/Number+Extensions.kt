package cn.netdiscovery.monica.utils.extensions

import java.text.DecimalFormat

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extensions.`Number+Extension`
 * @author: Tony Shen
 * @date:  2024/5/4 14:30
 * @version: V1.0 <描述当前版本功能>
 */
val format by lazy {
    DecimalFormat("#.##")
}

fun Float.to2fStr(): String = format.format(this)