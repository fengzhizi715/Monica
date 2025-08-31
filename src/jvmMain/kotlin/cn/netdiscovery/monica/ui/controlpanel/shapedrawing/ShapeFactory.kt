package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * 形状工厂类
 * 提供创建各种几何形状的便捷方法
 * 
 * @author Tony Shen
 * @date 2024/11/21 16:09
 * @version V1.0
 */
object ShapeFactory {
    
    /**
     * 创建线条
     */
    fun createLine(
        from: Offset,
        to: Offset,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fill: Boolean = false,
        border: Border = Border.Line
    ): Shape.Line = Shape.Line(
        from = from,
        to = to,
        shapeProperties = ShapeProperties(
            color = color,
            alpha = alpha,
            fill = fill,
            border = border
        )
    )
    
    /**
     * 创建圆形
     */
    fun createCircle(
        center: Offset,
        radius: Float,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fill: Boolean = false,
        border: Border = Border.Line
    ): Shape.Circle = Shape.Circle(
        center = center,
        radius = radius,
        shapeProperties = ShapeProperties(
            color = color,
            alpha = alpha,
            fill = fill,
            border = border
        )
    )
    
    /**
     * 创建三角形
     */
    fun createTriangle(
        first: Offset,
        second: Offset,
        third: Offset,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fill: Boolean = false,
        border: Border = Border.Line
    ): Shape.Triangle = Shape.Triangle(
        first = first,
        second = second,
        third = third,
        shapeProperties = ShapeProperties(
            color = color,
            alpha = alpha,
            fill = fill,
            border = border
        )
    )
    
    /**
     * 创建矩形
     */
    fun createRectangle(
        topLeft: Offset,
        bottomRight: Offset,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fill: Boolean = false,
        border: Border = Border.Line
    ): Shape.Rectangle {
        val topRight = Offset(bottomRight.x, topLeft.y)
        val bottomLeft = Offset(topLeft.x, bottomRight.y)
        
        return Shape.Rectangle(
            tl = topLeft,
            bl = bottomLeft,
            br = bottomRight,
            tr = topRight,
            rectFirst = topLeft,
            shapeProperties = ShapeProperties(
                color = color,
                alpha = alpha,
                fill = fill,
                border = border
            )
        )
    }
    
    /**
     * 创建正方形
     */
    fun createSquare(
        center: Offset,
        size: Float,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fill: Boolean = false,
        border: Border = Border.Line
    ): Shape.Rectangle {
        val halfSize = size / 2f
        val topLeft = Offset(center.x - halfSize, center.y - halfSize)
        val bottomRight = Offset(center.x + halfSize, center.y + halfSize)
        
        return createRectangle(topLeft, bottomRight, color, alpha, fill, border)
    }
    
    /**
     * 创建多边形
     */
    fun createPolygon(
        points: List<Offset>,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fill: Boolean = false,
        border: Border = Border.Line
    ): Shape.Polygon = Shape.Polygon(
        points = points,
        shapeProperties = ShapeProperties(
            color = color,
            alpha = alpha,
            fill = fill,
            border = border
        )
    )
    
    /**
     * 创建正多边形
     */
    fun createRegularPolygon(
        center: Offset,
        radius: Float,
        sides: Int,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fill: Boolean = false,
        border: Border = Border.Line
    ): Shape.Polygon {
        require(sides >= 3) { "Polygon must have at least 3 sides" }
        
        val points = mutableListOf<Offset>()
        val angleStep = 2f * PI.toFloat() / sides
        
        for (i in 0 until sides) {
            val angle = i * angleStep
            val x = center.x + radius * cos(angle)
            val y = center.y + radius * sin(angle)
            points.add(Offset(x, y))
        }
        
        return createPolygon(points, color, alpha, fill, border)
    }
    
    /**
     * 创建文本
     */
    fun createText(
        point: Offset,
        message: String,
        color: Color = Color.Red,
        alpha: Float = 1f,
        fontSize: Float = 40f
    ): Shape.Text = Shape.Text(
        point = point,
        message = message,
        shapeProperties = ShapeProperties(
            color = color,
            alpha = alpha,
            fontSize = fontSize
        )
    )
    
    /**
     * 创建点
     */
    fun createPoint(
        position: Offset,
        color: Color = Color.Red,
        alpha: Float = 1f
    ): Shape.Line = Shape.Line(
        from = position,
        to = position,
        shapeProperties = ShapeProperties(
            color = color,
            alpha = alpha,
            fill = false,
            border = Border.No
        )
    )
    
    /**
     * 使用构建器模式创建形状
     */
    fun createShape(init: ShapeBuilder.() -> Unit): Shape = 
        ShapeBuilder().apply(init).build()
}

/**
 * 形状构建器
 * 提供流畅的API来构建形状
 */
class ShapeBuilder {
    private var shapeType: String = ""
    private var points: MutableList<Offset> = mutableListOf()
    private var center: Offset = Offset.Unspecified
    private var radius: Float = 0f
    private var size: Float = 0f
    private var sides: Int = 0
    private var message: String = ""
    private var color: Color = Color.Red
    private var alpha: Float = 1f
    private var fill: Boolean = false
    private var border: Border = Border.Line
    private var fontSize: Float = 40f
    
    fun line(from: Offset, to: Offset) = apply { 
        shapeType = "line"
        points.clear()
        points.add(from)
        points.add(to)
    }
    
    fun circle(center: Offset, radius: Float) = apply { 
        shapeType = "circle"
        this.center = center
        this.radius = radius
    }
    
    fun triangle(first: Offset, second: Offset, third: Offset) = apply { 
        shapeType = "triangle"
        points.clear()
        points.addAll(listOf(first, second, third))
    }
    
    fun rectangle(topLeft: Offset, bottomRight: Offset) = apply { 
        shapeType = "rectangle"
        points.clear()
        points.add(topLeft)
        points.add(bottomRight)
    }
    
    fun polygon(points: List<Offset>) = apply { 
        shapeType = "polygon"
        this.points.clear()
        this.points.addAll(points)
    }
    
    fun text(point: Offset, message: String) = apply { 
        shapeType = "text"
        this.center = point
        this.message = message
    }
    
    fun color(color: Color) = apply { this.color = color }
    fun alpha(alpha: Float) = apply { this.alpha = alpha.coerceIn(0f, 1f) }
    fun fill(fill: Boolean) = apply { this.fill = fill }
    fun border(border: Border) = apply { this.border = border }
    fun fontSize(fontSize: Float) = apply { this.fontSize = fontSize.coerceAtLeast(1f) }
    
    fun build(): Shape = when (shapeType) {
        "line" -> if (points.size >= 2) {
            ShapeFactory.createLine(points[0], points[1], color, alpha, fill, border)
        } else throw IllegalStateException("Line requires 2 points")
        
        "circle" -> if (center != Offset.Unspecified && radius > 0) {
            ShapeFactory.createCircle(center, radius, color, alpha, fill, border)
        } else throw IllegalStateException("Circle requires valid center and radius")
        
        "triangle" -> if (points.size >= 3) {
            ShapeFactory.createTriangle(points[0], points[1], points[2], color, alpha, fill, border)
        } else throw IllegalStateException("Triangle requires 3 points")
        
        "rectangle" -> if (points.size >= 2) {
            ShapeFactory.createRectangle(points[0], points[1], color, alpha, fill, border)
        } else throw IllegalStateException("Rectangle requires 2 points")
        
        "polygon" -> if (points.size >= 3) {
            ShapeFactory.createPolygon(points, color, alpha, fill, border)
        } else throw IllegalStateException("Polygon requires at least 3 points")
        
        "text" -> if (center != Offset.Unspecified && message.isNotBlank()) {
            ShapeFactory.createText(center, message, color, alpha, fontSize)
        } else throw IllegalStateException("Text requires valid point and message")
        
        else -> throw IllegalStateException("No shape type specified")
    }
}
