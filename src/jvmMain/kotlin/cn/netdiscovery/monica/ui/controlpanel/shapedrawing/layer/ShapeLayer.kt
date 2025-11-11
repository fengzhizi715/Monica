package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape

/**
 * 形状图层，负责维护各种几何形状的数据集合。
 *
 * 渲染逻辑会在后续的 LayerRenderer 中统一处理，此处只负责数据管理。
 */
class ShapeLayer(
    name: String
) : Layer(
    type = LayerType.SHAPE,
    name = name
) {

    val displayLines: SnapshotStateMap<Offset, Shape.Line> = mutableStateMapOf()
    val originalLines: SnapshotStateMap<Offset, Shape.Line> = mutableStateMapOf()

    val displayCircles: SnapshotStateMap<Offset, Shape.Circle> = mutableStateMapOf()
    val originalCircles: SnapshotStateMap<Offset, Shape.Circle> = mutableStateMapOf()

    val displayTriangles: SnapshotStateMap<Offset, Shape.Triangle> = mutableStateMapOf()
    val originalTriangles: SnapshotStateMap<Offset, Shape.Triangle> = mutableStateMapOf()

    val displayRectangles: SnapshotStateMap<Offset, Shape.Rectangle> = mutableStateMapOf()
    val originalRectangles: SnapshotStateMap<Offset, Shape.Rectangle> = mutableStateMapOf()

    val displayPolygons: SnapshotStateMap<Offset, Shape.Polygon> = mutableStateMapOf()
    val originalPolygons: SnapshotStateMap<Offset, Shape.Polygon> = mutableStateMapOf()

    val displayTexts: SnapshotStateMap<Offset, Shape.Text> = mutableStateMapOf()
    val originalTexts: SnapshotStateMap<Offset, Shape.Text> = mutableStateMapOf()

    override fun render(drawScope: DrawScope) = Unit

    fun addShape(key: Offset, displayShape: Shape, originalShape: Shape) {
        when (displayShape) {
            is Shape.Line -> {
                displayLines[key] = displayShape
                originalLines[key] = (originalShape as? Shape.Line) ?: displayShape
            }

            is Shape.Circle -> {
                displayCircles[key] = displayShape
                originalCircles[key] = (originalShape as? Shape.Circle) ?: displayShape
            }

            is Shape.Triangle -> {
                displayTriangles[key] = displayShape
                originalTriangles[key] = (originalShape as? Shape.Triangle) ?: displayShape
            }

            is Shape.Rectangle -> {
                displayRectangles[key] = displayShape
                originalRectangles[key] = (originalShape as? Shape.Rectangle) ?: displayShape
            }

            is Shape.Polygon -> {
                displayPolygons[key] = displayShape
                originalPolygons[key] = (originalShape as? Shape.Polygon) ?: displayShape
            }

            is Shape.Text -> {
                displayTexts[key] = displayShape
                originalTexts[key] = (originalShape as? Shape.Text) ?: displayShape
            }
        }
    }

    fun removeShape(key: Offset) {
        displayLines.remove(key)
        originalLines.remove(key)

        displayCircles.remove(key)
        originalCircles.remove(key)

        displayTriangles.remove(key)
        originalTriangles.remove(key)

        displayRectangles.remove(key)
        originalRectangles.remove(key)

        displayPolygons.remove(key)
        originalPolygons.remove(key)

        displayTexts.remove(key)
        originalTexts.remove(key)
    }

    fun clearShapes() {
        displayLines.clear()
        originalLines.clear()

        displayCircles.clear()
        originalCircles.clear()

        displayTriangles.clear()
        originalTriangles.clear()

        displayRectangles.clear()
        originalRectangles.clear()

        displayPolygons.clear()
        originalPolygons.clear()

        displayTexts.clear()
        originalTexts.clear()
    }

    fun isEmpty(): Boolean =
        displayLines.isEmpty() &&
            displayCircles.isEmpty() &&
            displayTriangles.isEmpty() &&
            displayRectangles.isEmpty() &&
            displayPolygons.isEmpty() &&
            displayTexts.isEmpty()

    fun snapshot(): ShapeLayerSnapshot = ShapeLayerSnapshot(
        displayLines = displayLines.toMap(),
        originalLines = originalLines.toMap(),
        displayCircles = displayCircles.toMap(),
        originalCircles = originalCircles.toMap(),
        displayTriangles = displayTriangles.toMap(),
        originalTriangles = originalTriangles.toMap(),
        displayRectangles = displayRectangles.toMap(),
        originalRectangles = originalRectangles.toMap(),
        displayPolygons = displayPolygons.toMap(),
        originalPolygons = originalPolygons.toMap(),
        displayTexts = displayTexts.toMap(),
        originalTexts = originalTexts.toMap()
    )

    fun restore(snapshot: ShapeLayerSnapshot) {
        replaceAll(
            snapshot.displayLines,
            snapshot.originalLines,
            snapshot.displayCircles,
            snapshot.originalCircles,
            snapshot.displayTriangles,
            snapshot.originalTriangles,
            snapshot.displayRectangles,
            snapshot.originalRectangles,
            snapshot.displayPolygons,
            snapshot.originalPolygons,
            snapshot.displayTexts,
            snapshot.originalTexts
        )
    }

    fun replaceAll(
        displayLines: Map<Offset, Shape.Line>,
        originalLines: Map<Offset, Shape.Line>,
        displayCircles: Map<Offset, Shape.Circle>,
        originalCircles: Map<Offset, Shape.Circle>,
        displayTriangles: Map<Offset, Shape.Triangle>,
        originalTriangles: Map<Offset, Shape.Triangle>,
        displayRectangles: Map<Offset, Shape.Rectangle>,
        originalRectangles: Map<Offset, Shape.Rectangle>,
        displayPolygons: Map<Offset, Shape.Polygon>,
        originalPolygons: Map<Offset, Shape.Polygon>,
        displayTexts: Map<Offset, Shape.Text>,
        originalTexts: Map<Offset, Shape.Text>
    ) {
        this.displayLines.clear()
        this.displayLines.putAll(displayLines)
        this.originalLines.clear()
        this.originalLines.putAll(originalLines)

        this.displayCircles.clear()
        this.displayCircles.putAll(displayCircles)
        this.originalCircles.clear()
        this.originalCircles.putAll(originalCircles)

        this.displayTriangles.clear()
        this.displayTriangles.putAll(displayTriangles)
        this.originalTriangles.clear()
        this.originalTriangles.putAll(originalTriangles)

        this.displayRectangles.clear()
        this.displayRectangles.putAll(displayRectangles)
        this.originalRectangles.clear()
        this.originalRectangles.putAll(originalRectangles)

        this.displayPolygons.clear()
        this.displayPolygons.putAll(displayPolygons)
        this.originalPolygons.clear()
        this.originalPolygons.putAll(originalPolygons)

        this.displayTexts.clear()
        this.displayTexts.putAll(displayTexts)
        this.originalTexts.clear()
        this.originalTexts.putAll(originalTexts)
    }

    data class ShapeLayerSnapshot(
        val displayLines: Map<Offset, Shape.Line>,
        val originalLines: Map<Offset, Shape.Line>,
        val displayCircles: Map<Offset, Shape.Circle>,
        val originalCircles: Map<Offset, Shape.Circle>,
        val displayTriangles: Map<Offset, Shape.Triangle>,
        val originalTriangles: Map<Offset, Shape.Triangle>,
        val displayRectangles: Map<Offset, Shape.Rectangle>,
        val originalRectangles: Map<Offset, Shape.Rectangle>,
        val displayPolygons: Map<Offset, Shape.Polygon>,
        val originalPolygons: Map<Offset, Shape.Polygon>,
        val displayTexts: Map<Offset, Shape.Text>,
        val originalTexts: Map<Offset, Shape.Text>
    )
}



