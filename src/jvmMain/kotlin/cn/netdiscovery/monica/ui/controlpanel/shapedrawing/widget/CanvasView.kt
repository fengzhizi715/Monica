package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.animation.ShapeAnimationManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state.ShapeDrawingState
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun CanvasView(
    editorController: EditorController,
    drawingState: ShapeDrawingState,
    animationManager: ShapeAnimationManager,
    modifier: Modifier = Modifier.Companion,
    overlay: DrawScope.() -> Unit = {},
    showImageLayerControls: Boolean = true
) {
    // 观察图层列表变化，触发重组和重绘
    val layers by editorController.layerManager.layers.collectAsState()
    val activeLayer by editorController.layerManager.activeLayer.collectAsState()

    Canvas(modifier = modifier) {
        // 使用当前观察到的图层列表进行绘制
        editorController.layerRenderer.drawAll(this, layers)
        drawAllAnimations(
            animationManager = animationManager,
            displayLines = drawingState.displayLines,
            displayCircles = drawingState.displayCircles,
            displayTriangles = drawingState.displayTriangles,
            displayRectangles = drawingState.displayRectangles,
            displayPolygons = drawingState.displayPolygons
        )
        
        // 绘制激活图像层的控制点
        if (showImageLayerControls) {
            val activeImageLayer = activeLayer as? ImageLayer
            if (activeImageLayer != null && !activeImageLayer.locked && activeImageLayer.name != "背景图层") {
                ImageLayerControlRenderer.drawControls(
                    drawScope = this,
                    layer = activeImageLayer,
                    canvasWidth = size.width,
                    canvasHeight = size.height
                )
            }
        }
        
        overlay()
    }
}

private fun DrawScope.drawAllAnimations(
    animationManager: ShapeAnimationManager,
    displayLines: Map<Offset, Shape.Line>,
    displayCircles: Map<Offset, Shape.Circle>,
    displayTriangles: Map<Offset, Shape.Triangle>,
    displayRectangles: Map<Offset, Shape.Rectangle>,
    displayPolygons: Map<Offset, Shape.Polygon>
) {
    val currentTime = System.currentTimeMillis()

    animationManager.animatedShapes.forEach { (_, animatedShape) ->
        val elapsed = currentTime - animatedShape.startTime
        val progress = (elapsed.toFloat() / animatedShape.duration.toFloat()).coerceIn(0f, 1f)

        if (progress < 1f) {
            val easedProgress = animationManager.easeInOutCubic(progress)
            val scale = animationManager.lerp(animatedShape.startScale, animatedShape.endScale, easedProgress)
            val alpha = animationManager.lerp(animatedShape.startAlpha, animatedShape.endAlpha, easedProgress)

            val key = animatedShape.key
            val highlightColor = animatedShape.highlightColor

            val parts = key.split("_")
            if (parts.size >= 3) {
                val x = parts[1].toFloatOrNull() ?: 0f
                val y = parts[2].toFloatOrNull() ?: 0f
                val center = Offset(x, y)

                val pulseAlpha = alpha * (0.5f + 0.5f * sin((progress * PI * 4).toDouble()).toFloat())

                drawCircle(
                    color = highlightColor.copy(alpha = pulseAlpha * 0.3f),
                    radius = 30f * scale,
                    center = center
                )
                drawCircle(
                    color = Color.Companion.White.copy(alpha = pulseAlpha * 0.6f),
                    radius = 20f * scale,
                    center = center
                )
                drawCircle(
                    color = highlightColor.copy(alpha = pulseAlpha * 0.8f),
                    radius = 10f * scale,
                    center = center
                )
            }
        }
    }
}