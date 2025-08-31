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
    
    /**
     * 检查属性是否有效
     */
    fun isValid(): Boolean = 
        alpha in 0f..1f && 
        fontSize > 0f
    
    /**
     * 创建带有新颜色的副本
     */
    fun withColor(newColor: Color): ShapeProperties = copy(color = newColor)
    
    /**
     * 创建带有新透明度的副本
     */
    fun withAlpha(newAlpha: Float): ShapeProperties = copy(alpha = newAlpha.coerceIn(0f, 1f))
    
    /**
     * 创建带有新字体大小的副本
     */
    fun withFontSize(newFontSize: Float): ShapeProperties = copy(fontSize = newFontSize.coerceAtLeast(1f))
    
    /**
     * 创建带有新填充状态的副本
     */
    fun withFill(newFill: Boolean): ShapeProperties = copy(fill = newFill)
    
    /**
     * 创建带有新边框样式的副本
     */
    fun withBorder(newBorder: Border): ShapeProperties = copy(border = newBorder)
    
    companion object {
        /**
         * 默认的红色属性
         */
        val DEFAULT_RED = ShapeProperties(
            color = Color.Red,
            alpha = 1f,
            fontSize = 40f,
            fill = false,
            border = Border.Line
        )
        
        /**
         * 默认的蓝色属性
         */
        val DEFAULT_BLUE = ShapeProperties(
            color = Color.Blue,
            alpha = 1f,
            fontSize = 40f,
            fill = false,
            border = Border.Line
        )
        
        /**
         * 默认的绿色属性
         */
        val DEFAULT_GREEN = ShapeProperties(
            color = Color.Green,
            alpha = 1f,
            fontSize = 40f,
            fill = false,
            border = Border.Line
        )
        
        /**
         * 透明属性
         */
        val TRANSPARENT = ShapeProperties(
            color = Color.Transparent,
            alpha = 0f,
            fontSize = 40f,
            fill = false,
            border = Border.No
        )
    }
}

/**
 * 形状属性构建器
 * 提供流畅的API来构建ShapeProperties
 */
class ShapePropertiesBuilder {
    private var color: Color = Color.Red
    private var alpha: Float = 1f
    private var fontSize: Float = 40f
    private var fill: Boolean = false
    private var border: Border = Border.Line
    
    fun color(color: Color) = apply { this.color = color }
    fun alpha(alpha: Float) = apply { this.alpha = alpha.coerceIn(0f, 1f) }
    fun fontSize(fontSize: Float) = apply { this.fontSize = fontSize.coerceAtLeast(1f) }
    fun fill(fill: Boolean) = apply { this.fill = fill }
    fun border(border: Border) = apply { this.border = border }
    
    fun build(): ShapeProperties = ShapeProperties(
        color = color,
        alpha = alpha,
        fontSize = fontSize,
        fill = fill,
        border = border
    )
}

/**
 * 扩展函数：创建形状属性构建器
 */
fun shapeProperties(init: ShapePropertiesBuilder.() -> Unit): ShapeProperties = 
    ShapePropertiesBuilder().apply(init).build()