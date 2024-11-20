package cn.netdiscovery.monica.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.geometry.Drawer
 * @author: Tony Shen
 * @date: 2024/11/20 11:42
 * @version: V1.0 <描述当前版本功能>
 */
interface Drawer {
    val zoom: Float get() = 1f
    val bounds: Rect get() = Rect(-100f, -100f, 100f, 100f)

    fun point(offset: Offset, color: Color)

    fun circle(center: Offset, radius: Float, style: Style)

    fun line(from: Offset, to: Offset, style: Style)

    fun polygon(points: List<Offset>, style: Style)

    fun text(pos: Offset, text: List<String>, color: Color)

    fun angle(center: Offset, from: Float, to: Float, style: Style)
}