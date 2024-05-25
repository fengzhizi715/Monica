package cn.netdiscovery.monica.ui.controlpanel.doodle

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.state.ApplicationState

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.doodle.DoodleViewModel
 * @author: Tony Shen
 * @date: 2024/5/25 20:49
 * @version: V1.0 <描述当前版本功能>
 */
class DoodleViewModel {

    fun saveCanvasToBitmap(density:Density, paths: List<Pair<Path, PathProperties>>, image: ImageBitmap, state:ApplicationState) {

        val bitmapWidth = image.width
        val bitmapHeight = image.height

        val drawScope = CanvasDrawScope()
        val size = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val canvas = Canvas(image)

        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size,
        ) {
            state.togglePreviewWindow(false)

            paths.forEach {

                val path = it.first
                val property = it.second

                drawPath(
                    color = property.color,
                    path = path,
                    style = Stroke(
                        width = property.strokeWidth,
                        cap = property.strokeCap,
                        join = property.strokeJoin
                    )
                )
            }
        }

        state.addQueue(state.currentImage!!)
        state.currentImage = image.toAwtImage()
    }
}