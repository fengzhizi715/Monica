package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model

import androidx.compose.ui.graphics.Color

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
 * @author: Tony Shen
 * @date: 2024/11/24 14:18
 * @version: V1.0 <描述当前版本功能>
 */
class ShapeProperties (
    var color: Color = Color.Black,
    var alpha: Float = 1f
) {

    override fun toString(): String {
        return "color = $color, alpha = $alpha"
    }
}