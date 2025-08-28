package cn.netdiscovery.monica.i18n

/**
 * 语言状态管理类
 * 用于管理语言状态和提供语言操作
 */
class LanguageState {
    val currentLanguage: Language
        get() = LocalizationManager.currentLanguage
    
    /**
     * 切换语言
     */
    fun toggleLanguage() {
        val newLang = if (currentLanguage == Language.CHINESE) Language.ENGLISH else Language.CHINESE
        LocalizationManager.setLanguage(newLang)
    }
    
    /**
     * 设置特定语言
     */
    fun setLanguage(language: Language) {
        LocalizationManager.setLanguage(language)
    }
    
    /**
     * 重置为系统语言
     */
    fun resetToSystemLanguage() {
        val systemLang = Language.getSystemLanguage()
        LocalizationManager.setLanguage(systemLang)
    }
    
    /**
     * 获取语言显示名称
     */
    fun getLanguageDisplayName(): String {
        return "${currentLanguage.flag} ${currentLanguage.displayName}"
    }
    
    /**
     * 获取切换按钮文本
     */
    fun getToggleButtonText(): String {
        return if (currentLanguage == Language.CHINESE) "切换到英文" else "Switch to Chinese"
    }
}

/**
 * 创建语言状态实例
 */
fun createLanguageState(): LanguageState = LanguageState()
