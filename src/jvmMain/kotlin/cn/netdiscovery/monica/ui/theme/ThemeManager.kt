package cn.netdiscovery.monica.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 主题管理器
 * @author: Tony Shen
 * @date: 2025/9/8
 * @version: V1.0
 */
object ThemeManager {
    private val logger: Logger = LoggerFactory.getLogger(ThemeManager::class.java)
    
    // 移除独立的状态管理，改为从外部获取
    private var _currentTheme: ColorTheme? = null
    
    /**
     * 设置当前主题（从ApplicationState调用）
     */
    fun setCurrentTheme(theme: ColorTheme) {
        logger.info("切换主题: ${theme.displayName}")
        _currentTheme = theme
    }
    
    /**
     * 获取当前主题
     */
    fun getCurrentTheme(): ColorTheme {
        return _currentTheme ?: ColorTheme.LIGHT
    }
    
    /**
     * 根据主题获取 Material Colors
     * 根据背景亮度自动选择使用 lightColors 或 darkColors
     */
    fun getMaterialColors(theme: ColorTheme = getCurrentTheme()): Colors {
        // 计算背景色的亮度来判断是否为深色主题
        val isDarkTheme = isDarkBackground(theme.background)
        
        return if (isDarkTheme) {
            darkColors(
                primary = theme.primary,
                primaryVariant = theme.primaryVariant,
                secondary = theme.secondary,
                secondaryVariant = theme.secondaryVariant,
                background = theme.background,
                surface = theme.surface,
                error = theme.error,
                onPrimary = theme.onPrimary,
                onSecondary = theme.onSecondary,
                onBackground = theme.onBackground,
                onSurface = theme.onSurface,
                onError = theme.onError
            )
        } else {
            lightColors(
                primary = theme.primary,
                primaryVariant = theme.primaryVariant,
                secondary = theme.secondary,
                secondaryVariant = theme.secondaryVariant,
                background = theme.background,
                surface = theme.surface,
                error = theme.error,
                onPrimary = theme.onPrimary,
                onSecondary = theme.onSecondary,
                onBackground = theme.onBackground,
                onSurface = theme.onSurface,
                onError = theme.onError
            )
        }
    }
    
    /**
     * 判断背景色是否为深色
     * 使用相对亮度公式：0.299*R + 0.587*G + 0.114*B
     */
    private fun isDarkBackground(backgroundColor: Color): Boolean {
        val luminance = 0.299f * backgroundColor.red + 0.587f * backgroundColor.green + 0.114f * backgroundColor.blue
        return luminance < 0.5f // 亮度小于0.5认为是深色背景
    }
    
    /**
     * 获取所有可用主题
     */
    fun getAllThemes(): List<ColorTheme> {
        return ColorTheme.values().toList()
    }
    
    /**
     * 根据 ID 获取主题
     */
    fun getThemeById(id: String): ColorTheme? {
        return try {
            ColorTheme.valueOf(id)
        } catch (e: IllegalArgumentException) {
            logger.warn("未找到主题: $id")
            null
        }
    }
    
    /**
     * 重置为默认主题
     */
    fun resetToDefault() {
        logger.info("重置为默认主题")
        setCurrentTheme(ColorTheme.LIGHT)
    }
}

/**
 * 主题状态的可组合函数
 */
@Composable
fun rememberThemeState(): ColorTheme {
    return ThemeManager.getCurrentTheme()
}

/**
 * 主题切换的可组合函数
 */
@Composable
fun setTheme(theme: ColorTheme) {
    ThemeManager.setCurrentTheme(theme)
}

/**
 * 自定义 MaterialTheme 包装器
 */
@Composable
fun CustomMaterialTheme(
    theme: ColorTheme = ThemeManager.getCurrentTheme(),
    content: @Composable () -> Unit
) {
    val colors = ThemeManager.getMaterialColors(theme)
    
    MaterialTheme(
        colors = colors,
        content = content
    )
}

/**
 * 主题状态提供者
 */
val LocalThemeState = staticCompositionLocalOf { ColorTheme.LIGHT }