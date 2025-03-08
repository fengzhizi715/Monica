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

/**
 * 点击按钮后，会带有 loading 的效果
 */
fun loadingDisplay(block: Action) {
    loadingDisplay = true
    block.invoke()
    loadingDisplay = false
}

/**
 * 点击按钮后，会带有 loading 的效果
 */
suspend fun loadingDisplayWithSuspend(block:suspend ()->Unit) {
    loadingDisplay = true
    block.invoke()
    loadingDisplay = false
}