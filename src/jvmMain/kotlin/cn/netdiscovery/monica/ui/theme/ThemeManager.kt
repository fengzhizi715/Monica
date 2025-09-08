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
    
    private var _currentTheme by mutableStateOf(ColorTheme.LIGHT)
    
    /**
     * 当前主题
     */
    val currentTheme: ColorTheme
        get() = _currentTheme
    
    /**
     * 设置主题
     */
    fun setTheme(theme: ColorTheme) {
        logger.info("切换主题: ${theme.displayName}")
        _currentTheme = theme
    }
    
    /**
     * 根据主题获取 Material Colors
     */
    fun getMaterialColors(theme: ColorTheme = currentTheme): Colors {
        return when (theme) {
            ColorTheme.DARK -> darkColors(
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
            else -> lightColors(
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
        setTheme(ColorTheme.LIGHT)
    }
}

/**
 * 主题状态的可组合函数
 */
@Composable
fun rememberThemeState(): ColorTheme {
    return ThemeManager.currentTheme
}

/**
 * 主题切换的可组合函数
 */
@Composable
fun setTheme(theme: ColorTheme) {
    ThemeManager.setTheme(theme)
}

/**
 * 自定义 MaterialTheme 包装器
 */
@Composable
fun CustomMaterialTheme(
    theme: ColorTheme = ThemeManager.currentTheme,
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
