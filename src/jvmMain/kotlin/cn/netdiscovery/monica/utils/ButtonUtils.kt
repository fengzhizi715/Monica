package cn.netdiscovery.monica.utils

import loadingDisplay

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.ButtonUtils
 * @author: Tony Shen
 * @date: 2024/4/27 17:16
 * @version: V1.0 <描述当前版本功能>
 */
private var currentTime = 0L

/**
 * 防止重复点击
 */
fun click(block:()->Unit) {

    val systemTime: Long = System.currentTimeMillis()

    if(systemTime - currentTime > 1000){
        // 与上次点击时间超过1000毫秒，则按钮可以点击
        block.invoke()
    } else {
        // 与上次点击的时间少于1000毫秒，则按钮不能被点击
    }

    currentTime = systemTime
}

/**
 * 点击按钮后，会带有 loading 的效果
 */
fun clickLoadingDisplay(block:()->Unit) {
    loadingDisplay = true
    block.invoke()
    loadingDisplay = false
}

/**
 * 点击按钮后，会带有 loading 的效果
 */
suspend fun clickLoadingDisplayWithSuspend(block:suspend ()->Unit) {
    loadingDisplay = true
    block.invoke()
    loadingDisplay = false
}