package cn.netdiscovery.monica.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.TimeUtils
 * @author: Tony Shen
 * @date: 2024/5/2 21:40
 * @version: V1.0 <描述当前版本功能>
 */

private const val yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd-H-mm-ss-SSS"

private val formatterWithHorizontal by lazy {
    DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss_SSS).withZone(ZoneId.systemDefault())
}

/**
 * 生成图片的名称
 */
fun currentTime(): String = ZonedDateTime.now().format(formatterWithHorizontal)
