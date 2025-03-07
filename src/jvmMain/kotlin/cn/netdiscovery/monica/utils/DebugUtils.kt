package cn.netdiscovery.monica.utils

import kotlin.system.measureTimeMillis

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.DebugUtils
 * @author: Tony Shen
 * @date: 2024/4/30 12:43
 * @version: V1.0 调试时，使用的工具类
 */


/**
 * 统计耗时任务的时间，便于调试时使用
 */
fun measure(block: () -> Unit):Long {

    val timeCost = measureTimeMillis {
        block.invoke()
    }

    return timeCost
}

/**
 * 统计耗时任务的时间，便于调试时使用
 */
suspend fun measureWithSuspend(block: suspend() -> Unit):Long {

    val timeCost = measureTimeMillis {
        block.invoke()
    }

    return timeCost
}