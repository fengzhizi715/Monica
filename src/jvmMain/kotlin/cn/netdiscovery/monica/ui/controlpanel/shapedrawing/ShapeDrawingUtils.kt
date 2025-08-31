package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Style
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.abs

/**
 * 形状绘制工具类
 * 提供常用的绘制辅助方法和常量
 * 
 * @author Tony Shen
 * @date 2024/11/21 16:09
 * @version V1.0
 */
object ShapeDrawingUtils {
    
    // 常用颜色常量
    val COMMON_COLORS = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Cyan,
        Color.Magenta,
        Color.Black,
        Color.White
    )
    
    // 常用边框样式
    val COMMON_BORDERS = listOf(
        Border.Line,
        Border.Dash,
        Border.Dot,
        Border.DashDot,
        Border.No
    )
    
    // 默认点大小
    const val DEFAULT_POINT_SIZE = 4f
    
    // 默认线条宽度
    const val DEFAULT_STROKE_WIDTH = 4.2f
    
    // 最小字体大小
    const val MIN_FONT_SIZE = 8f
    
    // 最大字体大小
    const val MAX_FONT_SIZE = 200f
    
    /**
     * 创建默认的红色样式
     */
    fun createDefaultRedStyle(): Style = Style(
        color = Color.Red,
        border = Border.Line,
        fill = false,
        scale = 1f,
        alpha = 1f
    )
    
    /**
     * 创建默认的蓝色样式
     */
    fun createDefaultBlueStyle(): Style = Style(
        color = Color.Blue,
        border = Border.Line,
        fill = false,
        scale = 1f,
        alpha = 1f
    )
    
    /**
     * 创建填充样式
     */
    fun createFilledStyle(color: Color, alpha: Float = 1f): Style = Style(
        color = color,
        border = Border.Line,
        fill = true,
        scale = 1f,
        alpha = alpha
    )
    
    /**
     * 创建透明样式
     */
    fun createTransparentStyle(): Style = Style(
        color = Color.Transparent,
        border = Border.No,
        fill = false,
        scale = 1f,
        alpha = 0f
    )
    
    /**
     * 从ShapeProperties创建Style
     */
    fun createStyleFromProperties(properties: ShapeProperties): Style = Style(
        color = properties.color,
        border = properties.border,
        fill = properties.fill,
        scale = 1f,
        alpha = properties.alpha
    )
    
    /**
     * 检查Offset是否有效
     */
    fun isValidOffset(offset: Offset): Boolean = offset != Offset.Unspecified
    
    /**
     * 检查多个Offset是否都有效
     */
    fun areValidOffsets(offsets: List<Offset>): Boolean = 
        offsets.all { it != Offset.Unspecified }
    
    /**
     * 计算两点之间的距离
     */
    fun calculateDistance(from: Offset, to: Offset): Float {
        if (!isValidOffset(from) || !isValidOffset(to)) return 0f
        return sqrt((to.x - from.x).pow(2) + (to.y - from.y).pow(2))
    }
    
    /**
     * 计算多边形的周长
     */
    fun calculatePolygonPerimeter(points: List<Offset>): Float {
        if (points.size < 2) return 0f
        
        var perimeter = 0f
        for (i in 0 until points.size - 1) {
            perimeter += calculateDistance(points[i], points[i + 1])
        }
        // 连接最后一个点和第一个点
        if (points.size > 2) {
            perimeter += calculateDistance(points.last(), points.first())
        }
        return perimeter
    }
    
    /**
     * 计算多边形的面积（使用鞋带公式）
     */
    fun calculatePolygonArea(points: List<Offset>): Float {
        if (points.size < 3) return 0f
        
        var area = 0f
        for (i in 0 until points.size) {
            val j = (i + 1) % points.size
            area += points[i].x * points[j].y
            area -= points[j].x * points[i].y
        }
        return abs(area) / 2f
    }
    
    /**
     * 获取形状的边界框
     */
    fun getShapeBounds(shape: Shape): Pair<Offset, Offset>? {
        return when (shape) {
            is Shape.Line -> {
                if (shape.isValid()) {
                    Pair(
                        Offset(minOf(shape.from.x, shape.to.x), minOf(shape.from.y, shape.to.y)),
                        Offset(maxOf(shape.from.x, shape.to.x), maxOf(shape.from.y, shape.to.y))
                    )
                } else null
            }
            is Shape.Circle -> {
                if (shape.isValid()) {
                    val center = shape.center
                    val radius = shape.radius
                    Pair(
                        Offset(center.x - radius, center.y - radius),
                        Offset(center.x + radius, center.y + radius)
                    )
                } else null
            }
            is Shape.Triangle -> {
                if (shape.isValid()) {
                    val points = shape.getPoints()
                    val minX = points.minOf { it.x }
                    val minY = points.minOf { it.y }
                    val maxX = points.maxOf { it.x }
                    val maxY = points.maxOf { it.y }
                    Pair(Offset(minX, minY), Offset(maxX, maxY))
                } else null
            }
            is Shape.Rectangle -> {
                if (shape.isValid()) {
                    val points = shape.getPoints()
                    val minX = points.minOf { it.x }
                    val minY = points.minOf { it.y }
                    val maxX = points.maxOf { it.x }
                    val maxY = points.maxOf { it.y }
                    Pair(Offset(minX, minY), Offset(maxX, maxY))
                } else null
            }
            is Shape.Polygon -> {
                if (shape.isValid()) {
                    val minX = shape.points.minOf { it.x }
                    val minY = shape.points.minOf { it.y }
                    val maxX = shape.points.maxOf { it.x }
                    val maxY = shape.points.maxOf { it.y }
                    Pair(Offset(minX, minY), Offset(maxX, maxY))
                } else null
            }
            is Shape.Text -> {
                if (shape.isValid()) {
                    val point = shape.point
                    Pair(point, point)
                } else null
            }
        }
    }
    
    /**
     * 检查两个形状是否相交
     */
    fun doShapesIntersect(shape1: Shape, shape2: Shape): Boolean {
        val bounds1 = getShapeBounds(shape1) ?: return false
        val bounds2 = getShapeBounds(shape2) ?: return false
        
        val (min1, max1) = bounds1
        val (min2, max2) = bounds2
        
        // 检查边界框是否重叠
        return !(max1.x < min2.x || max2.x < min1.x || 
                max1.y < min2.y || max2.y < min1.y)
    }
}
