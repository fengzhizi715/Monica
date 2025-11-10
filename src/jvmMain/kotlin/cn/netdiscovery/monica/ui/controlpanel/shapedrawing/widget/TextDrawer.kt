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
            // pos.y 是文本的中心位置，我们希望文字垂直居中显示
            // 由于Skia的drawTextLine中Y坐标是基线位置，我们需要计算正确的基线
            val fontHeight = if (index % 2 == 0) fontSize else (fontSize - 10)
            
            // 文字应该垂直居中显示在 pos.y 位置
            // 对于大多数字体，基线大约在字体高度的 70-80% 位置
            // 要让文本中心在 pos.y，基线应该在 pos.y + (fontHeight * 0.3) 左右
            // 但考虑到不同字体的差异，使用更精确的计算：
            // 文本中心 = 基线 - fontHeight * 0.7
            // 所以：基线 = 文本中心 + fontHeight * 0.7 = pos.y + fontHeight * 0.7
            // 但这样会让文本偏下，应该使用：基线 = pos.y + fontHeight * 0.3
            val drawY = pos.y + fontHeight * 0.3f
            
            println("绘制文本: '$str' 在 ($drawX, $drawY)")
            println("  - 原始位置: $pos")
            println("  - 文字宽度: $textWidth")
            println("  - 字体高度: $fontHeight")
            println("  - X居中计算: pos.x(${pos.x}) - textWidth/2(${textWidth/2}) = $drawX")
            println("  - Y基线计算: pos.y(${pos.y}) + fontHeight*0.3(${fontHeight*0.3f}) = $drawY")
            
            canvas.nativeCanvas.drawTextLine(line, drawX, drawY, paint)
            current += line.width
        }
        println("=== TextDrawer.text 结束 ===")
    }
}