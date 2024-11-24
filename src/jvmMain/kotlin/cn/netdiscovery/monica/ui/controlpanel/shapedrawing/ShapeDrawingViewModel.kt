package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Style
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.*
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.ShapeDrawingViewModel
 * @author: Tony Shen
 * @date: 2024/11/21 16:09
 * @version: V1.0 <描述当前版本功能>
 */
class ShapeDrawingViewModel {

    fun drawShape(canvasDrawer:CanvasDrawer,
                  lines: Map<Offset, Line>,
                  circles: Map<Offset, Circle>,
                  triangles: Map<Offset, Triangle>,
                  rectangles: Map<Offset, Rectangle>,
                  polygons: Map<Offset, Polygon>,
                  saveFlag: Boolean = false) {

        lines.forEach {

            val line = it.value

            if (line.from != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(line.from, line.shapeProperties.color)
            }

            if (line.from != Offset.Unspecified && line.to != Offset.Unspecified) {
                canvasDrawer.line(line.from,line.to, Style(null, line.shapeProperties.color, Border.Line, null, fill = true, scale = 1f, bounded = true))
            }
        }

        circles.forEach {

            val circle = it.value

            if (circle.center != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(circle.center, circle.shapeProperties.color)
            }

            canvasDrawer.circle(circle.center, circle.radius, Style(null, circle.shapeProperties.color, Border.No, null, fill = true, scale = 1f, bounded = true))
        }

        triangles.forEach {
            val triangle = it.value

            if (triangle.first != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(triangle.first, triangle.shapeProperties.color)
            }

            if (triangle.second != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(triangle.second!!, triangle.shapeProperties.color)
                canvasDrawer.line(triangle.first,triangle.second, Style(null, triangle.shapeProperties.color, Border.Line, null, fill = true, scale = 1f, bounded = true))
            }

            if (triangle.first != Offset.Unspecified && triangle.second != Offset.Unspecified && triangle.third != Offset.Unspecified) {
                val list = mutableListOf<Offset>().apply {
                    add(triangle.first)
                    add(triangle.second!!)
                    add(triangle.third!!)
                }

                canvasDrawer.polygon(list, Style(null, triangle.shapeProperties.color, Border.No, null, fill = true, scale = 1f, bounded = true))
            }
        }

        rectangles.forEach {
            val rect = it.value

            if (rect.rectFirst!=Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(rect.rectFirst, rect.shapeProperties.color)
            }

            if (rect.tl!=Offset.Unspecified && rect.bl!=Offset.Unspecified && rect.br!=Offset.Unspecified && rect.tr!=Offset.Unspecified) {
                val list = mutableListOf<Offset>().apply {
                    add(rect.tl)
                    add(rect.bl)
                    add(rect.br)
                    add(rect.tr)
                }

                canvasDrawer.polygon(list, Style(null, rect.shapeProperties.color, Border.No, null, fill = true, scale = 1f, bounded = true))
            }
        }

        polygons.forEach {
            val polygon = it.value

            if (polygon.points[0]!=null && polygon.points[0] != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(polygon.points[0] , polygon.shapeProperties.color)
            }

            if (polygon.points.size>1 && polygon.points[1] != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(polygon.points[1] , polygon.shapeProperties.color)
                canvasDrawer.line(polygon.points[0], polygon.points[1], Style(null, polygon.shapeProperties.color, Border.Line, null, fill = true, scale = 1f, bounded = true))
            }

            canvasDrawer.polygon(polygon.points, Style(null, polygon.shapeProperties.color, Border.No, null, fill = true, scale = 1f, bounded = true))
        }
    }

    fun saveCanvasToBitmap(density: Density,
                           lines: Map<Offset, Line>,
                           circles: Map<Offset, Circle>,
                           triangles: Map<Offset, Triangle>,
                           rectangles: Map<Offset, Rectangle>,
                           polygons: Map<Offset, Polygon>,
                           image: ImageBitmap,
                           state: ApplicationState) {

        val bitmapWidth = image.width
        val bitmapHeight = image.height

        val drawScope = CanvasDrawScope()
        val size = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val canvas = Canvas(image)
        val canvasDrawer = CanvasDrawer(TextDrawer, canvas)

        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size
        ) {
            state.closeWindows()

            drawShape(canvasDrawer,lines,circles,triangles,rectangles, polygons,true)
        }

        state.addQueue(state.currentImage!!)
        state.currentImage = image.toAwtImage()
    }
}