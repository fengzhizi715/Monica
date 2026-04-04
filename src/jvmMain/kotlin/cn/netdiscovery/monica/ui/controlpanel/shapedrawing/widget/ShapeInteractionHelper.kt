package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state.ShapeDrawingState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

data class ShapeSelection(
    val key: Offset,
    val type: String
)

object ShapeInteractionHelper {

    private const val HIT_TOLERANCE = 14f

    fun findShapeAt(state: ShapeDrawingState, point: Offset): ShapeSelection? {
        val candidates = buildList {
            state.displayTexts.keys.forEach { add(ShapeSelection(it, "Text")) }
            state.displayPolygons.keys.forEach { add(ShapeSelection(it, "Polygon")) }
            state.displayRectangles.keys.forEach { add(ShapeSelection(it, "Rectangle")) }
            state.displayTriangles.keys.forEach { add(ShapeSelection(it, "Triangle")) }
            state.displayCircles.keys.forEach { add(ShapeSelection(it, "Circle")) }
            state.displayLines.keys.forEach { add(ShapeSelection(it, "Line")) }
        }

        return candidates.asReversed().firstOrNull { selection ->
            hitTestShape(state, selection, point)
        } ?: state.lastDrawnShapeKey?.let { lastKey ->
            state.lastDrawnShapeType?.let { lastType ->
                val selection = ShapeSelection(lastKey, lastType)
                selection.takeIf { hitTestShape(state, selection, point) }
            }
        }
    }

    fun drawSelection(drawScope: DrawScope, state: ShapeDrawingState) {
        val selection = state.selectedShapeKey?.let { key ->
            state.selectedShapeType?.let { ShapeSelection(key, it) }
        } ?: return
        val bounds = getBounds(state, selection) ?: return
        drawScope.drawRect(
            color = Color(0xFFFB8C00),
            topLeft = bounds.topLeft,
            size = bounds.size,
            style = Stroke(width = 2f)
        )
    }

    private fun hitTestShape(
        state: ShapeDrawingState,
        selection: ShapeSelection,
        point: Offset
    ): Boolean {
        return when (selection.type) {
            "Line" -> state.displayLines[selection.key]?.let { hitLine(it, point) } == true
            "Circle" -> state.displayCircles[selection.key]?.let { hitCircle(it, point) } == true
            "Triangle" -> state.displayTriangles[selection.key]?.let { hitPolygon(it.getPoints(), point) } == true
            "Rectangle" -> state.displayRectangles[selection.key]?.let { hitPolygon(it.getPoints(), point) } == true
            "Polygon" -> state.displayPolygons[selection.key]?.let { hitPolygon(it.points, point) } == true
            "Text" -> state.displayTexts[selection.key]?.let { hitText(it, point) } == true
            else -> false
        }
    }

    private fun getBounds(state: ShapeDrawingState, selection: ShapeSelection): Rect? {
        return when (selection.type) {
            "Line" -> state.displayLines[selection.key]?.let { boundsOf(listOf(it.from, it.to)) }
            "Circle" -> state.displayCircles[selection.key]?.let {
                Rect(
                    left = it.center.x - it.radius,
                    top = it.center.y - it.radius,
                    right = it.center.x + it.radius,
                    bottom = it.center.y + it.radius
                )
            }
            "Triangle" -> state.displayTriangles[selection.key]?.let { boundsOf(it.getPoints()) }
            "Rectangle" -> state.displayRectangles[selection.key]?.let { boundsOf(it.getPoints()) }
            "Polygon" -> state.displayPolygons[selection.key]?.let { boundsOf(it.points) }
            "Text" -> state.displayTexts[selection.key]?.let { textBounds(it) }
            else -> null
        }?.inflate(6f)
    }

    private fun hitLine(line: Shape.Line, point: Offset): Boolean {
        val lengthSquared = squaredDistance(line.from, line.to)
        if (lengthSquared == 0f) return distance(point, line.from) <= HIT_TOLERANCE
        val t = (((point.x - line.from.x) * (line.to.x - line.from.x)) +
            ((point.y - line.from.y) * (line.to.y - line.from.y))) / lengthSquared
        val clampedT = t.coerceIn(0f, 1f)
        val projection = Offset(
            x = line.from.x + (line.to.x - line.from.x) * clampedT,
            y = line.from.y + (line.to.y - line.from.y) * clampedT
        )
        return distance(point, projection) <= HIT_TOLERANCE
    }

    private fun hitCircle(circle: Shape.Circle, point: Offset): Boolean {
        return distance(circle.center, point) <= circle.radius + HIT_TOLERANCE
    }

    private fun hitPolygon(points: List<Offset>, point: Offset): Boolean {
        if (points.size < 2) return false
        if (pointInPolygon(points, point)) return true
        return points.zip(points.drop(1) + points.first()).any { (start, end) ->
            hitLine(Shape.Line(start, end, pointsStyleFallback), point)
        }
    }

    private fun hitText(text: Shape.Text, point: Offset): Boolean {
        return textBounds(text).contains(point)
    }

    private fun textBounds(text: Shape.Text): Rect {
        val width = max(text.message.length, 1) * text.shapeProperties.fontSize * 0.6f
        val height = max(text.shapeProperties.fontSize, 12f) * 1.4f
        return Rect(
            left = text.point.x,
            top = text.point.y - height,
            right = text.point.x + width,
            bottom = text.point.y + height * 0.2f
        )
    }

    private fun boundsOf(points: List<Offset>): Rect? {
        if (points.isEmpty()) return null
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        return Rect(minX, minY, maxX, maxY)
    }

    private fun pointInPolygon(points: List<Offset>, point: Offset): Boolean {
        var inside = false
        var j = points.lastIndex
        for (i in points.indices) {
            val pi = points[i]
            val pj = points[j]
            val intersects = ((pi.y > point.y) != (pj.y > point.y)) &&
                (point.x < (pj.x - pi.x) * (point.y - pi.y) / ((pj.y - pi.y).takeIf { abs(it) > 0.0001f } ?: 0.0001f) + pi.x)
            if (intersects) inside = !inside
            j = i
        }
        return inside
    }

    private fun distance(a: Offset, b: Offset): Float = sqrt(squaredDistance(a, b))

    private fun squaredDistance(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return dx * dx + dy * dy
    }

    private val pointsStyleFallback = cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties()
}
