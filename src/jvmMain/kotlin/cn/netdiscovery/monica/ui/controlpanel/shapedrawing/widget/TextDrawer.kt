package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget.TextDrawerImpl
 * @author: Tony Shen
 * @date: 2024/11/20 11:59
 * @version: V1.0 <描述当前版本功能>
 */
object TextDrawer: cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.TextDrawer {

    override fun text(canvas: Canvas, pos: Offset, text: List<String>, color: Color, fontSize: Float) {
        val paint = Paint()
        paint.color = color.toArgb()
        val font = Font(null, fontSize)
        val subscript = Font(null, fontSize - 10)
        var current = pos.x
        text.forEachIndexed { index, str ->
            val line = TextLine.make(str, if (index % 2 == 0) font else subscript)
            canvas.nativeCanvas.drawTextLine(line, current + 12f, pos.y + if (index % 2 == 0) 12f else 24f, paint)
            current += line.width
        }
    }
}