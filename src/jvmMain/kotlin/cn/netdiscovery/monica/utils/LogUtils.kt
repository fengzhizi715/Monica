package cn.netdiscovery.monica.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.LogUtils
 * @author: Tony Shen
 * @date: 2024/7/10 14:03
 * @version: V1.0 <描述当前版本功能>
 */
inline fun <reified T> logger(): Logger = LoggerFactory.getLogger(T::class.java)