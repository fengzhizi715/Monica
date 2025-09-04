package cn.netdiscovery.monica.ui.controlpanel.doodle

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.IntSize
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.doodle.model.PathProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.doodle.DoodleViewModel
 * @author: Tony Shen
 * @date: 2024/5/25 20:49
 * @version: V1.0 <描述当前版本功能>
 */
class DoodleViewModel {
    
    private val logger: Logger = LoggerFactory.getLogger(DoodleViewModel::class.java)

    fun saveCanvasToBitmap(
        density: Density, 
        paths: List<Pair<Path, PathProperties>>, 
        image: ImageBitmap, 
        state: ApplicationState
    ) {
        logger.info("开始保存涂鸦到图片，路径数量: ${paths.size}")
        
        val bitmapWidth = image.width
        val bitmapHeight = image.height
        
        logger.info("原始图片尺寸: ${bitmapWidth}x${bitmapHeight}")

        val drawScope = CanvasDrawScope()
        val size = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val canvas = Canvas(image)

        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size,
        ) {
            state.closePreviewWindow()

            // 先绘制原始图片
            drawImage(image = image, dstSize = IntSize(bitmapWidth, bitmapHeight))

            // 直接绘制路径，因为现在路径已经是基于原始图片尺寸的
            paths.forEach { pathPair ->
                val path = pathPair.first
                val property = pathPair.second
                
                if (!property.eraseMode) {
                    drawPath(
                        color = property.color,
                        path = path,
                        style = Stroke(
                            width = property.strokeWidth,
                            cap = property.strokeCap,
                            join = property.strokeJoin
                        )
                    )
                } else {
                    drawPath(
                        color = Color.Transparent,
                        path = path,
                        style = Stroke(
                            width = property.strokeWidth,
                            cap = property.strokeCap,
                            join = property.strokeJoin
                        ),
                        blendMode = BlendMode.Clear
                    )
                }
            }
        }

        state.addQueue(state.currentImage!!)
        state.currentImage = image.toAwtImage()
        logger.info("涂鸦保存完成")
    }
}