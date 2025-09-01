package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import cn.netdiscovery.monica.ui.controlpanel.ai.AIViewModel
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.sqrt
import kotlin.math.pow

/**
 * 坐标系统工具类
 * 统一处理坐标转换、边界检查和验证
 * 
 * @author Tony Shen
 * @date 2024/11/21 16:09
 * @version V1.0
 */
object CoordinateSystem {
    private val logger: Logger = logger<CoordinateSystem>()
    
    /**
     * 坐标验证结果
     */
    data class ValidationResult(
        val isValid: Boolean,
        val message: String = "",
        val correctedOffset: Offset? = null
    )
    
    /**
     * 验证坐标是否有效
     */
    fun validateOffset(offset: Offset, imageWidth: Int, imageHeight: Int): ValidationResult {
        return when {
            offset == Offset.Unspecified -> {
                ValidationResult(false, "坐标未指定")
            }
            offset.x.isNaN() || offset.y.isNaN() -> {
                ValidationResult(false, "坐标包含NaN值")
            }
            offset.x.isInfinite() || offset.y.isInfinite() -> {
                ValidationResult(false, "坐标包含无穷值")
            }
            offset.x < 0 || offset.y < 0 -> {
                ValidationResult(false, "坐标超出图像边界（负值）")
            }
            offset.x > imageWidth || offset.y > imageHeight -> {
                ValidationResult(false, "坐标超出图像边界")
            }
            else -> {
                ValidationResult(true, "坐标有效")
            }
        }
    }
    
    /**
     * 计算文本位置（考虑文本尺寸和边界）
     * @param dragOffset 拖拽偏移量
     * @param imageWidth 图像宽度
     * @param imageHeight 图像高度
     * @param density 屏幕密度
     * @param textFieldWidth 文本输入框宽度
     * @param textFieldHeight 文本输入框高度
     * @return 文本在图像中的位置
     */
    fun calculateTextPosition(
        dragOffset: Offset,
        imageWidth: Int,
        imageHeight: Int,
        density: Density,
        textFieldWidth: Float = 250f,
        textFieldHeight: Float = 130f
    ): Offset {
        val halfWidth = imageWidth / 2f
        val halfHeight = imageHeight / 2f
        
        // 计算文本输入框的实际像素尺寸
        val textFieldWidthPx = textFieldWidth * density.density
        val textFieldHeightPx = textFieldHeight * density.density
        
        // 计算文本在图像中的位置（文本左上角）
        val x = halfWidth + dragOffset.x - textFieldWidthPx / 2
        val y = halfHeight + dragOffset.y - textFieldHeightPx / 2
        
        // 确保文本不会超出图像边界
        val clampedX = x.coerceIn(0f, imageWidth - textFieldWidthPx)
        val clampedY = y.coerceIn(0f, imageHeight - textFieldHeightPx)
        
        logger.info("文本位置计算: 拖拽偏移=$dragOffset, 图像尺寸=${imageWidth}x${imageHeight}, 密度=${density.density}, 文本框尺寸=${textFieldWidthPx}x${textFieldHeightPx}, 计算位置=($x, $y), 最终位置=(${clampedX}, ${clampedY})")
        
        return Offset(clampedX, clampedY)
    }
    
    /**
     * 检查点是否在图像范围内
     */
    fun isPointInImage(point: Offset, imageWidth: Int, imageHeight: Int): Boolean {
        return point.x >= 0 && point.x <= imageWidth && 
               point.y >= 0 && point.y <= imageHeight
    }
    
    /**
     * 计算两点之间的距离
     */
    fun calculateDistance(point1: Offset, point2: Offset): Float {
        return sqrt((point2.x - point1.x).pow(2) + (point2.y - point1.y).pow(2))
    }
    
    /**
     * 计算圆的半径
     */
    fun calculateCircleRadius(center: Offset, pointOnCircle: Offset): Float {
        return calculateDistance(center, pointOnCircle)
    }
    
    /**
     * 验证形状的边界
     */
    fun validateShapeBoundary(
        points: List<Offset>, 
        imageWidth: Int, 
        imageHeight: Int
    ): ValidationResult {
        if (points.isEmpty()) {
            return ValidationResult(false, "形状没有顶点")
        }
        
        val invalidPoints = points.filter { !isPointInImage(it, imageWidth, imageHeight) }
        
        return if (invalidPoints.isEmpty()) {
            ValidationResult(true, "形状边界有效")
        } else {
            ValidationResult(false, "形状包含超出边界的点: $invalidPoints")
        }
    }
}
