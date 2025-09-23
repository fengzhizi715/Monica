package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape.*
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeEnum
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.widget.image.gesture.MotionEvent

/**
 * 形状绘制状态管理
 * 统一管理所有形状的状态，降低耦合度
 * 
 * @author Tony Shen
 * @date 2025/9/8
 * @version V1.0
 */
@Stable
class ShapeDrawingState {
    
    // 当前选择的形状类型
    var currentShape by mutableStateOf(ShapeEnum.NotAShape)
        private set
    
    // 当前形状属性
    var currentShapeProperty by mutableStateOf(ShapeProperties())
        private set
    
    // 当前鼠标位置
    var currentPosition by mutableStateOf(Offset.Unspecified)
        private set
    
    var previousPosition by mutableStateOf(Offset.Unspecified)
        private set
    
    // 当前运动事件
    var motionEvent by mutableStateOf(MotionEvent.Idle)
        private set
    
    // 线段相关状态
    var currentLineStart by mutableStateOf(Offset.Unspecified)
        private set
    var currentLineEnd by mutableStateOf(Offset.Unspecified)
        private set
    
    // 圆形相关状态
    var currentCircleCenter by mutableStateOf(Offset.Unspecified)
        private set
    var currentCircleRadius by mutableStateOf(0.0f)
        private set
    
    // 三角形相关状态
    var currentTriangleFirst by mutableStateOf(Offset.Unspecified)
        private set
    var currentTriangleSecond by mutableStateOf(Offset.Unspecified)
        private set
    var currentTriangleThird by mutableStateOf(Offset.Unspecified)
        private set
    
    // 矩形相关状态
    var currentRectFirst by mutableStateOf(Offset.Unspecified)
        private set
    var currentRectTL by mutableStateOf(Offset.Unspecified)
        private set
    var currentRectBR by mutableStateOf(Offset.Unspecified)
        private set
    var currentRectTR by mutableStateOf(Offset.Unspecified)
        private set
    var currentRectBL by mutableStateOf(Offset.Unspecified)
        private set
    
    // 多边形相关状态
    var currentPolygonFirst by mutableStateOf(Offset.Unspecified)
        private set
    var currentPolygonPoints = mutableStateListOf<Offset>()
    
    // 文字相关状态
    var currentText by mutableStateOf("")
        private set
    
    // 已完成的形状集合
    val displayLines = mutableStateMapOf<Offset, Line>()
    val originalLines = mutableStateMapOf<Offset, Line>()
    val displayCircles = mutableStateMapOf<Offset, Circle>()
    val originalCircles = mutableStateMapOf<Offset, Circle>()
    val displayTriangles = mutableStateMapOf<Offset, Triangle>()
    val originalTriangles = mutableStateMapOf<Offset, Triangle>()
    val displayRectangles = mutableStateMapOf<Offset, Rectangle>()
    val originalRectangles = mutableStateMapOf<Offset, Rectangle>()
    val displayPolygons = mutableStateMapOf<Offset, Polygon>()
    val originalPolygons = mutableStateMapOf<Offset, Polygon>()
    val displayTexts = mutableStateMapOf<Offset, Text>()
    val originalTexts = mutableStateMapOf<Offset, Text>()
    
    // 最后一个绘制的形状跟踪
    var lastDrawnShapeKey by mutableStateOf<Offset?>(null)
        private set
    var lastDrawnShapeType by mutableStateOf<String?>(null)
        private set
    
    /**
     * 设置当前形状类型
     */
    fun selectShape(shape: ShapeEnum) {
        currentShape = shape
        clearCurrentDrawingState()
    }
    
    /**
     * 更新形状属性
     */
    fun updateShapeProperty(property: ShapeProperties) {
        currentShapeProperty = property
    }
    
    /**
     * 更新颜色
     */
    fun updateColor(color: Color) {
        currentShapeProperty = currentShapeProperty.copy(color = color)
    }
    
    /**
     * 更新位置信息
     */
    fun updatePosition(position: Offset) {
        previousPosition = currentPosition
        currentPosition = position
    }
    
    /**
     * 更新运动事件
     */
    fun updateMotionEvent(event: MotionEvent) {
        motionEvent = event
    }
    
    /**
     * 更新线段状态
     */
    fun updateLineState(start: Offset? = null, end: Offset? = null) {
        start?.let { currentLineStart = it }
        end?.let { currentLineEnd = it }
    }
    
    /**
     * 更新圆形状态
     */
    fun updateCircleState(center: Offset? = null, radius: Float? = null) {
        center?.let { currentCircleCenter = it }
        radius?.let { currentCircleRadius = it }
    }
    
    /**
     * 更新三角形状态
     */
    fun updateTriangleState(first: Offset? = null, second: Offset? = null, third: Offset? = null) {
        first?.let { currentTriangleFirst = it }
        second?.let { currentTriangleSecond = it }
        third?.let { currentTriangleThird = it }
    }
    
    /**
     * 更新矩形状态
     */
    fun updateRectangleState(
        first: Offset? = null,
        tl: Offset? = null,
        br: Offset? = null,
        tr: Offset? = null,
        bl: Offset? = null
    ) {
        first?.let { currentRectFirst = it }
        tl?.let { currentRectTL = it }
        br?.let { currentRectBR = it }
        tr?.let { currentRectTR = it }
        bl?.let { currentRectBL = it }
    }
    
    /**
     * 更新多边形状态
     */
    fun updatePolygonState(first: Offset? = null, addPoint: Offset? = null) {
        first?.let { currentPolygonFirst = it }
        addPoint?.let { currentPolygonPoints.add(it) }
    }
    
    /**
     * 更新文字状态
     */
    fun updateTextState(text: String) {
        currentText = text
    }
    
    /**
     * 记录最后绘制的形状
     */
    fun recordLastDrawnShape(key: Offset, type: String) {
        lastDrawnShapeKey = key
        lastDrawnShapeType = type
    }
    
    /**
     * 清除当前绘制状态（保留已完成的形状）
     */
    fun clearCurrentDrawingState() {
        // 保存当前颜色设置
        val currentColor = currentShapeProperty.color
        
        // 清除临时绘制状态
        currentLineStart = Offset.Unspecified
        currentLineEnd = Offset.Unspecified
        
        currentCircleCenter = Offset.Unspecified
        currentCircleRadius = 0.0f
        
        currentTriangleFirst = Offset.Unspecified
        currentTriangleSecond = Offset.Unspecified
        currentTriangleThird = Offset.Unspecified
        
        currentRectFirst = Offset.Unspecified
        currentRectTL = Offset.Unspecified
        currentRectBR = Offset.Unspecified
        currentRectTR = Offset.Unspecified
        currentRectBL = Offset.Unspecified
        
        currentPolygonFirst = Offset.Unspecified
        currentPolygonPoints.clear()
        
        currentText = ""
        
        // 重置最后一个形状的跟踪
        lastDrawnShapeKey = null
        lastDrawnShapeType = null
        
        // 保持颜色设置
        currentShapeProperty = currentShapeProperty.copy(color = currentColor)
    }
    
    /**
     * 清除所有已完成的形状
     */
    fun clearAllShapes() {
        // 清理已完成的形状
        displayLines.clear()
        originalLines.clear()
        displayCircles.clear()
        originalCircles.clear()
        displayTriangles.clear()
        originalTriangles.clear()
        displayRectangles.clear()
        originalRectangles.clear()
        displayPolygons.clear()
        originalPolygons.clear()
        displayTexts.clear()
        originalTexts.clear()
        
        // 清理当前绘制状态
        clearCurrentDrawingState()
        
        // 重置最后一个形状的跟踪
        lastDrawnShapeKey = null
        lastDrawnShapeType = null
    }
    
    /**
     * 添加形状到显示和原始集合
     */
    fun addShape(key: Offset, displayShape: Shape, originalShape: Shape) {
        when (displayShape) {
            is Line -> {
                displayLines[key] = displayShape
                originalLines[key] = originalShape as Line
            }
            is Circle -> {
                displayCircles[key] = displayShape
                originalCircles[key] = originalShape as Circle
            }
            is Triangle -> {
                displayTriangles[key] = displayShape
                originalTriangles[key] = originalShape as Triangle
            }
            is Rectangle -> {
                displayRectangles[key] = displayShape
                originalRectangles[key] = originalShape as Rectangle
            }
            is Polygon -> {
                displayPolygons[key] = displayShape
                originalPolygons[key] = originalShape as Polygon
            }
            is Text -> {
                displayTexts[key] = displayShape
                originalTexts[key] = originalShape as Text
            }
        }
    }
}
