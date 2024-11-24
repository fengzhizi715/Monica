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

data class Line(val from: Offset, val to: Offset, val shapeProperties: ShapeProperties)

data class Circle(val center: Offset, val radius:Float, val shapeProperties: ShapeProperties)

data class Triangle(val first: Offset, val second: Offset?=null, val third: Offset?=null, val shapeProperties: ShapeProperties)

data class Rectangle(val tl: Offset, val bl: Offset, val br: Offset, val tr: Offset, val rectFirst: Offset,val shapeProperties: ShapeProperties)

data class Polygon(val points: List<Offset>, val shapeProperties: ShapeProperties)