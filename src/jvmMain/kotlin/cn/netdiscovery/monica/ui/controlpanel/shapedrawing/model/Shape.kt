package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model

import androidx.compose.ui.geometry.Offset

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
 * @author: Tony Shen
 * @date: 2024/11/22 14:34
 * @version: V1.0 <描述当前版本功能>
 */
enum class ShapeEnum {
    Point,
    Line,
    Circle,
    Triangle,
    Rectangle,
    Polygon,
    NotAShape
}

sealed class Shape {
    data class Line(val from: Offset, val to: Offset, val shapeProperties: ShapeProperties): Shape()

    data class Circle(val center: Offset, val radius:Float, val shapeProperties: ShapeProperties): Shape()

    data class Triangle(val first: Offset, val second: Offset?=null, val third: Offset?=null, val shapeProperties: ShapeProperties): Shape()

    data class Rectangle(val tl: Offset, val bl: Offset, val br: Offset, val tr: Offset, val rectFirst: Offset,val shapeProperties: ShapeProperties): Shape()

    data class Polygon(val points: List<Offset>, val shapeProperties: ShapeProperties): Shape()

    data class Text(val point: Offset, val message:String, val shapeProperties: ShapeProperties): Shape()
}

