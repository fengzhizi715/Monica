package cn.netdiscovery.monica.ui.theme

import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.i18n.LocalizationManager
import cn.netdiscovery.monica.i18n.Language

/**
 * 颜色主题枚举
 * @author: Tony Shen
 * @date: 2025/9/8
 * @version: V1.0
 */
enum class ColorTheme(
    val displayName: String,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color
) {
    LIGHT(
        displayName = "浅色主题",
        primary = Color(0xFF2196F3),
        primaryVariant = Color(0xFF1976D2),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = Color(0xFFF5F5F5),
        surface = Color(0xFFFFFFFF),
        error = Color(0xFFB00020),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        onError = Color(0xFFFFFFFF)
    ),
    
    DARK(
        displayName = "深色主题",
        primary = Color(0xFF90CAF9),
        primaryVariant = Color(0xFF42A5F5),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = Color(0xFF1A1A1A), // 稍微亮一点的深色
        surface = Color(0xFF2D2D2D), // 更亮的表面色
        error = Color(0xFFCF6679),
        onPrimary = Color(0xFF000000),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFFE0E0E0), // 更亮的文字色
        onSurface = Color(0xFFE0E0E0), // 更亮的文字色
        onError = Color(0xFF000000)
    ),
    
    BLUE(
        displayName = "蓝色主题",
        primary = Color(0xFF1976D2),
        primaryVariant = Color(0xFF0D47A1),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = Color(0xFFE3F2FD),
        surface = Color(0xFFFFFFFF),
        error = Color(0xFFB00020),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        onError = Color(0xFFFFFFFF)
    ),
    
    GREEN(
        displayName = "绿色主题",
        primary = Color(0xFF388E3C),
        primaryVariant = Color(0xFF1B5E20),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = Color(0xFFE8F5E8),
        surface = Color(0xFFFFFFFF),
        error = Color(0xFFB00020),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        onError = Color(0xFFFFFFFF)
    ),
    
    PURPLE(
        displayName = "紫色主题",
        primary = Color(0xFF7B1FA2),
        primaryVariant = Color(0xFF4A148C),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = Color(0xFFF3E5F5),
        surface = Color(0xFFFFFFFF),
        error = Color(0xFFB00020),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        onError = Color(0xFFFFFFFF)
    ),
    
    ORANGE(
        displayName = "橙色主题",
        primary = Color(0xFFF57C00),
        primaryVariant = Color(0xFFE65100),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = Color(0xFFFFF3E0),
        surface = Color(0xFFFFFFFF),
        error = Color(0xFFB00020),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        onError = Color(0xFFFFFFFF)
    ),
    
    PINK(
        displayName = "粉色主题",
        primary = Color(0xFFE91E63),
        primaryVariant = Color(0xFFC2185B),
        secondary = Color(0xFFF06292),
        secondaryVariant = Color(0xFFE91E63),
        background = Color(0xFFFCE4EC),
        surface = Color(0xFFF8BBD9),
        error = Color(0xFFB00020),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        onError = Color(0xFFFFFFFF)
    );

    /**
     * 获取主题的显示名称
     */
    fun getThemeDisplayName(): String {
        return when (this) {
            LIGHT -> if (LocalizationManager.currentLanguage == Language.CHINESE) "浅色主题" else "Light Theme"
            DARK -> if (LocalizationManager.currentLanguage == Language.CHINESE) "深色主题" else "Dark Theme"
            BLUE -> if (LocalizationManager.currentLanguage == Language.CHINESE) "蓝色主题" else "Blue Theme"
            GREEN -> if (LocalizationManager.currentLanguage == Language.CHINESE) "绿色主题" else "Green Theme"
            PURPLE -> if (LocalizationManager.currentLanguage == Language.CHINESE) "紫色主题" else "Purple Theme"
            ORANGE -> if (LocalizationManager.currentLanguage == Language.CHINESE) "橙色主题" else "Orange Theme"
            PINK -> if (LocalizationManager.currentLanguage == Language.CHINESE) "粉色主题" else "Pink Theme"
        }
    }

    /**
     * 获取主题的唯一标识符
     */
    fun getThemeId(): String = name
}
