package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.widget

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController
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
    private val CROP_BOUNDARY_COLOR = Color(0xFFFF9800)
    
    /**
     * 计算图像层的边界框（考虑变换后的位置）
     */
    fun calculateImageBounds(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): Rect? {
        val bitmap = layer.image ?: return null
        
        if (bitmap.width <= 0 || bitmap.height <= 0) return null
        
        // 判断是否为背景图层（使用常量，避免硬编码）
        val isBackgroundLayer = layer.name == EditorController.BACKGROUND_LAYER_NAME
        
        if (isBackgroundLayer) {
            // 背景图层填充整个画布
            return Rect(Offset.Zero, Size(canvasWidth, canvasHeight))
        }
        
        // 计算适应和居中后的尺寸（与 ImageLayer.render() 逻辑一致）
        // 如果提供了背景层尺寸，需要考虑背景层在画布上的显示比例
        val fitScale = if (backgroundSize != null) {
            val referenceWidth = backgroundSize.first
            val referenceHeight = backgroundSize.second
            
            // 计算背景层在画布上的缩放比例（背景层被拉伸到画布尺寸）
            val backgroundScaleX = canvasWidth / referenceWidth
            val backgroundScaleY = canvasHeight / referenceHeight
            
            // 计算基于背景层原始尺寸的缩放比例，确保图像尺寸不超过背景层尺寸
            val referenceScaleX = referenceWidth / bitmap.width
            val referenceScaleY = referenceHeight / bitmap.height
            val referenceFitScale = minOf(referenceScaleX, referenceScaleY).coerceAtMost(1f)
            
            // 图像层在背景层原始坐标系中的缩放比例
            // 然后需要乘以背景层在画布上的缩放比例，得到在画布坐标系中的缩放比例
            referenceFitScale * minOf(backgroundScaleX, backgroundScaleY)
        } else {
            // 没有背景层时，基于画布尺寸
            val canvasScaleX = canvasWidth / bitmap.width
            val canvasScaleY = canvasHeight / bitmap.height
            minOf(canvasScaleX, canvasScaleY).coerceAtMost(1f)
        }
        
        val scaledWidth = bitmap.width * fitScale
        val scaledHeight = bitmap.height * fitScale
        
        val centerOffsetX = (canvasWidth - scaledWidth) / 2f
        val centerOffsetY = (canvasHeight - scaledHeight) / 2f
        
        // 应用用户定义的变换
        val transform = layer.transform
        
        // 计算变换后的四个角点
        // 注意：在 ImageLayer.render() 中，withTransform 的执行顺序是（从外到内，后写的先执行）：
        // 1. 自动平移（centerOffset）- 最外层，最后执行
        // 2. 自动缩放（fitScale）- 将图像坐标系转换到适应后的坐标系
        // 3. 用户平移（在适应后的坐标系中，相对于适应后图像中心）
        // 4. 用户旋转（相对于 adaptedPivot，在适应后的坐标系中）
        // 5. 用户缩放（相对于 adaptedPivot，在适应后的坐标系中）- 最内层，最先执行
        
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
        
        // 将 pivot 转换到适应后的坐标系（用于用户变换）
        // 注意：在 ImageLayer.render() 中，用户变换是在适应后的坐标系中进行的
        // 所以这里需要先将图像坐标转换到适应后的坐标系，然后再应用用户变换
        val adaptedPivot = Offset(pivot.x * fitScale, pivot.y * fitScale)
        
        // 计算变换后的角点
        // 变换顺序：用户平移(translation) -> fitScale -> centerOffset
        // translation 在图像原始坐标系中，所以需要先应用 translation，然后应用 fitScale
        
        // 1. 应用用户平移（在图像原始坐标系中）
        val translation = transform.translation
        val translatedTopLeft = imageTopLeft + translation
        val translatedTopRight = imageTopRight + translation
        val translatedBottomLeft = imageBottomLeft + translation
        val translatedBottomRight = imageBottomRight + translation
        
        // 2. 应用用户缩放（相对于 pivot，在图像原始坐标系中）
        val scaleXFinal = transform.scaleX
        val scaleYFinal = transform.scaleY
        val scaledTopLeft = applyScale(translatedTopLeft, pivot, scaleXFinal, scaleYFinal)
        val scaledTopRight = applyScale(translatedTopRight, pivot, scaleXFinal, scaleYFinal)
        val scaledBottomLeft = applyScale(translatedBottomLeft, pivot, scaleXFinal, scaleYFinal)
        val scaledBottomRight = applyScale(translatedBottomRight, pivot, scaleXFinal, scaleYFinal)
        
        // 3. 应用用户旋转（相对于 pivot，在图像原始坐标系中）
        val rotation = transform.rotation
        val rotatedTopLeft = applyRotation(scaledTopLeft, pivot, rotation)
        val rotatedTopRight = applyRotation(scaledTopRight, pivot, rotation)
        val rotatedBottomLeft = applyRotation(scaledBottomLeft, pivot, rotation)
        val rotatedBottomRight = applyRotation(scaledBottomRight, pivot, rotation)
        
        // 4. 应用自动缩放（fitScale）- 将图像坐标系转换到适应后的坐标系
        val adaptedTopLeft = Offset(rotatedTopLeft.x * fitScale, rotatedTopLeft.y * fitScale)
        val adaptedTopRight = Offset(rotatedTopRight.x * fitScale, rotatedTopRight.y * fitScale)
        val adaptedBottomLeft = Offset(rotatedBottomLeft.x * fitScale, rotatedBottomLeft.y * fitScale)
        val adaptedBottomRight = Offset(rotatedBottomRight.x * fitScale, rotatedBottomRight.y * fitScale)
        
        // 5. 应用自动平移（centerOffset）- 在画布坐标系中
        val finalTopLeft = adaptedTopLeft + Offset(centerOffsetX, centerOffsetY)
        val finalTopRight = adaptedTopRight + Offset(centerOffsetX, centerOffsetY)
        val finalBottomLeft = adaptedBottomLeft + Offset(centerOffsetX, centerOffsetY)
        val finalBottomRight = adaptedBottomRight + Offset(centerOffsetX, centerOffsetY)
        
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
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): Offset? {
        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight, backgroundSize) ?: return null
        return Offset(bounds.center.x, bounds.center.y)
    }
    
    /**
     * 计算旋转手柄的位置
     */
    fun calculateRotationHandlePosition(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): Offset? {
        val center = calculateImageCenter(layer, canvasWidth, canvasHeight, backgroundSize) ?: return null
        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight, backgroundSize) ?: return null
        
        // 旋转手柄在图像上方中心
        val handleOffset = Offset(0f, -bounds.height / 2f - ROTATION_HANDLE_LENGTH)
        
        return center + handleOffset
    }
    
    /**
     * 计算所有控制点的位置（四个角、旋转手柄和裁剪控制点）
     */
    fun calculateControlPoints(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null,
        cropMode: Boolean = false
    ): List<ControlPoint> {
        if (cropMode) {
            return calculateCropControlPoints(layer, canvasWidth, canvasHeight, backgroundSize)
        }

        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight, backgroundSize) ?: return emptyList()
        val rotationHandle = calculateRotationHandlePosition(layer, canvasWidth, canvasHeight, backgroundSize)
        
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
        
        // 裁剪控制点（如果存在裁剪区域）
        // 注意：裁剪控制点的计算比较复杂，需要考虑所有变换
        // 这里先简化实现，后续可以优化
        val cropRect = layer.transform.cropRect
        if (cropRect != null) {
            // 裁剪区域在图像坐标系中，需要转换到画布坐标系
            // 简化实现：使用 bounds 作为参考，后续可以完善
            // TODO: 完善裁剪控制点的计算，考虑所有变换
        }
        
        return points
    }

    fun initializeCropRect(layer: ImageLayer) {
        val bitmap = layer.image ?: return
        if (layer.transform.cropRect == null) {
            layer.updateTransform(
                layer.transform.copy(
                    cropRect = Rect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
                )
            )
        }
    }

    fun updateCropRectFromControlPoint(
        layer: ImageLayer,
        controlPointType: ControlPointType,
        canvasPoint: Offset,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): Rect? {
        val bitmap = layer.image ?: return null
        val current = layer.transform.cropRect ?: Rect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val imagePoint = mapCanvasPointToImage(layer, canvasPoint, canvasWidth, canvasHeight, backgroundSize) ?: return current
        val clamped = Offset(
            x = imagePoint.x.coerceIn(0f, bitmap.width.toFloat()),
            y = imagePoint.y.coerceIn(0f, bitmap.height.toFloat())
        )
        val minSize = 8f

        var left = current.left
        var top = current.top
        var right = current.right
        var bottom = current.bottom

        when (controlPointType) {
            ControlPointType.CROP_TOP_LEFT -> {
                left = clamped.x.coerceAtMost(right - minSize)
                top = clamped.y.coerceAtMost(bottom - minSize)
            }
            ControlPointType.CROP_TOP_RIGHT -> {
                right = clamped.x.coerceAtLeast(left + minSize)
                top = clamped.y.coerceAtMost(bottom - minSize)
            }
            ControlPointType.CROP_BOTTOM_LEFT -> {
                left = clamped.x.coerceAtMost(right - minSize)
                bottom = clamped.y.coerceAtLeast(top + minSize)
            }
            ControlPointType.CROP_BOTTOM_RIGHT -> {
                right = clamped.x.coerceAtLeast(left + minSize)
                bottom = clamped.y.coerceAtLeast(top + minSize)
            }
            ControlPointType.CROP_TOP -> {
                top = clamped.y.coerceAtMost(bottom - minSize)
            }
            ControlPointType.CROP_BOTTOM -> {
                bottom = clamped.y.coerceAtLeast(top + minSize)
            }
            ControlPointType.CROP_LEFT -> {
                left = clamped.x.coerceAtMost(right - minSize)
            }
            ControlPointType.CROP_RIGHT -> {
                right = clamped.x.coerceAtLeast(left + minSize)
            }
            else -> return current
        }

        return Rect(left, top, right, bottom)
    }

    fun isCropControlPoint(type: ControlPointType): Boolean = when (type) {
        ControlPointType.CROP_TOP_LEFT,
        ControlPointType.CROP_TOP_RIGHT,
        ControlPointType.CROP_BOTTOM_LEFT,
        ControlPointType.CROP_BOTTOM_RIGHT,
        ControlPointType.CROP_TOP,
        ControlPointType.CROP_BOTTOM,
        ControlPointType.CROP_LEFT,
        ControlPointType.CROP_RIGHT -> true
        else -> false
    }
    
    /**
     * 绘制图像层的控制点和边界框
     */
    fun drawControls(
        drawScope: DrawScope,
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null,
        cropMode: Boolean = false
    ) {
        if (cropMode) {
            drawCropControls(drawScope, layer, canvasWidth, canvasHeight, backgroundSize)
            return
        }

        val bounds = calculateImageBounds(layer, canvasWidth, canvasHeight, backgroundSize) ?: return
        val controlPoints = calculateControlPoints(layer, canvasWidth, canvasHeight, backgroundSize, cropMode = false)
        
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
        val center = calculateImageCenter(layer, canvasWidth, canvasHeight, backgroundSize)
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
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null,
        cropMode: Boolean = false
    ): ControlPoint? {
        val controlPoints = calculateControlPoints(layer, canvasWidth, canvasHeight, backgroundSize, cropMode)
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

    private fun calculateCropControlPoints(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): List<ControlPoint> {
        initializeCropRect(layer)
        val cropRect = layer.transform.cropRect ?: return emptyList()
        val topLeft = mapImagePointToCanvas(layer, cropRect.topLeft, canvasWidth, canvasHeight, backgroundSize) ?: return emptyList()
        val topRight = mapImagePointToCanvas(layer, Offset(cropRect.right, cropRect.top), canvasWidth, canvasHeight, backgroundSize) ?: return emptyList()
        val bottomLeft = mapImagePointToCanvas(layer, Offset(cropRect.left, cropRect.bottom), canvasWidth, canvasHeight, backgroundSize) ?: return emptyList()
        val bottomRight = mapImagePointToCanvas(layer, cropRect.bottomRight, canvasWidth, canvasHeight, backgroundSize) ?: return emptyList()

        return listOf(
            ControlPoint(ControlPointType.CROP_TOP_LEFT, topLeft),
            ControlPoint(ControlPointType.CROP_TOP_RIGHT, topRight),
            ControlPoint(ControlPointType.CROP_BOTTOM_LEFT, bottomLeft),
            ControlPoint(ControlPointType.CROP_BOTTOM_RIGHT, bottomRight),
            ControlPoint(ControlPointType.CROP_TOP, Offset((topLeft.x + topRight.x) / 2f, (topLeft.y + topRight.y) / 2f)),
            ControlPoint(ControlPointType.CROP_BOTTOM, Offset((bottomLeft.x + bottomRight.x) / 2f, (bottomLeft.y + bottomRight.y) / 2f)),
            ControlPoint(ControlPointType.CROP_LEFT, Offset((topLeft.x + bottomLeft.x) / 2f, (topLeft.y + bottomLeft.y) / 2f)),
            ControlPoint(ControlPointType.CROP_RIGHT, Offset((topRight.x + bottomRight.x) / 2f, (topRight.y + bottomRight.y) / 2f))
        )
    }

    private fun drawCropControls(
        drawScope: DrawScope,
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ) {
        val controlPoints = calculateCropControlPoints(layer, canvasWidth, canvasHeight, backgroundSize)
        if (controlPoints.isEmpty()) return

        val topLeft = controlPoints.first { it.type == ControlPointType.CROP_TOP_LEFT }.position
        val topRight = controlPoints.first { it.type == ControlPointType.CROP_TOP_RIGHT }.position
        val bottomLeft = controlPoints.first { it.type == ControlPointType.CROP_BOTTOM_LEFT }.position
        val bottomRight = controlPoints.first { it.type == ControlPointType.CROP_BOTTOM_RIGHT }.position

        drawScope.drawLine(CROP_BOUNDARY_COLOR, topLeft, topRight, 1.5f)
        drawScope.drawLine(CROP_BOUNDARY_COLOR, topRight, bottomRight, 1.5f)
        drawScope.drawLine(CROP_BOUNDARY_COLOR, bottomRight, bottomLeft, 1.5f)
        drawScope.drawLine(CROP_BOUNDARY_COLOR, bottomLeft, topLeft, 1.5f)

        controlPoints.forEach { point ->
            drawScope.drawCircle(
                color = CROP_BOUNDARY_COLOR,
                radius = CONTROL_POINT_SIZE,
                center = point.position
            )
            drawScope.drawCircle(
                color = Color.White,
                radius = CONTROL_POINT_SIZE + 1f,
                style = Stroke(width = 1f),
                center = point.position
            )
        }
    }

    private fun mapImagePointToCanvas(
        layer: ImageLayer,
        imagePoint: Offset,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): Offset? {
        val bitmap = layer.image ?: return null
        if (bitmap.width <= 0 || bitmap.height <= 0 || canvasWidth <= 0 || canvasHeight <= 0) return null

        val fitScale = calculateFitScale(layer, canvasWidth, canvasHeight, backgroundSize)
        val scaledWidth = bitmap.width * fitScale
        val scaledHeight = bitmap.height * fitScale
        val centerOffsetX = (canvasWidth - scaledWidth) / 2f
        val centerOffsetY = (canvasHeight - scaledHeight) / 2f
        val imageCenter = Offset(bitmap.width / 2f, bitmap.height / 2f)
        val pivot = if (layer.transform.pivot == Offset.Zero) imageCenter else imageCenter + layer.transform.pivot

        val translated = if (layer.transform.translation != Offset.Zero) {
            val delta = layer.transform.translation
            imagePoint + delta
        } else {
            imagePoint
        }

        val scaled = applyScale(translated, pivot, layer.transform.scaleX, layer.transform.scaleY)
        val rotated = applyRotation(scaled, pivot, layer.transform.rotation)
        return Offset(
            x = rotated.x * fitScale + centerOffsetX,
            y = rotated.y * fitScale + centerOffsetY
        )
    }

    private fun mapCanvasPointToImage(
        layer: ImageLayer,
        canvasPoint: Offset,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): Offset? {
        val bitmap = layer.image ?: return null
        if (bitmap.width <= 0 || bitmap.height <= 0 || canvasWidth <= 0 || canvasHeight <= 0) return null

        val fitScale = calculateFitScale(layer, canvasWidth, canvasHeight, backgroundSize)
        val scaledWidth = bitmap.width * fitScale
        val scaledHeight = bitmap.height * fitScale
        val centerOffsetX = (canvasWidth - scaledWidth) / 2f
        val centerOffsetY = (canvasHeight - scaledHeight) / 2f
        val imageCenter = Offset(bitmap.width / 2f, bitmap.height / 2f)
        val pivot = if (layer.transform.pivot == Offset.Zero) imageCenter else imageCenter + layer.transform.pivot

        val uncentered = Offset(
            x = (canvasPoint.x - centerOffsetX) / fitScale,
            y = (canvasPoint.y - centerOffsetY) / fitScale
        )

        val unrotated = applyRotation(uncentered, pivot, -layer.transform.rotation)
        val unscaled = applyScale(
            point = unrotated,
            pivot = pivot,
            scaleX = if (layer.transform.scaleX == 0f) 1f else 1f / layer.transform.scaleX,
            scaleY = if (layer.transform.scaleY == 0f) 1f else 1f / layer.transform.scaleY
        )

        return if (layer.transform.translation != Offset.Zero) {
            unscaled - layer.transform.translation
        } else {
            unscaled
        }
    }

    private fun calculateFitScale(
        layer: ImageLayer,
        canvasWidth: Float,
        canvasHeight: Float,
        backgroundSize: Pair<Float, Float>? = null
    ): Float {
        val bitmap = layer.image ?: return 1f
        val isBackgroundLayer = layer.name == EditorController.BACKGROUND_LAYER_NAME
        if (isBackgroundLayer) return 1f

        return if (backgroundSize != null) {
            val referenceWidth = backgroundSize.first
            val referenceHeight = backgroundSize.second
            val backgroundScaleX = canvasWidth / referenceWidth
            val backgroundScaleY = canvasHeight / referenceHeight
            val referenceScaleX = referenceWidth / bitmap.width
            val referenceScaleY = referenceHeight / bitmap.height
            val referenceFitScale = minOf(referenceScaleX, referenceScaleY).coerceAtMost(1f)
            referenceFitScale * minOf(backgroundScaleX, backgroundScaleY)
        } else {
            val canvasScaleX = canvasWidth / bitmap.width
            val canvasScaleY = canvasHeight / bitmap.height
            minOf(canvasScaleX, canvasScaleY).coerceAtMost(1f)
        }
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
    ROTATION_HANDLE,
    CROP_TOP_LEFT,
    CROP_TOP_RIGHT,
    CROP_BOTTOM_LEFT,
    CROP_BOTTOM_RIGHT,
    CROP_TOP,
    CROP_BOTTOM,
    CROP_LEFT,
    CROP_RIGHT
}

/**
 * 控制点数据
 */
data class ControlPoint(
    val type: ControlPointType,
    val position: Offset
)
