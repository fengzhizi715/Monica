package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.coordinate

import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.*

/**
 * 坐标转换器
 * 负责显示坐标和原始坐标之间的转换
 * 
 * @author Tony Shen
 * @date 2024/12/19
 * @version V1.0
 */
class CoordinateConverter(
    private val scaleX: Float,
    private val scaleY: Float
) {
    
    /**
     * 显示坐标转原始坐标
     */
    fun displayToOriginal(displayOffset: Offset): Offset {
        return Offset(displayOffset.x * scaleX, displayOffset.y * scaleY)
    }
    
    /**
     * 转换线段坐标
     */
    fun convertLineToOriginal(displayLine: Line): Line {
        val originalFrom = displayToOriginal(displayLine.from)
        val originalTo = displayToOriginal(displayLine.to)
        return Line(originalFrom, originalTo, displayLine.shapeProperties)
    }
    
    /**
     * 转换圆形坐标
     */
    fun convertCircleToOriginal(displayCircle: Circle): Circle {
        val originalCenter = displayToOriginal(displayCircle.center)
        val originalRadius = displayCircle.radius * ((scaleX + scaleY) / 2f) // 平均缩放半径
        return Circle(originalCenter, originalRadius, displayCircle.shapeProperties)
    }
    
    /**
     * 转换三角形坐标
     */
    fun convertTriangleToOriginal(displayTriangle: Triangle): Triangle {
        val originalFirst = displayToOriginal(displayTriangle.first)
        val originalSecond = displayTriangle.second?.let { displayToOriginal(it) }
        val originalThird = displayTriangle.third?.let { displayToOriginal(it) }
        return Triangle(originalFirst, originalSecond, originalThird, displayTriangle.shapeProperties)
    }
    
    /**
     * 转换矩形坐标
     */
    fun convertRectangleToOriginal(displayRect: Rectangle): Rectangle {
        val originalTl = displayToOriginal(displayRect.tl)
        val originalBl = displayToOriginal(displayRect.bl)
        val originalBr = displayToOriginal(displayRect.br)
        val originalTr = displayToOriginal(displayRect.tr)
        val originalFirst = displayToOriginal(displayRect.rectFirst)
        return Rectangle(originalTl, originalBl, originalBr, originalTr, originalFirst, displayRect.shapeProperties)
    }
    
    /**
     * 转换多边形坐标
     */
    fun convertPolygonToOriginal(displayPolygon: Polygon): Polygon {
        val originalPoints = displayPolygon.points.map { displayToOriginal(it) }
        return Polygon(originalPoints, displayPolygon.shapeProperties)
    }
    
    /**
     * 转换文字坐标
     */
    fun convertTextToOriginal(displayText: Text): Text {
        val originalPoint = displayToOriginal(displayText.point)
        return Text(originalPoint, displayText.message, displayText.shapeProperties)
    }
}
