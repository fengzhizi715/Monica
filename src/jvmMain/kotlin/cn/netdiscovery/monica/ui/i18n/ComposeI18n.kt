package cn.netdiscovery.monica.ui.i18n

import androidx.compose.runtime.*
import cn.netdiscovery.monica.i18n.LocalizationManager
import cn.netdiscovery.monica.i18n.Language

/**
 * Compose专用的国际化状态管理
 * 用于在UI中响应语言变化
 */
@Composable
fun rememberI18nState(): I18nState {
    // 创建响应式的语言状态
    var currentLanguage by remember { mutableStateOf(LocalizationManager.currentLanguage) }
    
    // 监听语言变化
    LaunchedEffect(Unit) {
        LocalizationManager.addLanguageChangeListener {
            // 当语言变化时，更新Compose状态
            currentLanguage = LocalizationManager.currentLanguage
        }
    }
    
    return remember(currentLanguage) {
        I18nState(currentLanguage)
    }
}

/**
 * 国际化状态类
 */
class I18nState(private val language: Language) {
    
    /**
     * 获取当前语言的字符串资源
     */
    fun getString(key: String): String {
        return LocalizationManager.getString(key)
    }
    
    /**
     * 获取带参数的字符串资源
     */
    fun getString(key: String, vararg args: Any): String {
        return LocalizationManager.getString(key, *args)
    }
    
    /**
     * 获取当前语言
     */
    fun getCurrentLanguage(): Language = language
    
    /**
     * 获取语言显示名称
     */
    fun getLanguageDisplayName(): String {
        return "${language.flag} ${language.displayName}"
    }
    
    /**
     * 切换语言
     */
    fun toggleLanguage() {
        val newLang = if (language == Language.CHINESE) Language.ENGLISH else Language.CHINESE
        LocalizationManager.setLanguage(newLang)
    }
    
    /**
     * 设置特定语言
     */
    fun setLanguage(newLanguage: Language) {
        LocalizationManager.setLanguage(newLanguage)
    }
    
    /**
     * 重置为系统语言
     */
    fun resetToSystemLanguage() {
        val systemLang = Language.getSystemLanguage()
        LocalizationManager.setLanguage(systemLang)
    }
    
    /**
     * 获取切换按钮文本
     */
    fun getToggleButtonText(): String {
        return if (language == Language.CHINESE) "切换到英文" else "Switch to Chinese"
    }
}

/**
 * 便捷的字符串获取函数
 */
@Composable
fun getString(key: String): String {
    val i18nState = rememberI18nState()
    return i18nState.getString(key)
}

/**
 * 便捷的带参数字符串获取函数
 */
@Composable
fun getString(key: String, vararg args: Any): String {
    val i18nState = rememberI18nState()
    return i18nState.getString(key, *args)
}
