package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model

import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border

/**
 * 形状属性类
 * 定义了形状的视觉属性，如颜色、透明度、字体大小等
 * 
 * @author Tony Shen
 * @date 2024/11/24 14:18
 * @version V1.0
 */
data class ShapeProperties(
    val color: Color = Color.Red,
    val alpha: Float = 1f,
    val fontSize: Float = 40f,
    val fill: Boolean = false,
    val border: Border = Border.Line
) {
    
    init {
        require(alpha in 0f..1f) { "Alpha must be between 0.0 and 1.0" }
        require(fontSize > 0f) { "Font size must be positive" }
    }
}