package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import kotlin.math.cos
import kotlin.math.sin

/**
 * 图像层控制点渲染器
 * 负责绘制图像层的控制点、旋转手柄和边界框
 */
object ImageLayerControlRenderer {
    
    // 控制点大小（像素）
    private const val CONTROL_POINT_SIZE = 8f
    // 旋转手柄长度（像素）
    private const val ROTATION_HANDLE_LENGTH = 30f
    // 控制点颜色
    private val CONTROL_POINT_COLOR = Color(0xFF2196F3)
    // 旋转手柄颜色
    private val ROTATION_HANDLE_COLOR = Color(0xFF4CAF50)
    // 边界框颜色
    private val BOUNDARY_COLOR = Color(0xFF2196F3)
    
    /**
     * 计算图像层的边界框（考虑变换后的位置）
     */
    fun calculateImageBounds(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float
    ): Rect? {
        val bitmap = layer.image ?: return null
        
        if (bitmap.width <= 0 || bitmap.height <= 0) return null
        
        // 判断是否为背景图层
        val isBackgroundLayer = layer.name == "背景图层"
        
        if (isBackgroundLayer) {
            // 背景图层填充整个画布
            return Rect(Offset.Zero, Size(canvasWidth, canvasHeight))
        }
        
        // 计算适应和居中后的尺寸（与 ImageLayer.render() 逻辑一致）
        val scaleX = canvasWidth / bitmap.width
        val scaleY = canvasHeight / bitmap.height
        val fitScale = minOf(scaleX, scaleY).coerceAtMost(1f)
        
        val scaledWidth = bitmap.width * fitScale
        val scaledHeight = bitmap.height * fitScale
        
        val centerOffsetX = (canvasWidth - scaledWidth) / 2f
        val centerOffsetY = (canvasHeight - scaledHeight) / 2f
        
        // 应用用户定义的变换
        val transform = layer.transform
        
        // 计算变换后的四个角点
        // 注意：在 ImageLayer.render() 中，变换顺序是（从内到外）：
        // 1. 用户缩放（相对于 pivot）
        // 2. 用户旋转（相对于 pivot）
        // 3. 用户平移
        // 4. 自动缩放（fitScale）
        // 5. 自动平移（centerOffset）
        
        // 先计算原始图像的四个角点（在图像坐标系中，0,0 到 width,height）
        val imageTopLeft = Offset(0f, 0f)
        val imageTopRight = Offset(bitmap.width.toFloat(), 0f)
        val imageBottomLeft = Offset(0f, bitmap.height.toFloat())
        val imageBottomRight = Offset(bitmap.width.toFloat(), bitmap.height.toFloat())
        val imageCenter = Offset(bitmap.width / 2f, bitmap.height / 2f)
        
        // 计算 pivot（在图像坐标系中）
        val pivot = if (transform.pivot == Offset.Zero) {
            imageCenter
        } else {
            imageCenter + transform.pivot
        }
        
        // 1. 应用用户缩放（相对于 pivot）
        val scaleXFinal = transform.scaleX
        val scaleYFinal = transform.scaleY
        val scaledTopLeft = applyScale(imageTopLeft, pivot, scaleXFinal, scaleYFinal)
        val scaledTopRight = applyScale(imageTopRight, pivot, scaleXFinal, scaleYFinal)
        val scaledBottomLeft = applyScale(imageBottomLeft, pivot, scaleXFinal, scaleYFinal)
        val scaledBottomRight = applyScale(imageBottomRight, pivot, scaleXFinal, scaleYFinal)
        
        // 2. 应用用户旋转（相对于 pivot）
        val rotation = transform.rotation
        val rotatedTopLeft = applyRotation(scaledTopLeft, pivot, rotation)
        val rotatedTopRight = applyRotation(scaledTopRight, pivot, rotation)
        val rotatedBottomLeft = applyRotation(scaledBottomLeft, pivot, rotation)
        val rotatedBottomRight = applyRotation(scaledBottomRight, pivot, rotation)
        
        // 3. 应用用户平移
        val translation = transform.translation
        val translatedTopLeft = rotatedTopLeft + translation
        val translatedTopRight = rotatedTopRight + translation
        val translatedBottomLeft = rotatedBottomLeft + translation
        val translatedBottomRight = rotatedBottomRight + translation
        
        // 4. 应用自动缩放（fitScale）
        val autoScaledTopLeft = Offset(translatedTopLeft.x * fitScale, translatedTopLeft.y * fitScale)
        val autoScaledTopRight = Offset(translatedTopRight.x * fitScale, translatedTopRight.y * fitScale)
        val autoScaledBottomLeft = Offset(translatedBottomLeft.x * fitScale, translatedBottomLeft.y * fitScale)
        val autoScaledBottomRight = Offset(translatedBottomRight.x * fitScale, translatedBottomRight.y * fitScale)
        
        // 5. 应用自动平移（centerOffset）
        val finalTopLeft = autoScaledTopLeft + Offset(centerOffsetX, centerOffsetY)
        val finalTopRight = autoScaledTopRight + Offset(centerOffsetX, centerOffsetY)
        val finalBottomLeft = autoScaledBottomLeft + Offset(centerOffsetX, centerOffsetY)
        val finalBottomRight = autoScaledBottomRight + Offset(centerOffsetX, centerOffsetY)
        
        // 计算边界框
        val minX = minOf(finalTopLeft.x, finalTopRight.x, finalBottomLeft.x, finalBottomRight.x)
        val maxX = maxOf(finalTopLeft.x, finalTopRight.x, finalBottomLeft.x, finalBottomRight.x)
        val minY = minOf(finalTopLeft.y, finalTopRight.y, finalBottomLeft.y, finalBottomRight.y)
        val maxY = maxOf(finalTopLeft.y, finalTopRight.y, finalBottomLeft.y, finalBottomRight.y)
        
        return Rect(
            offset = Offset(minX, minY),
            size = Size(maxX - minX, maxY - minY)
        )
    }
    
    /**
     * 计算图像层的中心点（考虑变换后）
     */
    fun calculateImageCenter(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float
    ): Offset? {
        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight) ?: return null
        return Offset(bounds.center.x, bounds.center.y)
    }
    
    /**
     * 计算旋转手柄的位置
     */
    fun calculateRotationHandlePosition(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float
    ): Offset? {
        val center = calculateImageCenter(layer, canvasWidth, canvasHeight) ?: return null
        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight) ?: return null
        
        // 旋转手柄在图像上方中心
        val handleOffset = Offset(0f, -bounds.height / 2f - ROTATION_HANDLE_LENGTH)
        
        return center + handleOffset
    }
    
    /**
     * 计算所有控制点的位置（四个角和旋转手柄）
     */
    fun calculateControlPoints(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float
    ): List<ControlPoint> {
        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight) ?: return emptyList()
        val rotationHandle = calculateRotationHandlePosition(layer, canvasWidth, canvasHeight)
        
        val points = mutableListOf<ControlPoint>()
        
        // 四个角的控制点
        points.add(ControlPoint(ControlPointType.CORNER_TOP_LEFT, bounds.topLeft))
        points.add(ControlPoint(ControlPointType.CORNER_TOP_RIGHT, bounds.topRight))
        points.add(ControlPoint(ControlPointType.CORNER_BOTTOM_LEFT, bounds.bottomLeft))
        points.add(ControlPoint(ControlPointType.CORNER_BOTTOM_RIGHT, bounds.bottomRight))
        
        // 旋转手柄
        if (rotationHandle != null) {
            points.add(ControlPoint(ControlPointType.ROTATION_HANDLE, rotationHandle))
        }
        
        return points
    }
    
    /**
     * 绘制图像层的控制点和边界框
     */
    fun drawControls(
        drawScope: DrawScope,
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float
    ) {
        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight) ?: return
        val controlPoints = calculateControlPoints(layer, canvasWidth, canvasHeight)
        
        // 绘制边界框
        drawScope.drawRect(
            color = BOUNDARY_COLOR.copy(alpha = 0.5f),
            style = Stroke(width = 1f),
            topLeft = bounds.topLeft,
            size = bounds.size
        )
        
        // 绘制控制点
        controlPoints.forEach { point ->
            val color = when (point.type) {
                ControlPointType.ROTATION_HANDLE -> ROTATION_HANDLE_COLOR
                else -> CONTROL_POINT_COLOR
            }
            
            drawScope.drawCircle(
                color = color,
                radius = CONTROL_POINT_SIZE,
                center = point.position
            )
            
            // 绘制控制点外圈
            drawScope.drawCircle(
                color = Color.White,
                radius = CONTROL_POINT_SIZE + 1f,
                style = Stroke(width = 1f),
                center = point.position
            )
        }
        
        // 绘制旋转手柄连线
        val rotationHandle = controlPoints.find { it.type == ControlPointType.ROTATION_HANDLE }
        val center = calculateImageCenter(layer, canvasWidth, canvasHeight)
        if (rotationHandle != null && center != null) {
            drawScope.drawLine(
                color = ROTATION_HANDLE_COLOR.copy(alpha = 0.5f),
                start = center,
                end = rotationHandle.position,
                strokeWidth = 1f
            )
        }
    }
    
    /**
     * 检查点是否在控制点附近
     */
    fun hitTestControlPoint(
        point: Offset,
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float
    ): ControlPoint? {
        val controlPoints = calculateControlPoints(layer, canvasWidth, canvasHeight)
        val hitRadius = CONTROL_POINT_SIZE * 2f
        
        return controlPoints.firstOrNull { controlPoint ->
            val distance = (point - controlPoint.position).getDistance()
            distance <= hitRadius
        }
    }
    
    // 辅助函数：应用缩放
    private fun applyScale(point: Offset, pivot: Offset, scaleX: Float, scaleY: Float): Offset {
        val translated = point - pivot
        val scaled = Offset(translated.x * scaleX, translated.y * scaleY)
        return scaled + pivot
    }
    
    // 辅助函数：应用旋转
    private fun applyRotation(point: Offset, pivot: Offset, rotation: Float): Offset {
        val translated = point - pivot
        val rad = Math.toRadians(rotation.toDouble())
        val cos = cos(rad).toFloat()
        val sin = sin(rad).toFloat()
        val rotated = Offset(
            translated.x * cos - translated.y * sin,
            translated.x * sin + translated.y * cos
        )
        return rotated + pivot
    }
}

/**
 * 控制点类型
 */
enum class ControlPointType {
    CORNER_TOP_LEFT,
    CORNER_TOP_RIGHT,
    CORNER_BOTTOM_LEFT,
    CORNER_BOTTOM_RIGHT,
    ROTATION_HANDLE
}

/**
 * 控制点数据
 */
data class ControlPoint(
    val type: ControlPointType,
    val position: Offset
)

