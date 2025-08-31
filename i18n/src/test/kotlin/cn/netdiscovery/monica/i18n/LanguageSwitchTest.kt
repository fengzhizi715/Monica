package cn.netdiscovery.monica.i18n

import org.junit.Test
import org.junit.Assert.*

/**
 * 语言切换功能测试
 */
class LanguageSwitchTest {

    @Test
    fun `test language switching`() {
        // 记录原始语言
        val originalLanguage = LocalizationManager.currentLanguage
        
        // 测试切换到英文
        LocalizationManager.setLanguage(Language.ENGLISH)
        assertEquals("语言应该切换到英文", Language.ENGLISH, LocalizationManager.currentLanguage)
        
        // 测试切换到中文
        LocalizationManager.setLanguage(Language.CHINESE)
        assertEquals("语言应该切换到中文", Language.CHINESE, LocalizationManager.currentLanguage)
        
        // 恢复原始语言
        LocalizationManager.setLanguage(originalLanguage)
        assertEquals("语言应该恢复到原始状态", originalLanguage, LocalizationManager.currentLanguage)
    }

    @Test
    fun `test language state management`() {
        val languageState = createLanguageState()
        
        // 测试当前语言
        val currentLang = languageState.currentLanguage
        assertTrue("当前语言应该是支持的语言之一", currentLang in Language.values())
        
        // 测试切换语言
        val newLang = if (currentLang == Language.CHINESE) Language.ENGLISH else Language.CHINESE
        languageState.setLanguage(newLang)
        assertEquals("语言状态应该更新", newLang, languageState.currentLanguage)
        
        // 测试切换按钮文本
        val toggleText = languageState.getToggleButtonText()
        if (newLang == Language.CHINESE) {
            assertTrue("切换按钮文本应该包含'切换到英文'", toggleText.contains("切换到英文"))
        } else {
            assertTrue("切换按钮文本应该包含'Switch to Chinese'", toggleText.contains("Switch to Chinese"))
        }
        
        // 测试语言显示名称
        val displayName = languageState.getLanguageDisplayName()
        assertTrue("显示名称应该包含国旗和语言名", displayName.contains(newLang.flag))
        assertTrue("显示名称应该包含语言名", displayName.contains(newLang.displayName))
    }

    @Test
    fun `test system language detection`() {
        val systemLang = Language.getSystemLanguage()
        assertTrue("系统语言应该是支持的语言之一", systemLang in Language.values())
        
        // 测试重置为系统语言
        val languageState = createLanguageState()
        languageState.resetToSystemLanguage()
        assertEquals("语言应该重置为系统语言", systemLang, languageState.currentLanguage)
    }

    @Test
    fun `test language persistence`() {
        val originalLanguage = LocalizationManager.currentLanguage
        
        // 切换到英文
        LocalizationManager.setLanguage(Language.ENGLISH)
        assertEquals("语言应该切换到英文", Language.ENGLISH, LocalizationManager.currentLanguage)
        
        // 模拟重新加载（清除缓存）
        LocalizationManager.javaClass.getDeclaredMethod("clearCache").apply {
            isAccessible = true
            invoke(LocalizationManager)
        }
        
        // 重新获取语言，应该保持英文
        val reloadedLanguage = LocalizationManager.currentLanguage
        assertEquals("语言设置应该持久化", Language.ENGLISH, reloadedLanguage)
        
        // 恢复原始语言
        LocalizationManager.setLanguage(originalLanguage)
    }
}


