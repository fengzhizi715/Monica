package cn.netdiscovery.monica.utils.extension

import java.text.DecimalFormat

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extension.Extension
 * @author: Tony Shen
 * @date: 2024/4/26 11:14
 * @version: V1.0 <描述当前版本功能>
 */
fun Float.to2fStr(): String = DecimalFormat("#.##").format(this)