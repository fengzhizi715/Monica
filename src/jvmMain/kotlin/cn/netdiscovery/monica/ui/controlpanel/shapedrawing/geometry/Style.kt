package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry

import androidx.compose.ui.graphics.Color

/**
 * 边框样式枚举
 * 定义了不同的边框效果
 * 
 * @author Tony Shen
 * @date 2024/11/20 11:45
 * @version V1.0
 */
enum class Border(val effect: FloatArray?, val description: String) {
    No(null, "无边框"),
    Dot(floatArrayOf(5f, 5f), "点状边框"),
    Dash(floatArrayOf(25f, 25f), "虚线边框"),
    DashDot(floatArrayOf(25f, 10f, 5f, 10f), "点划线边框"),
    Line(null, "实线边框");
    
    companion object {
        /**
         * 根据描述获取边框样式
         */
        fun fromDescription(description: String): Border? = 
            values().find { it.description == description }
    }
}

/**
 * 等分组枚举
 * 用于分组相关的样式
 */
enum class EqualityGroup(val description: String) {
    Equal1("等分组1"),
    Equal2("等分组2"),
    Equal3("等分组3"),
    EqualV("等分组V"),
    EqualO("等分组O");
    
    companion object {
        /**
         * 根据描述获取等分组
         */
        fun fromDescription(description: String): EqualityGroup? = 
            values().find { it.description == description }
    }
}

/**
 * 文本跨度处理函数
 * 将包含下划线的文本分割为多个部分
 * 
 * @param text 输入文本
 * @return 分割后的文本列表
 */
fun spans(text: String): List<String> = buildList {
    var last = 0
    while (true) {
        var next = text.indexOf('_', last)
        if (next == text.length - 1 || next == -1) next = text.length
        add(text.substring(last, next))
        if (next == text.length) break
        if (text[next + 1] == '{') {
            last = text.indexOf('}', next + 1)
            if (last == 0) error("Expected '}'")
            add(text.substring(next + 2, last))
            last++
        } else {
            add(text[next + 1].toString())
            last = next + 2
        }
    }
}

/**
 * 样式数据类
 * 定义了绘制形状时的视觉样式
 * 
 * @param name 样式名称列表
 * @param color 颜色
 * @param border 边框样式
 * @param equalityGroup 等分组
 * @param fill 是否填充
 * @param scale 缩放比例
 * @param alpha 透明度
 * @param bounded 是否受边界限制
 */
data class Style(
    val name: List<String>? = null,
    val color: Color,
    val border: Border,
    val equalityGroup: EqualityGroup? = null,
    val fill: Boolean = false,
    val scale: Float = 1f,
    val alpha: Float = 1f,
    val bounded: Boolean = true
) {
    
    init {
        require(scale > 0f) { "Scale must be positive" }
        require(alpha in 0f..1f) { "Alpha must be between 0.0 and 1.0" }
    }
    
    /**
     * 检查样式是否有效
     */
    fun isValid(): Boolean = 
        scale > 0f && 
        alpha in 0f..1f
    
    /**
     * 创建带有新颜色的副本
     */
    fun withColor(newColor: Color): Style = copy(color = newColor)
    
    /**
     * 创建带有新边框的副本
     */
    fun withBorder(newBorder: Border): Style = copy(border = newBorder)
    
    /**
     * 创建带有新透明度的副本
     */
    fun withAlpha(newAlpha: Float): Style = copy(alpha = newAlpha.coerceIn(0f, 1f))
    
    /**
     * 创建带有新缩放比例的副本
     */
    fun withScale(newScale: Float): Style = copy(scale = newScale.coerceAtLeast(0.1f))
    
    /**
     * 创建带有新填充状态的副本
     */
    fun withFill(newFill: Boolean): Style = copy(fill = newFill)
    
    /**
     * 创建带有新边界限制的副本
     */
    fun withBounded(newBounded: Boolean): Style = copy(bounded = newBounded)
    
    companion object {
        /**
         * 默认样式
         */
        val DEFAULT = Style(
            color = Color.Black,
            border = Border.Line,
            fill = false,
            scale = 1f,
            alpha = 1f,
            bounded = true
        )
        
        /**
         * 透明样式
         */
        val TRANSPARENT = Style(
            color = Color.Transparent,
            border = Border.No,
            fill = false,
            scale = 1f,
            alpha = 0f,
            bounded = true
        )
        
        /**
         * 填充样式
         */
        val FILLED = Style(
            color = Color.Blue,
            border = Border.Line,
            fill = true,
            scale = 1f,
            alpha = 1f,
            bounded = true
        )
    }
}