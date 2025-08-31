package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*

/**
 * 文本绘制器接口
 * 定义了文本绘制的契约
 */
interface TextDrawer {
    /**
     * 在指定位置绘制文本
     * 
     * @param canvas 画布
     * @param pos 位置
     * @param text 文本列表
     * @param color 颜色
     * @param fontSize 字体大小
     */
    fun text(canvas: Canvas, pos: Offset, text: List<String>, color: Color, fontSize: Float)
}

/**
 * 画布绘制器
 * 实现了Drawer接口，提供具体的绘制功能
 * 
 * @param textDrawer 文本绘制器
 * @param canvas 画布对象
 * 
 * @author Tony Shen
 * @date 2024/11/20 11:07
 * @version V1.0
 */
class CanvasDrawer(
    private val textDrawer: TextDrawer, 
    private val canvas: Canvas
) : Drawer {

    override fun point(offset: Offset, color: Color) {
        try {
            canvas.drawCircle(offset, 4f, Paint().apply { 
                this.color = color 
            })
        } catch (e: Exception) {
            // 记录错误但不中断绘制
            println("Error drawing point at $offset: ${e.message}")
        }
    }

    override fun circle(center: Offset, radius: Float, style: Style) {
        try {
            style.styled { paint ->
                canvas.drawCircle(center, radius, paint)
            }
        } catch (e: Exception) {
            println("Error drawing circle at $center with radius $radius: ${e.message}")
        }
    }

    override fun line(from: Offset, to: Offset, style: Style) {
        try {
            style.styled { paint ->
                canvas.drawLine(from, to, paint)
            }
        } catch (e: Exception) {
            println("Error drawing line from $from to $to: ${e.message}")
        }
    }

    override fun polygon(points: List<Offset>, style: Style) {
        if (points.size < 2) {
            println("Warning: Polygon must have at least 2 points")
            return
        }
        
        try {
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
                close()
            }
            
            style.styled { paint ->
                canvas.drawPath(path, paint)
            }
        } catch (e: Exception) {
            println("Error drawing polygon with ${points.size} points: ${e.message}")
        }
    }

    override fun text(pos: Offset, text: List<String>, color: Color, fontSize: Float) {
        try {
            textDrawer.text(canvas, pos, text, color, fontSize)
        } catch (e: Exception) {
            println("Error drawing text at $pos: ${e.message}")
        }
    }

    override fun angle(center: Offset, from: Float, to: Float, style: Style) {
        try {
            val rect = Rect(center, 50f * style.scale)
            style.styled { paint ->
                canvas.drawArcRad(rect, -from, from - to, true, paint)
            }
        } catch (e: Exception) {
            println("Error drawing angle at $center: ${e.message}")
        }
    }
    
    /**
     * 绘制矩形
     * 
     * @param rect 矩形区域
     * @param style 样式
     */
    fun rectangle(rect: Rect, style: Style) {
        try {
            val path = Path().apply {
                addRect(rect)
            }
            
            style.styled { paint ->
                canvas.drawPath(path, paint)
            }
        } catch (e: Exception) {
            println("Error drawing rectangle: ${e.message}")
        }
    }
    
    /**
     * 绘制椭圆
     * 
     * @param rect 椭圆边界矩形
     * @param style 样式
     */
    fun ellipse(rect: Rect, style: Style) {
        try {
            style.styled { paint ->
                canvas.drawOval(rect, paint)
            }
        } catch (e: Exception) {
            println("Error drawing ellipse: ${e.message}")
        }
    }
}

/**
 * Style扩展函数：应用样式到绘制操作
 * 
 * @param action 绘制操作，接收Paint参数
 */
private fun Style.styled(action: (Paint) -> Unit) {
    // 绘制填充
    if (fill) {
        action(Paint().apply {
            color = this@styled.color
            style = PaintingStyle.Fill
            alpha = this@styled.alpha
        })
    }

    // 绘制边框
    if (border != Border.No) {
        action(Paint().apply {
            color = this@styled.color
            style = PaintingStyle.Stroke
            pathEffect = border.effect?.let { PathEffect.dashPathEffect(it) }
            strokeWidth = 4.2f * scale
            alpha = this@styled.alpha
        })
    }
}