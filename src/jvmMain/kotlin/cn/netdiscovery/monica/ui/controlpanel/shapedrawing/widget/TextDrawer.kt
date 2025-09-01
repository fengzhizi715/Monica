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
        println("=== TextDrawer.text 开始 ===")
        println("传入位置: $pos")
        println("文本内容: $text")
        println("字体大小: $fontSize")
        
        val paint = Paint()
        paint.color = color.toArgb()
        val font = Font(null, fontSize)
        val subscript = Font(null, fontSize - 10)
        var current = pos.x
        
        text.forEachIndexed { index, str ->
            val line = TextLine.make(str, if (index % 2 == 0) font else subscript)
            
            // 让文字显示在控件的中心位置
            // pos.x 是控件中心，我们需要减去文字宽度的一半来水平居中
            val textWidth = line.width
            val drawX = pos.x - textWidth / 2
            
            // 计算文本基线位置
            // pos.y 是控件的中心位置，我们希望文字显示在这个位置
            // 由于Skia的drawTextLine中Y坐标是基线位置，我们需要计算正确的基线
            val fontHeight = if (index % 2 == 0) fontSize else (fontSize - 10)
            
            // 文字应该显示在控件中心，所以：
            // 1. 文字的上边缘应该在 pos.y - fontHeight/2
            // 2. 文字的基线应该在 pos.y - fontHeight/2 + fontHeight * 0.7 (约70%的字体高度)
            val drawY = pos.y - fontHeight / 2 + fontHeight * 0.7f
            
            println("绘制文本: '$str' 在 ($drawX, $drawY)")
            println("  - 原始位置: $pos")
            println("  - 文字宽度: $textWidth")
            println("  - 字体高度: $fontHeight")
            println("  - X居中计算: pos.x(${pos.x}) - textWidth/2(${textWidth/2}) = $drawX")
            println("  - Y基线计算: pos.y(${pos.y}) - fontHeight/2(${fontHeight/2}) + fontHeight*0.7(${fontHeight*0.7f}) = $drawY")
            
            canvas.nativeCanvas.drawTextLine(line, drawX, drawY, paint)
            current += line.width
        }
        println("=== TextDrawer.text 结束 ===")
    }
}