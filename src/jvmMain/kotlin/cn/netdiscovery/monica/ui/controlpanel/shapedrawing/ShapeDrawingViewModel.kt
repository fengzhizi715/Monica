package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Border
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Style
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Line
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

    fun saveCanvasToBitmap(density: Density, lines: Map<Offset, Line>,
                           image: ImageBitmap, state: ApplicationState) {

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
            size = size,
        ) {
            state.closeWindows()

            lines.forEach {

                val line = it.value

                if (line.from != Offset.Unspecified && line.to != Offset.Unspecified) {
                    canvasDrawer.line(line.from,line.to, Style(null, line.shapeProperties.color, Border.Line, null, fill = true, scale = 1f, bounded = true))
                }
            }
        }

        state.addQueue(state.currentImage!!)
        state.currentImage = image.toAwtImage()
    }
}