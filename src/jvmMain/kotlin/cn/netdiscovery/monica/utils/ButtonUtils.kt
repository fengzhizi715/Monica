package cn.netdiscovery.monica.utils

import androidx.compose.runtime.*
import loadingDisplay

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.ButtonUtils
 * @author: Tony Shen
 * @date: 2024/4/27 17:16
 * @version: V1.0 <描述当前版本功能>
 */
const val VIEW_CLICK_INTERVAL_TIME = 1000 // View 的 click 方法的两次点击间隔时间

/**
 * 防止 view 的重复点击，默认是 1s 间隔，不同的 view 可以有不同的间隔时间。
 */
@Composable
inline fun composeClick(
    time: Int = VIEW_CLICK_INTERVAL_TIME,
    crossinline filter: () -> Boolean = { true },
    crossinline onClick: Action
): Action {
    var lastClickTime by remember { mutableStateOf(value = 0L) } // 使用remember函数记录上次点击的时间

    return {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time >= lastClickTime) {          // 判断点击间隔,如果在间隔内则不回调

            if (filter.invoke()) {
                onClick()
            }

            lastClickTime = currentTimeMillis
        }
    }
}

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