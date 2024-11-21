package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.CanvasDrawer
 * @author: Tony Shen
 * @date: 2024/11/20 11:07
 * @version: V1.0 <描述当前版本功能>
 */
interface TextDrawer {
    fun text(canvas: Canvas, pos: Offset, text: List<String>, color: Color)
}

class CanvasDrawer(private val textDrawer: TextDrawer, private val canvas: Canvas): Drawer {

    override fun point(offset: Offset, color: Color) {
        canvas.drawCircle(offset, 9f, Paint().also { it.color = color })
    }

    override fun circle(center: Offset, radius: Float, style: Style) = style.styled {
        canvas.drawCircle(center, radius, it)
    }

    override fun line(from: Offset, to: Offset, style: Style) = style.styled {
        canvas.drawLine(from, to, it)
    }

    override fun polygon(points: List<Offset>, style: Style) = Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach {
            lineTo(it.x, it.y)
        }
        close()
    }.let { path ->
        style.styled {
            canvas.drawPath(path, it)
        }
    }

    override fun text(pos: Offset, text: List<String>, color: Color) =
        textDrawer.text(canvas, pos, text, color)

    override fun angle(center: Offset, from: Float, to: Float, style: Style) = style.styled {
        canvas.drawArcRad(Rect(center, 50*style.scale), -from, from - to, true, it)
    }
}

private fun Style.styled(action: (Paint) -> Unit) {
    if (fill)
        action(Paint().apply {
        color = this@styled.color
        style = PaintingStyle.Fill
//        alpha = 0.3f
    })
    if (border != Border.No)
        action(Paint().apply {
        color = this@styled.color
        style = PaintingStyle.Stroke
        pathEffect = border.effect?.let(PathEffect.Companion::dashPathEffect)
        strokeWidth = 4.2f
    })
}