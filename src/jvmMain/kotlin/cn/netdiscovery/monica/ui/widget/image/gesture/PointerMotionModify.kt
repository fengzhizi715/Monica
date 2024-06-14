package cn.netdiscovery.monica.ui.widget.image.gesture

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.image.gesture.PointerMotionModify
 * @author: Tony Shen
 * @date: 2024/6/13 22:00
 * @version: V1.0 <描述当前版本功能>
 */

fun Modifier.pointerMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    key1: Any?,
    key2: Any?
) = this.then(
    Modifier.pointerInput(key1, key2) {
        detectMotionEvents(
            onDown,
            onMove,
            onUp,
            delayAfterDownInMillis,
            requireUnconsumed,
            pass
        )
    }
)