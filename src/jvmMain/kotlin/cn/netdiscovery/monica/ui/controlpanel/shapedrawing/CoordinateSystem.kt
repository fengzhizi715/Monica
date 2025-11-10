package cn.netdiscovery.monica.ui.controlpanel.shapedrawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 坐标系统工具类
 * 统一处理坐标转换、边界检查和验证
 * 
 * @author Tony Shen
 * @date 2025/9/1 16:09
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
     * @param dragOffset 拖拽偏移量（像素，相对于画布/图像显示中心）
     * @param imageWidth 图像显示宽度（像素）
     * @param imageHeight 图像显示高度（像素）
     * @param density 屏幕密度
     * @param textFieldWidth 文本输入框宽度（dp）
     * @param textFieldHeight 文本输入框高度（dp）
     * @param fontSize 字体大小（用于文本居中对齐）
     * @return 文本在Canvas中的位置（Canvas坐标，以Canvas左上角为原点，像素）
     */
    fun calculateTextPosition(
        dragOffset: Offset,
        imageWidth: Int,
        imageHeight: Int,
        density: Density,
        textFieldWidth: Float = 250f,
        textFieldHeight: Float = 130f,
        fontSize: Float = 40f
    ): Offset {
        // dragOffset 是文本输入框中心相对于画布中心的偏移（像素）
        // 画布显示尺寸等于图像显示尺寸（imageWidth x imageHeight）
        // 画布中心在 (imageWidth/2, imageHeight/2)
        val canvasCenterX = imageWidth / 2f
        val canvasCenterY = imageHeight / 2f
        
        // 文本中心位置 = 画布中心 + 拖拽偏移
        val textCenterX = canvasCenterX + dragOffset.x
        val textCenterY = canvasCenterY + dragOffset.y
        
        // 确保文本中心不会超出图像边界（考虑文本可能的最大宽度和高度）
        // 使用 fontSize 作为文本高度的近似值，文本宽度需要根据实际文本内容计算
        // 这里使用一个保守的估计值
        val estimatedTextHeight = fontSize * 1.2f // 字体高度的1.2倍作为安全边距
        val textFieldWidthPx = textFieldWidth * density.density
        val estimatedTextWidth = textFieldWidthPx * 0.8f // 使用输入框宽度的80%作为文本宽度的估计
        
        val clampedX = textCenterX.coerceIn(estimatedTextWidth / 2f, imageWidth - estimatedTextWidth / 2f)
        val clampedY = textCenterY.coerceIn(estimatedTextHeight / 2f, imageHeight - estimatedTextHeight / 2f)
        
        val canvasPosition = Offset(clampedX, clampedY)
        
        logger.info("文本位置计算: 拖拽偏移=$dragOffset, Canvas中心=($canvasCenterX, $canvasCenterY), 文本中心=($textCenterX, $textCenterY), 修正位置=($clampedX, $clampedY), 最终Canvas位置=$canvasPosition")
        
        return canvasPosition
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
