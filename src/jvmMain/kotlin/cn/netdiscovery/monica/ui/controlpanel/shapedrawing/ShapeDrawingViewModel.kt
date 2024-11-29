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
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.*
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
                  texts: Map<Offset, Text>,
                  saveFlag: Boolean = false) {

        lines.forEach {

            val line = it.value

            if (line.from != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(line.from, line.shapeProperties.color)
            }

            if (line.from != Offset.Unspecified && line.to != Offset.Unspecified) {
                val style = Style(null, line.shapeProperties.color, line.shapeProperties.border, null, fill = line.shapeProperties.fill, scale = 1f, alpha = line.shapeProperties.alpha, bounded = true)
                canvasDrawer.line(line.from,line.to, style)
            }
        }

        circles.forEach {

            val circle = it.value

            if (circle.center != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(circle.center, circle.shapeProperties.color)
            }

            val style = Style(null, circle.shapeProperties.color, circle.shapeProperties.border, null, fill = circle.shapeProperties.fill, scale = 1f, alpha = circle.shapeProperties.alpha, bounded = true)
            canvasDrawer.circle(circle.center, circle.radius, style)
        }

        triangles.forEach {
            val triangle = it.value

            if (triangle.first != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(triangle.first, triangle.shapeProperties.color)
            }

            val style = Style(null, triangle.shapeProperties.color, triangle.shapeProperties.border, null, fill = triangle.shapeProperties.fill, scale = 1f, alpha = triangle.shapeProperties.alpha, bounded = true)

            if (triangle.second != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(triangle.second!!, triangle.shapeProperties.color)
                canvasDrawer.line(triangle.first,triangle.second, style)
            }

            if (triangle.first != Offset.Unspecified && triangle.second != Offset.Unspecified && triangle.third != Offset.Unspecified) {
                val list = mutableListOf<Offset>().apply {
                    add(triangle.first)
                    add(triangle.second!!)
                    add(triangle.third!!)
                }

                canvasDrawer.polygon(list, style)
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

                val style = Style(null, rect.shapeProperties.color, rect.shapeProperties.border, null, fill = rect.shapeProperties.fill, scale = 1f, alpha = rect.shapeProperties.alpha, bounded = true)

                canvasDrawer.polygon(list, style)
            }
        }

        polygons.forEach {
            val polygon = it.value

            if (polygon.points[0]!=null && polygon.points[0] != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(polygon.points[0] , polygon.shapeProperties.color)
            }

            val style = Style(null, polygon.shapeProperties.color, polygon.shapeProperties.border, null, fill = polygon.shapeProperties.fill, scale = 1f, alpha = polygon.shapeProperties.alpha, bounded = true)
            if (polygon.points.size>1 && polygon.points[1] != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(polygon.points[1] , polygon.shapeProperties.color)
                canvasDrawer.line(polygon.points[0], polygon.points[1], style)
            }

            canvasDrawer.polygon(polygon.points, style)
        }

        texts.forEach {
            val text = it.value

            if (text.point!= Offset.Unspecified) {
                val list = mutableListOf<String>().apply {
                    add(text.message)
                }
                canvasDrawer.text(text.point, list, text.shapeProperties.color, text.shapeProperties.fontSize)
            }
        }
    }

    fun saveCanvasToBitmap(density: Density,
                           lines: Map<Offset, Line>,
                           circles: Map<Offset, Circle>,
                           triangles: Map<Offset, Triangle>,
                           rectangles: Map<Offset, Rectangle>,
                           polygons: Map<Offset, Polygon>,
                           texts: Map<Offset, Text>,
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

            drawShape(canvasDrawer,lines,circles,triangles,rectangles, polygons,texts, true)
        }

        state.addQueue(state.currentImage!!)
        state.currentImage = image.toAwtImage()
    }
}