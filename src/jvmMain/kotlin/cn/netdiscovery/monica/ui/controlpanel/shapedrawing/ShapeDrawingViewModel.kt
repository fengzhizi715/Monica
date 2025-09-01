package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Style
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.*
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawer

/**
 * 形状绘制视图模型
 * 负责管理各种几何形状的绘制逻辑
 * 
 * @author Tony Shen
 * @date 2024/11/21 16:09
 * @version V1.0
 */
class ShapeDrawingViewModel {

    /**
     * 绘制所有形状到画布
     */
    fun drawShape(
        canvasDrawer: CanvasDrawer,
        lines: Map<Offset, Line>,
        circles: Map<Offset, Circle>,
        triangles: Map<Offset, Triangle>,
        rectangles: Map<Offset, Rectangle>,
        polygons: Map<Offset, Polygon>,
        texts: Map<Offset, Text>,
        saveFlag: Boolean = false
    ) {
        drawLines(canvasDrawer, lines, saveFlag)
        drawCircles(canvasDrawer, circles, saveFlag)
        drawTriangles(canvasDrawer, triangles, saveFlag)
        drawRectangles(canvasDrawer, rectangles, saveFlag)
        drawPolygons(canvasDrawer, polygons, saveFlag)
        drawTexts(canvasDrawer, texts)
    }

    /**
     * 保存画布为位图
     */
    fun saveCanvasToBitmap(
        density: Density,
        lines: Map<Offset, Line>,
        circles: Map<Offset, Circle>,
        triangles: Map<Offset, Triangle>,
        rectangles: Map<Offset, Rectangle>,
        polygons: Map<Offset, Polygon>,
        texts: Map<Offset, Text>,
        image: ImageBitmap,
        state: ApplicationState
    ) {
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
            state.closePreviewWindow()
            drawShape(canvasDrawer, lines, circles, triangles, rectangles, polygons, texts, true)
        }

        state.addQueue(state.currentImage!!)
        state.currentImage = image.toAwtImage()
    }

    // 绘制线条
    private fun drawLines(canvasDrawer: CanvasDrawer, lines: Map<Offset, Line>, saveFlag: Boolean) {
        lines.forEach { (_, line) ->
            if (line.from != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(line.from, line.shapeProperties.color)
            }

            if (line.from != Offset.Unspecified && line.to != Offset.Unspecified) {
                val style = createStyle(line.shapeProperties)
                canvasDrawer.line(line.from, line.to, style)
            }
        }
    }

    // 绘制圆形
    private fun drawCircles(canvasDrawer: CanvasDrawer, circles: Map<Offset, Circle>, saveFlag: Boolean) {
        circles.forEach { (_, circle) ->
            if (circle.center != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(circle.center, circle.shapeProperties.color)
            }

            if (circle.center != Offset.Unspecified) {
                val style = createStyle(circle.shapeProperties)
                canvasDrawer.circle(circle.center, circle.radius, style)
            }
        }
    }

    // 绘制三角形
    private fun drawTriangles(canvasDrawer: CanvasDrawer, triangles: Map<Offset, Triangle>, saveFlag: Boolean) {
        triangles.forEach { (_, triangle) ->
            if (triangle.first != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(triangle.first, triangle.shapeProperties.color)
            }

            val style = createStyle(triangle.shapeProperties)

            // 绘制三角形的边
            if (triangle.second != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(triangle.second!!, triangle.shapeProperties.color)
                canvasDrawer.line(triangle.first, triangle.second, style)
            }

            // 绘制完整的三角形
            if (isValidTriangle(triangle)) {
                val points = listOf(triangle.first, triangle.second!!, triangle.third!!)
                canvasDrawer.polygon(points, style)
            }
        }
    }

    // 绘制矩形
    private fun drawRectangles(canvasDrawer: CanvasDrawer, rectangles: Map<Offset, Rectangle>, saveFlag: Boolean) {
        rectangles.forEach { (_, rect) ->
            if (rect.rectFirst != Offset.Unspecified && !saveFlag) {
                canvasDrawer.point(rect.rectFirst, rect.shapeProperties.color)
            }

            if (isValidRectangle(rect)) {
                val points = listOf(rect.tl, rect.bl, rect.br, rect.tr)
                val style = createStyle(rect.shapeProperties)
                canvasDrawer.polygon(points, style)
            }
        }
    }

    // 绘制多边形
    private fun drawPolygons(canvasDrawer: CanvasDrawer, polygons: Map<Offset, Polygon>, saveFlag: Boolean) {
        polygons.forEach { (_, polygon) ->
            if (polygon.points.isNotEmpty()) {
                if (polygon.points[0] != Offset.Unspecified && !saveFlag) {
                    canvasDrawer.point(polygon.points[0], polygon.shapeProperties.color)
                }

                val style = createStyle(polygon.shapeProperties)
                
                // 绘制多边形的边
                if (polygon.points.size > 1 && polygon.points[1] != Offset.Unspecified && !saveFlag) {
                    canvasDrawer.point(polygon.points[1], polygon.shapeProperties.color)
                    canvasDrawer.line(polygon.points[0], polygon.points[1], style)
                }

                canvasDrawer.polygon(polygon.points, style)
            }
        }
    }

    // 绘制文本
    private fun drawTexts(canvasDrawer: CanvasDrawer, texts: Map<Offset, Text>) {
        texts.forEach { (_, text) ->
            if (text.point != Offset.Unspecified) {
                val textList = listOf(text.message)
                canvasDrawer.text(text.point, textList, text.shapeProperties.color, text.shapeProperties.fontSize)
            }
        }
    }

    // 创建样式对象
    private fun createStyle(properties: cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties): Style {
        return Style(
            name = null,
            color = properties.color,
            border = properties.border,
            equalityGroup = null,
            fill = properties.fill,
            scale = 1f,
            alpha = properties.alpha,
            bounded = true
        )
    }

    // 验证三角形是否有效
    private fun isValidTriangle(triangle: Triangle): Boolean {
        return triangle.first != Offset.Unspecified && 
               triangle.second != Offset.Unspecified && 
               triangle.third != Offset.Unspecified
    }

    // 验证矩形是否有效
    private fun isValidRectangle(rect: Rectangle): Boolean {
        return rect.tl != Offset.Unspecified && 
               rect.bl != Offset.Unspecified && 
               rect.br != Offset.Unspecified && 
               rect.tr != Offset.Unspecified
    }
}