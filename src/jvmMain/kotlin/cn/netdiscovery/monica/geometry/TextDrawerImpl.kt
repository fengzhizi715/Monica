package cn.netdiscovery.monica.geometry

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
 *          cn.netdiscovery.monica.geometry.TextDrawerImpl
 * @author: Tony Shen
 * @date: 2024/11/20 11:59
 * @version: V1.0 <描述当前版本功能>
 */
object TextDrawerImpl: TextDrawer {

    override fun text(canvas: Canvas, pos: Offset, text: List<String>, color: Color) {
        val paint = Paint()
        paint.color = color.toArgb()
        val font = Font(null, 40f)
        val subscript = Font(null, 30f)
        var current = pos.x
        text.forEachIndexed { index, str ->
            val line = TextLine.make(str, if (index % 2 == 0) font else subscript)
            canvas.nativeCanvas.drawTextLine(line, current + 12f, pos.y + if (index % 2 == 0) 12f else 24f, paint)
            current += line.width
        }
    }
}