package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.PI

/**
 * 形状枚举类型
 * 
 * @author Tony Shen
 * @date 2024/11/22 14:34
 * @version V1.0
 */
enum class ShapeEnum {
    Point,
    Line,
    Circle,
    Triangle,
    Rectangle,
    Polygon,
    Text,
    NotAShape
}

/**
 * 形状基类密封类
 * 定义了所有支持的几何形状类型
 */
sealed class Shape {
    
    /**
     * 线条形状
     * @param from 起始点
     * @param to 结束点
     * @param shapeProperties 形状属性
     */
    data class Line(
        val from: Offset, 
        val to: Offset, 
        val shapeProperties: ShapeProperties
    ) : Shape() {
        
        /**
         * 检查线条是否有效
         */
        fun isValid(): Boolean = from != Offset.Unspecified && to != Offset.Unspecified
        
        /**
         * 获取线条长度
         */
        fun getLength(): Float = if (isValid()) {
            sqrt((to.x - from.x).pow(2) + (to.y - from.y).pow(2))
        } else 0f
    }

    /**
     * 圆形形状
     * @param center 圆心
     * @param radius 半径
     * @param shapeProperties 形状属性
     */
    data class Circle(
        val center: Offset, 
        val radius: Float, 
        val shapeProperties: ShapeProperties
    ) : Shape() {
        
        /**
         * 检查圆形是否有效
         */
        fun isValid(): Boolean = center != Offset.Unspecified && radius > 0
        
        /**
         * 获取圆形面积
         */
        fun getArea(): Float = if (isValid()) {
            (PI * radius * radius).toFloat()
        } else 0f
    }

    /**
     * 三角形形状
     * @param first 第一个顶点
     * @param second 第二个顶点
     * @param third 第三个顶点
     * @param shapeProperties 形状属性
     */
    data class Triangle(
        val first: Offset, 
        val second: Offset? = null, 
        val third: Offset? = null, 
        val shapeProperties: ShapeProperties
    ) : Shape() {
        
        /**
         * 检查三角形是否有效
         */
        fun isValid(): Boolean = 
            first != Offset.Unspecified && 
            second != Offset.Unspecified && 
            third != Offset.Unspecified
        
        /**
         * 获取三角形顶点列表
         */
        fun getPoints(): List<Offset> = listOfNotNull(first, second, third)
    }

    /**
     * 矩形形状
     * @param tl 左上角
     * @param bl 左下角
     * @param br 右下角
     * @param tr 右上角
     * @param rectFirst 第一个点（用于预览）
     * @param shapeProperties 形状属性
     */
    data class Rectangle(
        val tl: Offset, 
        val bl: Offset, 
        val br: Offset, 
        val tr: Offset, 
        val rectFirst: Offset,
        val shapeProperties: ShapeProperties
    ) : Shape() {
        
        /**
         * 检查矩形是否有效
         */
        fun isValid(): Boolean = 
            tl != Offset.Unspecified && 
            bl != Offset.Unspecified && 
            br != Offset.Unspecified && 
            tr != Offset.Unspecified
        
        /**
         * 获取矩形顶点列表
         */
        fun getPoints(): List<Offset> = listOf(tl, bl, br, tr)
        
        /**
         * 获取矩形宽度
         */
        fun getWidth(): Float = if (isValid()) {
            sqrt((tr.x - tl.x).pow(2) + (tr.y - tl.y).pow(2))
        } else 0f
        
        /**
         * 获取矩形高度
         */
        fun getHeight(): Float = if (isValid()) {
            sqrt((bl.x - tl.x).pow(2) + (bl.y - tl.y).pow(2))
        } else 0f
    }

    /**
     * 多边形形状
     * @param points 顶点列表
     * @param shapeProperties 形状属性
     */
    data class Polygon(
        val points: List<Offset>, 
        val shapeProperties: ShapeProperties
    ) : Shape() {
        
        /**
         * 检查多边形是否有效
         */
        fun isValid(): Boolean = points.size >= 3 && 
            points.all { it != Offset.Unspecified }
        
        /**
         * 获取多边形的边数
         */
        fun getSideCount(): Int = points.size
    }

    /**
     * 文本形状
     * @param point 文本位置
     * @param message 文本内容
     * @param shapeProperties 形状属性
     */
    data class Text(
        val point: Offset, 
        val message: String, 
        val shapeProperties: ShapeProperties
    ) : Shape() {
        
        /**
         * 检查文本是否有效
         */
        fun isValid(): Boolean = point != Offset.Unspecified && message.isNotBlank()
    }
}

/**
 * 扩展函数：获取形状类型
 */
fun Shape.getType(): ShapeEnum = when (this) {
    is Shape.Line -> ShapeEnum.Line
    is Shape.Circle -> ShapeEnum.Circle
    is Shape.Triangle -> ShapeEnum.Triangle
    is Shape.Rectangle -> ShapeEnum.Rectangle
    is Shape.Polygon -> ShapeEnum.Polygon
    is Shape.Text -> ShapeEnum.Text
}

