package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.handler

import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.coordinate.CoordinateConverter
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.*
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state.ShapeDrawingState
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.CoordinateSystem
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 形状绘制事件处理器
 * 处理各种形状的绘制逻辑，分离业务逻辑
 * 
 * @author Tony Shen
 * @date 2024/12/19
 * @version V1.0
 */
class ShapeDrawingEventHandler(
    private val state: ShapeDrawingState,
    private val coordinateConverter: CoordinateConverter
) {
    
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 处理鼠标按下事件
     */
    fun handleMouseDown(position: Offset) {
        state.updatePosition(position)
        state.updateMotionEvent(MotionEvent.Down)
        
        when (state.currentShape) {
            ShapeEnum.Line -> handleLineDown(position)
            ShapeEnum.Circle -> handleCircleDown(position)
            ShapeEnum.Triangle -> handleTriangleDown(position)
            ShapeEnum.Rectangle -> handleRectangleDown(position)
            ShapeEnum.Polygon -> handlePolygonDown(position)
            else -> Unit
        }
    }
    
    /**
     * 处理鼠标移动事件
     */
    fun handleMouseMove(position: Offset): Map<Offset, Shape> {
        state.updatePosition(position)
        state.updateMotionEvent(MotionEvent.Move)
        
        return when (state.currentShape) {
            ShapeEnum.Line -> handleLineMove(position)
            ShapeEnum.Circle -> handleCircleMove(position)
            ShapeEnum.Triangle -> handleTriangleMove(position)
            ShapeEnum.Rectangle -> handleRectangleMove(position)
            ShapeEnum.Polygon -> handlePolygonMove(position)
            else -> emptyMap()
        }
    }
    
    /**
     * 处理鼠标抬起事件
     */
    fun handleMouseUp(position: Offset, bitmapWidth: Int, bitmapHeight: Int): Pair<Offset, Shape>? {
        state.updatePosition(position)
        state.updateMotionEvent(MotionEvent.Up)
        
        return when (state.currentShape) {
            ShapeEnum.Line -> handleLineUp(bitmapWidth, bitmapHeight)
            ShapeEnum.Circle -> handleCircleUp(bitmapWidth, bitmapHeight)
            ShapeEnum.Triangle -> handleTriangleUp(bitmapWidth, bitmapHeight)
            ShapeEnum.Rectangle -> handleRectangleUp(bitmapWidth, bitmapHeight)
            ShapeEnum.Polygon -> handlePolygonUp(bitmapWidth, bitmapHeight)
            else -> null
        }
    }
    
    // ========== 线段处理 ==========
    
    private fun handleLineDown(position: Offset) {
        if (state.previousPosition != position && state.currentLineStart == Offset.Unspecified) {
            state.updateLineState(start = position)
        } else if (state.currentLineStart != Offset.Unspecified) {
            state.updateLineState(end = position)
        }
    }
    
    private fun handleLineMove(position: Offset): Map<Offset, Shape> {
        state.updateLineState(end = position)
        return if (state.currentLineStart != Offset.Unspecified) {
            mapOf(state.currentLineStart to Line(state.currentLineStart, position, state.currentShapeProperty))
        } else emptyMap()
    }
    
    private fun handleLineUp(bitmapWidth: Int, bitmapHeight: Int): Pair<Offset, Shape>? {
        val startValidation = CoordinateSystem.validateOffset(state.currentLineStart, bitmapWidth, bitmapHeight)
        val endValidation = CoordinateSystem.validateOffset(state.currentLineEnd, bitmapWidth, bitmapHeight)
        
        return if (startValidation.isValid && endValidation.isValid) {
            val displayLine = Line(state.currentLineStart, state.currentLineEnd, state.currentShapeProperty)
            val originalLine = coordinateConverter.convertLineToOriginal(displayLine)
            state.addShape(state.currentLineStart, displayLine, originalLine)
            state.recordLastDrawnShape(state.currentLineStart, "Line")
            logger.info("添加线段: ${state.currentLineStart} -> ${state.currentLineEnd}")
            
            // 重置线段状态，准备下次绘制
            state.clearCurrentDrawingState()
            
            Pair(state.currentLineStart, displayLine)
        } else {
            logger.warn("线段坐标无效: ${startValidation.message}, ${endValidation.message}")
            null
        }
    }
    
    // ========== 圆形处理 ==========
    
    private fun handleCircleDown(position: Offset) {
        if (state.previousPosition != position && state.currentCircleCenter == Offset.Unspecified) {
            state.updateCircleState(center = position)
        }
    }
    
    private fun handleCircleMove(position: Offset): Map<Offset, Shape> {
        val radius = CoordinateSystem.calculateCircleRadius(state.currentCircleCenter, position)
        state.updateCircleState(radius = radius)
        return if (state.currentCircleCenter != Offset.Unspecified) {
            mapOf(state.currentCircleCenter to Circle(state.currentCircleCenter, radius, state.currentShapeProperty))
        } else emptyMap()
    }
    
    private fun handleCircleUp(bitmapWidth: Int, bitmapHeight: Int): Pair<Offset, Shape>? {
        val centerValidation = CoordinateSystem.validateOffset(state.currentCircleCenter, bitmapWidth, bitmapHeight)
        
        return if (centerValidation.isValid && state.currentCircleRadius > 0) {
            val displayCircle = Circle(state.currentCircleCenter, state.currentCircleRadius, state.currentShapeProperty)
            val originalCircle = coordinateConverter.convertCircleToOriginal(displayCircle)
            state.addShape(state.currentCircleCenter, displayCircle, originalCircle)
            state.recordLastDrawnShape(state.currentCircleCenter, "Circle")
            logger.info("添加圆形: 中心=${state.currentCircleCenter}, 半径=${state.currentCircleRadius}")
            
            // 重置圆形状态，准备下次绘制
            state.clearCurrentDrawingState()
            
            Pair(state.currentCircleCenter, displayCircle)
        } else {
            logger.warn("圆形坐标无效: ${centerValidation.message}")
            null
        }
    }
    
    // ========== 三角形处理 ==========
    
    private fun handleTriangleDown(position: Offset) {
        determineTriangleCoordinates()
    }
    
    private fun handleTriangleMove(position: Offset): Map<Offset, Shape> {
        determineTriangleCoordinates()
        return if (state.currentTriangleFirst != Offset.Unspecified && 
                   state.currentTriangleSecond != Offset.Unspecified && 
                   state.currentTriangleThird != Offset.Unspecified) {
            val triangle = Triangle(state.currentTriangleFirst, state.currentTriangleSecond, state.currentTriangleThird, state.currentShapeProperty)
            mapOf(state.currentTriangleFirst to triangle)
        } else emptyMap()
    }
    
    private fun handleTriangleUp(bitmapWidth: Int, bitmapHeight: Int): Pair<Offset, Shape>? {
        val firstValidation = CoordinateSystem.validateOffset(state.currentTriangleFirst, bitmapWidth, bitmapHeight)
        val secondValidation = CoordinateSystem.validateOffset(state.currentTriangleSecond, bitmapWidth, bitmapHeight)
        val thirdValidation = CoordinateSystem.validateOffset(state.currentTriangleThird, bitmapWidth, bitmapHeight)
        
        return if (firstValidation.isValid && secondValidation.isValid && thirdValidation.isValid) {
            val displayTriangle = Triangle(state.currentTriangleFirst, state.currentTriangleSecond, state.currentTriangleThird, state.currentShapeProperty)
            val originalTriangle = coordinateConverter.convertTriangleToOriginal(displayTriangle)
            state.addShape(state.currentTriangleFirst, displayTriangle, originalTriangle)
            state.recordLastDrawnShape(state.currentTriangleFirst, "Triangle")
            logger.info("添加三角形: ${state.currentTriangleFirst}, ${state.currentTriangleSecond}, ${state.currentTriangleThird}")
            
            // 重置三角形状态，准备下次绘制
            state.clearCurrentDrawingState()
            
            Pair(state.currentTriangleFirst, displayTriangle)
        } else {
            logger.warn("三角形坐标无效: ${firstValidation.message}, ${secondValidation.message}, ${thirdValidation.message}")
            null
        }
    }
    
    private fun determineTriangleCoordinates() {
        if (state.previousPosition != state.currentPosition && state.currentTriangleFirst == Offset.Unspecified) {
            state.updateTriangleState(first = state.currentPosition)
        } else if (state.currentTriangleFirst != Offset.Unspecified && 
                   state.currentTriangleSecond == Offset.Unspecified && 
                   state.currentTriangleFirst != state.currentPosition) {
            state.updateTriangleState(second = state.currentPosition)
        } else if (state.currentTriangleFirst != Offset.Unspecified && 
                   state.currentTriangleSecond != state.currentPosition) {
            state.updateTriangleState(third = state.currentPosition)
        }
    }
    
    // ========== 矩形处理 ==========
    
    private fun handleRectangleDown(position: Offset) {
        if (state.previousPosition != position && state.currentRectTL == Offset.Unspecified) {
            state.updateRectangleState(tl = position, first = position)
        } else if (state.currentRectTL != Offset.Unspecified) {
            state.updateRectangleState(br = position)
            determineRectangleCoordinates()
        }
    }
    
    private fun handleRectangleMove(position: Offset): Map<Offset, Shape> {
        state.updateRectangleState(br = position)
        determineRectangleCoordinates()
        return if (state.currentRectFirst != Offset.Unspecified) {
            val rect = Rectangle(state.currentRectTL, state.currentRectBL, state.currentRectBR, state.currentRectTR, state.currentRectFirst, state.currentShapeProperty)
            mapOf(state.currentRectFirst to rect)
        } else emptyMap()
    }
    
    private fun handleRectangleUp(bitmapWidth: Int, bitmapHeight: Int): Pair<Offset, Shape>? {
        val tlValidation = CoordinateSystem.validateOffset(state.currentRectTL, bitmapWidth, bitmapHeight)
        val brValidation = CoordinateSystem.validateOffset(state.currentRectBR, bitmapWidth, bitmapHeight)
        
        return if (tlValidation.isValid && brValidation.isValid) {
            val displayRect = Rectangle(state.currentRectTL, state.currentRectBL, state.currentRectBR, state.currentRectTR, state.currentRectFirst, state.currentShapeProperty)
            val originalRect = coordinateConverter.convertRectangleToOriginal(displayRect)
            state.addShape(state.currentRectFirst, displayRect, originalRect)
            state.recordLastDrawnShape(state.currentRectFirst, "Rectangle")
            logger.info("添加矩形: ${state.currentRectTL} -> ${state.currentRectBR}")
            
            // 重置矩形状态，准备下次绘制
            state.clearCurrentDrawingState()
            
            Pair(state.currentRectFirst, displayRect)
        } else {
            logger.warn("矩形坐标无效: ${tlValidation.message}, ${brValidation.message}")
            null
        }
    }
    
    private fun determineRectangleCoordinates() {
        if (state.currentRectBR.x > state.currentRectFirst.x && state.currentRectBR.y > state.currentRectFirst.y) {
            if (state.currentRectTL != state.currentRectFirst) {
                state.updateRectangleState(tl = state.currentRectFirst)
            }
            state.updateRectangleState(
                tr = Offset(state.currentRectBR.x, state.currentRectTL.y),
                bl = Offset(state.currentRectTL.x, state.currentRectBR.y)
            )
        } else if (state.currentRectBR.x > state.currentRectFirst.x && state.currentRectBR.y < state.currentRectFirst.y) {
            if (state.currentRectTL != state.currentRectFirst) {
                state.updateRectangleState(tl = state.currentRectFirst)
            }
            state.updateRectangleState(
                bl = state.currentRectTL,
                tr = state.currentRectBR,
                tl = Offset(state.currentRectBL.x, state.currentRectTR.y),
                br = Offset(state.currentRectTR.x, state.currentRectBL.y)
            )
        } else if (state.currentRectBR.x < state.currentRectFirst.x && state.currentRectBR.y > state.currentRectFirst.y) {
            if (state.currentRectTL != state.currentRectFirst) {
                state.updateRectangleState(tl = state.currentRectFirst)
            }
            state.updateRectangleState(
                tr = state.currentRectTL,
                bl = state.currentRectBR,
                tl = Offset(state.currentRectBL.x, state.currentRectTR.y),
                br = Offset(state.currentRectTR.x, state.currentRectBL.y)
            )
        } else if (state.currentRectBR.x < state.currentRectFirst.x && state.currentRectBR.y < state.currentRectFirst.y) {
            if (state.currentRectTL != state.currentRectFirst) {
                state.updateRectangleState(tl = state.currentRectFirst)
            }
            val temp = state.currentRectTL
            state.updateRectangleState(
                tl = state.currentRectBR,
                br = temp,
                tr = Offset(state.currentRectBR.x, state.currentRectTL.y),
                bl = Offset(state.currentRectTL.x, state.currentRectBR.y)
            )
        }
    }
    
    // ========== 多边形处理 ==========
    
    private fun handlePolygonDown(position: Offset) {
        if (state.previousPosition != position && state.currentPolygonFirst == Offset.Unspecified) {
            state.updatePolygonState(first = position, addPoint = position)
        } else if (state.currentPolygonFirst != Offset.Unspecified) {
            state.updatePolygonState(addPoint = position)
        }
    }
    
    private fun handlePolygonMove(position: Offset): Map<Offset, Shape> {
        state.updatePolygonState(addPoint = position)
        return if (state.currentPolygonFirst != Offset.Unspecified) {
            val polygon = Polygon(state.currentPolygonPoints.toList(), state.currentShapeProperty)
            mapOf(state.currentPolygonFirst to polygon)
        } else emptyMap()
    }
    
    private fun handlePolygonUp(bitmapWidth: Int, bitmapHeight: Int): Pair<Offset, Shape>? {
        return if (state.currentPolygonPoints.size >= 3) {
            val boundaryValidation = CoordinateSystem.validateShapeBoundary(state.currentPolygonPoints.toList(), bitmapWidth, bitmapHeight)
            
            if (boundaryValidation.isValid) {
                val displayPolygon = Polygon(state.currentPolygonPoints.toList(), state.currentShapeProperty)
                val originalPolygon = coordinateConverter.convertPolygonToOriginal(displayPolygon)
                state.addShape(state.currentPolygonFirst, displayPolygon, originalPolygon)
                state.recordLastDrawnShape(state.currentPolygonFirst, "Polygon")
                logger.info("添加多边形: ${state.currentPolygonPoints.size}个顶点")
                
                // 重置多边形状态，准备下次绘制
                state.clearCurrentDrawingState()
                
                Pair(state.currentPolygonFirst, displayPolygon)
            } else {
                logger.warn("多边形边界无效: ${boundaryValidation.message}")
                null
            }
        } else {
            logger.warn("多边形顶点数量不足: ${state.currentPolygonPoints.size} < 3")
            null
        }
    }
}
