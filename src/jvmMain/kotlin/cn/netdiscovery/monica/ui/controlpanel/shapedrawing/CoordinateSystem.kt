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
     * @param dragOffset 拖拽偏移量（画布坐标，以画布中心为原点）
     * @param imageWidth 图像宽度
     * @param imageHeight 图像高度
     * @param density 屏幕密度
     * @param textFieldWidth 文本输入框宽度
     * @param textFieldHeight 文本输入框高度
     * @param fontSize 字体大小（用于文本居中对齐）
     * @return 文本在Canvas中的位置（Canvas坐标，以Canvas左上角为原点）
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
        // 计算文本输入框的实际像素尺寸
        val textFieldWidthPx = textFieldWidth * density.density
        val textFieldHeightPx = textFieldHeight * density.density
        
        // 用户拖拽的是画布坐标（以画布中心为原点）
        // 我们需要将其转换为Canvas坐标（以Canvas左上角为原点）
        // Canvas的尺寸是 imageWidth x imageHeight，中心在 (imageWidth/2, imageHeight/2)
        val canvasCenterX = imageWidth / 2f
        val canvasCenterY = imageHeight / 2f
        
        // 将画布中心坐标转换为Canvas左上角坐标
        // 直接使用拖拽位置，让文本显示在拖拽位置
        val canvasX = canvasCenterX + dragOffset.x
        val canvasY = canvasCenterY + dragOffset.y
        
        // 确保文本不会超出图像边界
        val clampedX = canvasX.coerceIn(0f, imageWidth - textFieldWidthPx)
        val clampedY = canvasY.coerceIn(0f, imageHeight - textFieldHeightPx)
        
        val canvasPosition = Offset(clampedX, clampedY)
        
        logger.info("文本位置计算: 画布偏移=$dragOffset, Canvas中心=($canvasCenterX, $canvasCenterY), 计算位置=($canvasX, $canvasY), 修正位置=($clampedX, $clampedY), 最终Canvas位置=$canvasPosition")
        
        // 添加详细调试信息
        println("=== 文本位置计算详细分析 ===")
        println("1. 用户拖拽偏移: $dragOffset")
        println("2. Canvas中心: ($canvasCenterX, $canvasCenterY)")
        println("3. 计算位置: ($canvasX, $canvasY)")
        println("4. 修正位置: ($clampedX, $clampedY)")
        println("5. 最终Canvas位置: $canvasPosition")
        println("6. 文本框尺寸: ${textFieldWidthPx}x${textFieldHeightPx}")
        println("================================")
        
        return canvasPosition
    }
    
    /**
     * 将画布坐标转换为图像坐标
     * @param canvasOffset 画布坐标（以画布中心为原点）
     * @param imageWidth 图像宽度
     * @param imageHeight 图像高度
     * @return 图像坐标（以左上角为原点）
     */
    fun canvasToImage(canvasOffset: Offset, imageWidth: Int, imageHeight: Int): Offset {
        val halfWidth = imageWidth / 2f
        val halfHeight = imageHeight / 2f
        
        return Offset(
            x = halfWidth + canvasOffset.x,
            y = halfHeight + canvasOffset.y
        )
    }
    
    /**
     * 将图像坐标转换为画布坐标
     * @param imageOffset 图像坐标（以左上角为原点）
     * @param imageWidth 图像宽度
     * @param imageHeight 图像高度
     * @return 画布坐标（以画布中心为原点）
     */
    fun imageToCanvas(imageOffset: Offset, imageWidth: Int, imageHeight: Int): Offset {
        val halfWidth = imageWidth / 2f
        val halfHeight = imageHeight / 2f
        
        return Offset(
            x = imageOffset.x - halfWidth,
            y = imageOffset.y - halfHeight
        )
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
