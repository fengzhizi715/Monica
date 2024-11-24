package cn.netdiscovery.monica.utils.extension

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extension.`DrawScope+Extensions`
 * @author: Tony Shen
 * @date: 2024/11/25 00:49
 * @version: V1.0 <描述当前版本功能>
 */

fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}