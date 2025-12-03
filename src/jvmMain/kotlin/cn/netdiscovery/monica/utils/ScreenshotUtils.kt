package cn.netdiscovery.monica.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * 截图工具类
 * 
 * @author: Tony Shen
 * @date: 2025/11/09
 * @version: V1.0
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

/**
 * 截取整个屏幕
 */
fun captureFullScreen(): BufferedImage? {
    return try {
        val robot = Robot()
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        robot.createScreenCapture(Rectangle(0, 0, screenSize.width, screenSize.height))
    } catch (e: Exception) {
        logger.error("截取全屏失败", e)
        null
    }
}

/**
 * 截取指定区域
 * @param x 起始 X 坐标
 * @param y 起始 Y 坐标
 * @param width 宽度
 * @param height 高度
 */
fun captureRegion(x: Int, y: Int, width: Int, height: Int): BufferedImage? {
    return try {
        val robot = Robot()
        robot.createScreenCapture(Rectangle(x, y, width, height))
    } catch (e: Exception) {
        logger.error("截取区域失败: x=$x, y=$y, width=$width, height=$height", e)
        null
    }
}

/**
 * 将截图加载到 ApplicationState
 */
fun loadScreenshotToState(state: ApplicationState, screenshot: BufferedImage) {
    state.scope.launchWithLoading {
        try {
            logger.info("加载截图到应用状态")
            state.rawImage = screenshot
            state.currentImage = state.rawImage
            state.rawImageFile = null
        } catch (e: Exception) {
            logger.error("加载截图失败", e)
        }
    }
}

