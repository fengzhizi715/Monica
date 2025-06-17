package cn.netdiscovery.monica.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.PageLifecycle
 * @author: Tony Shen
 * @date: 2025/6/17 15:45
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 页面生命周期钩子，用于页面级别的生命周期管理。在页面进入时执行初始化逻辑，在页面移除时释放资源。
 *
 *  @param onInit 页面进入时的初始化逻辑（支持 suspend 函数）
 *  @param onDisposeEffect 页面被移除时的清理逻辑（同步函数）
 */
@Composable
fun PageLifecycle(
    onInit: suspend () -> Unit,
    onDisposeEffect: () -> Unit
) {
    // 页面进入时执行（支持挂起函数）
    LaunchedEffect(Unit) {
        onInit()
    }

    // 页面移除时执行（释放资源、取消监听等）
    DisposableEffect(Unit) {
        onDispose {
            onDisposeEffect()
        }
    }
}