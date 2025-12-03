package cn.netdiscovery.monica.ui.screenshot

import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.captureRegion
import cn.netdiscovery.monica.utils.loadScreenshotToState
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * 基于 Swing 的区域选择截图工具
 * 在 macOS 上更可靠地实现全屏透明窗口
 * 
 * @author: Tony Shen
 * @date: 2025/12/03
 * @version: V1.0
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

/**
 * 显示 Swing 区域选择截图窗口Ø
 */
fun showSwingScreenshotAreaSelector(
    state: ApplicationState,
    onDismiss: () -> Unit
) {
    SwingUtilities.invokeLater {
        ScreenshotAreaFrame(state, onDismiss)
    }
}

private class ScreenshotAreaFrame(
    private val state: ApplicationState,
    private val onDismiss: () -> Unit
) : JFrame() {
    
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var isSelecting = false
    
    private val selectionPanel = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            
            if (isSelecting && startPoint != null && endPoint != null) {
                val g2d = g as Graphics2D
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                
                val start = startPoint!!
                val end = endPoint!!
                
                // 规范化坐标
                val left = minOf(start.x, end.x)
                val top = minOf(start.y, end.y)
                val right = maxOf(start.x, end.x)
                val bottom = maxOf(start.y, end.y)
                val width = right - left
                val height = bottom - top
                
                // 绘制半透明遮罩（选择区域外）
                g2d.color = Color(0, 0, 0, 128) // 半透明黑色
                
                // 上方
                if (top > 0) {
                    g2d.fillRect(0, 0, size.width, top)
                }
                
                // 下方
                if (bottom < size.height) {
                    g2d.fillRect(0, bottom, size.width, size.height - bottom)
                }
                
                // 左侧
                if (left > 0) {
                    g2d.fillRect(0, top, left, height)
                }
                
                // 右侧
                if (right < size.width) {
                    g2d.fillRect(right, top, size.width - right, height)
                }
                
                // 绘制选择框边框
                g2d.color = Color.WHITE
                val oldStroke = g2d.stroke
                g2d.stroke = BasicStroke(2f)
                g2d.drawRect(left, top, width, height)
                g2d.stroke = oldStroke
                
                // 绘制尺寸信息
                val infoText = "${width} × ${height}"
                g2d.color = Color.WHITE
                g2d.font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
                val fontMetrics = g2d.fontMetrics
                val textWidth = fontMetrics.stringWidth(infoText)
                val textHeight = fontMetrics.height
                
                val textX = left + 5
                val textY = if (top - textHeight - 5 > 0) {
                    top - 5
                } else {
                    bottom + textHeight + 5
                }
                
                // 绘制文本背景（提高可读性）
                g2d.color = Color(0, 0, 0, 180)
                g2d.fillRect(textX - 2, textY - textHeight - 2, textWidth + 4, textHeight + 4)
                
                // 绘制文本
                g2d.color = Color.WHITE
                g2d.drawString(infoText, textX, textY)
            } else {
                // 没有选择时，绘制全屏半透明遮罩
                g.color = Color(0, 0, 0, 76) // 30% 透明度
                g.fillRect(0, 0, size.width, size.height)
            }
        }
    }
    
    init {
        // 隐藏主窗口
        logger.info("区域选择器打开，隐藏主窗口")
        state.window.isVisible = false
        
        // 设置窗口属性
        isUndecorated = true
        background = Color(0, 0, 0, 0) // 完全透明
        isAlwaysOnTop = true
        isResizable = false
        
        // 获取屏幕尺寸
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val graphicsConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
        
        // 设置窗口大小和位置
        bounds = graphicsConfig.bounds
        
        // 设置内容面板
        contentPane = selectionPanel
        (contentPane as? JPanel)?.apply {
            background = Color(0, 0, 0, 0)
            isOpaque = false
        }
        
        // 添加鼠标监听器
        val mouseAdapter = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                startPoint = e.point
                endPoint = e.point
                isSelecting = true
                selectionPanel.repaint()
            }
            
            override fun mouseDragged(e: MouseEvent) {
                endPoint = e.point
                selectionPanel.repaint()
            }
            
            override fun mouseReleased(e: MouseEvent) {
                isSelecting = false
                val start = startPoint
                val end = endPoint
                
                if (start != null && end != null) {
                    // 规范化坐标
                    val x = minOf(start.x, end.x).coerceAtLeast(0)
                    val y = minOf(start.y, end.y).coerceAtLeast(0)
                    val width = (maxOf(start.x, end.x) - minOf(start.x, end.x)).coerceAtLeast(1)
                    val height = (maxOf(start.y, end.y) - minOf(start.y, end.y)).coerceAtLeast(1)
                    
                    logger.info("选择区域: x=$x, y=$y, width=$width, height=$height")
                    
                    // 在后台线程执行截图
                    Thread {
                        try {
                            val screenshot = captureRegion(x, y, width, height)
                            SwingUtilities.invokeLater {
                                dispose() // 关闭窗口
                                state.window.isVisible = true // 恢复主窗口
                                
                                if (screenshot != null) {
                                    logger.info("截图成功，尺寸: ${screenshot.width}x${screenshot.height}")
                                    loadScreenshotToState(state, screenshot)
                                } else {
                                    logger.error("截图失败")
                                }
                                onDismiss()
                            }
                        } catch (e: Exception) {
                            logger.error("截图异常", e)
                            SwingUtilities.invokeLater {
                                dispose()
                                state.window.isVisible = true
                                onDismiss()
                            }
                        }
                    }.start()
                } else {
                    // 没有选择区域，直接关闭
                    dispose()
                    state.window.isVisible = true
                    onDismiss()
                }
                
                startPoint = null
                endPoint = null
            }
        }
        
        selectionPanel.addMouseListener(mouseAdapter)
        selectionPanel.addMouseMotionListener(mouseAdapter)
        
        // ESC 键关闭
        val keyAdapter = object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    dispose()
                    state.window.isVisible = true
                    onDismiss()
                }
            }
        }
        addKeyListener(keyAdapter)
        selectionPanel.isFocusable = true
        selectionPanel.requestFocus()
        
        // 设置窗口关闭监听
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                state.window.isVisible = true
                onDismiss()
            }
        })
        
        // 显示窗口
        isVisible = true
        toFront()
        selectionPanel.requestFocus()
        
        logger.info("Swing 区域选择窗口已显示，大小: ${bounds.width}x${bounds.height}")
    }
}