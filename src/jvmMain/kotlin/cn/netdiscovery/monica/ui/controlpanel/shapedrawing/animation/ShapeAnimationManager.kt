package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.animation

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 动画形状数据类
 */
data class AnimatedShape(
    val key: String,
    val shapeType: String,
    val startTime: Long,
    val duration: Long = 800L,
    val startScale: Float = 0.3f,
    val endScale: Float = 1.2f,
    val startAlpha: Float = 0.1f,
    val endAlpha: Float = 0.8f,
    val highlightColor: Color = Color.Cyan,
    val pulseEffect: Boolean = true
)

/**
 * 形状动画管理器
 * 负责管理形状绘制时的动画效果
 * 
 * @author Tony Shen
 * @date 2025/9/8
 * @version V1.0
 */
class ShapeAnimationManager {
    
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    
    // 动画状态管理
    var animatedShapes by mutableStateOf<Map<String, AnimatedShape>>(emptyMap())
        private set
    
    /**
     * 添加动画形状
     */
    fun addAnimatedShape(shapeType: String, key: Offset) {
        val shapeKey = "${shapeType}_${key.x}_${key.y}"
        val currentTime = System.currentTimeMillis()
        
        // 根据形状类型设置不同的动画参数
        val animationParams = when (shapeType) {
            "Circle" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 1000L,
                highlightColor = Color.Cyan
            )
            "Line" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 600L,
                highlightColor = Color.Magenta
            )
            "Triangle" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 800L,
                highlightColor = Color.Green
            )
            "Rectangle" -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                duration = 700L,
                highlightColor = Color.Blue
            )
            else -> AnimatedShape(
                key = shapeKey,
                shapeType = shapeType,
                startTime = currentTime,
                highlightColor = Color.Yellow
            )
        }
        
        animatedShapes = animatedShapes + (shapeKey to animationParams)
        
        // 动画结束后自动移除
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
            delay(animationParams.duration + 200)
            animatedShapes = animatedShapes - shapeKey
        }
        
        logger.info("添加动画形状: $shapeType")
    }
    
    /**
     * 缓动插值函数
     */
    fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + (end - start) * fraction
    }
    
    /**
     * 缓动函数 - 缓入缓出
     */
    fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4f * t * t * t
        } else {
            val temp = -2f * t + 2f
            1f - (temp * temp * temp) / 2f
        }
    }
    
    /**
     * 脉冲效果函数
     */
    fun pulseEffect(progress: Float): Float {
        return (kotlin.math.sin((progress * kotlin.math.PI * 4).toDouble()).toFloat() + 1f) / 2f
    }
    
    /**
     * 绘制动画高亮效果
     */
    fun DrawScope.drawAnimationHighlight(
        animatedShape: AnimatedShape,
        scale: Float,
        alpha: Float,
        displayLines: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Line>,
        displayCircles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Circle>,
        displayTriangles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Triangle>,
        displayRectangles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Rectangle>,
        displayPolygons: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Polygon>
    ) {
        val key = animatedShape.key
        val shapeType = animatedShape.shapeType
        val highlightColor = animatedShape.highlightColor
        
        // 添加脉冲效果
        val pulseAlpha = if (animatedShape.pulseEffect) {
            alpha * (0.5f + 0.5f * pulseEffect(scale))
        } else {
            alpha
        }
        
        // 根据形状类型获取位置和绘制动画效果
        when (shapeType) {
            "Line" -> {
                val lineKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                displayLines[lineKey]?.let { line ->
                    // 绘制多层效果
                    drawLine(
                        color = highlightColor.copy(alpha = pulseAlpha * 0.8f),
                        start = line.from,
                        end = line.to,
                        strokeWidth = 12f * scale,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = pulseAlpha * 0.6f),
                        start = line.from,
                        end = line.to,
                        strokeWidth = 6f * scale,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
            "Circle" -> {
                val circleKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                displayCircles[circleKey]?.let { circle ->
                    // 绘制外圈和内圈
                    drawCircle(
                        color = highlightColor.copy(alpha = pulseAlpha * 0.4f),
                        radius = circle.radius * scale * 1.2f,
                        center = circle.center,
                        style = Stroke(width = 4f * scale)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = pulseAlpha * 0.6f),
                        radius = circle.radius * scale,
                        center = circle.center,
                        style = Stroke(width = 2f * scale)
                    )
                }
            }
            "Triangle" -> {
                val triangleKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                displayTriangles[triangleKey]?.let { triangle ->
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(triangle.first.x, triangle.first.y)
                        lineTo(triangle.second?.x ?: triangle.first.x, triangle.second?.y ?: triangle.first.y)
                        lineTo(triangle.third?.x ?: triangle.first.x, triangle.third?.y ?: triangle.first.y)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = highlightColor.copy(alpha = pulseAlpha * 0.5f),
                        style = Stroke(width = 8f * scale)
                    )
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = pulseAlpha * 0.7f),
                        style = Stroke(width = 3f * scale)
                    )
                }
            }
            "Rectangle" -> {
                val rectKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                displayRectangles[rectKey]?.let { rect ->
                    val rectSize = androidx.compose.ui.geometry.Size(
                        rect.br.x - rect.tl.x,
                        rect.br.y - rect.tl.y
                    )
                    drawRect(
                        color = highlightColor.copy(alpha = pulseAlpha * 0.4f),
                        topLeft = rect.tl,
                        size = rectSize,
                        style = Stroke(width = 6f * scale)
                    )
                    drawRect(
                        color = Color.White.copy(alpha = pulseAlpha * 0.6f),
                        topLeft = rect.tl,
                        size = rectSize,
                        style = Stroke(width = 2f * scale)
                    )
                }
            }
            "Polygon" -> {
                val polygonKey = Offset(
                    key.split("_")[1].toFloat(),
                    key.split("_")[2].toFloat()
                )
                displayPolygons[polygonKey]?.let { polygon ->
                    if (polygon.points.isNotEmpty()) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(polygon.points[0].x, polygon.points[0].y)
                            polygon.points.drop(1).forEach { point ->
                                lineTo(point.x, point.y)
                            }
                            close()
                        }
                        drawPath(
                            path = path,
                            color = highlightColor.copy(alpha = pulseAlpha * 0.5f),
                            style = Stroke(width = 8f * scale)
                        )
                        drawPath(
                            path = path,
                            color = Color.White.copy(alpha = pulseAlpha * 0.7f),
                            style = Stroke(width = 3f * scale)
                        )
                    }
                }
            }
        }
    }
    
    /**
     * 绘制所有动画效果
     */
    fun DrawScope.drawAllAnimations(
        displayLines: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Line>,
        displayCircles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Circle>,
        displayTriangles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Triangle>,
        displayRectangles: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Rectangle>,
        displayPolygons: Map<Offset, cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.Polygon>
    ) {
        val currentTime = System.currentTimeMillis()
        animatedShapes.forEach { (key, animatedShape) ->
            val elapsed = currentTime - animatedShape.startTime
            val rawProgress = (elapsed.toFloat() / animatedShape.duration).coerceIn(0f, 1f)
            
            if (rawProgress < 1f) {
                // 使用缓动函数改进动画效果
                val easedProgress = easeInOutCubic(rawProgress)
                
                val scale = lerp(animatedShape.startScale, animatedShape.endScale, easedProgress)
                val alpha = lerp(animatedShape.startAlpha, animatedShape.endAlpha, easedProgress)
                
                // 绘制优化的动画高亮效果
                drawAnimationHighlight(
                    animatedShape, scale, alpha,
                    displayLines, displayCircles, displayTriangles, displayRectangles, displayPolygons
                )
            }
        }
    }
}
