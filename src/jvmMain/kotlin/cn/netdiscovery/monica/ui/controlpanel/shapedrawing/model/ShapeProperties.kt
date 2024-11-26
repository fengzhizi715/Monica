package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model

import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border

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
    var alpha: Float = 1f,
    var fontSize: Float = 40f,
    var fill: Boolean = true,
    var border: Border = Border.Line
)