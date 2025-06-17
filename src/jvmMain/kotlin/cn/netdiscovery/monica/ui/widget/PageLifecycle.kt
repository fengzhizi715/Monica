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
@Composable
fun PageLifecycle(
    onInit: suspend () -> Unit,
    onDispose: () -> Unit
) {
    LaunchedEffect(Unit) {
        onInit()
    }

    DisposableEffect(Unit) {
        onDispose()
        onDispose {

        }
    }
}