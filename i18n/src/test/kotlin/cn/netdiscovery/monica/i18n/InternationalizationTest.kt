package cn.netdiscovery.monica.i18n

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * 国际化功能测试
 */
class InternationalizationTest {

    @Test
    fun `test language enum`() {
        assertEquals("zh", Language.CHINESE.code)
        assertEquals("en", Language.ENGLISH.code)
        assertEquals("中文", Language.CHINESE.displayName)
        assertEquals("English", Language.ENGLISH.displayName)
        assertEquals("🇨🇳", Language.CHINESE.flag)
        assertEquals("🇺🇸", Language.ENGLISH.flag)
    }

    @Test
    fun `test language fromCode`() {
        assertEquals(Language.CHINESE, Language.fromCode("zh"))
        assertEquals(Language.ENGLISH, Language.fromCode("en"))
        assertEquals(Language.CHINESE, Language.fromCode("invalid")) // 默认返回中文
    }

    @Test
    fun `test system language detection`() {
        val systemLang = Language.getSystemLanguage()
        assertTrue(systemLang in Language.values())
    }

    @Test
    fun `test localization manager`() {
        // 测试默认语言
        val defaultLang = LocalizationManager.currentLanguage
        assertTrue(defaultLang in Language.values())
        
        // 测试语言切换
        val originalLang = LocalizationManager.currentLanguage
        val newLang = if (originalLang == Language.CHINESE) Language.ENGLISH else Language.CHINESE
        
        LocalizationManager.setLanguage(newLang)
        assertEquals(newLang, LocalizationManager.currentLanguage)
        
        // 恢复原语言
        LocalizationManager.setLanguage(originalLang)
    }

    @Test
    fun `test string resource loading`() {
        val chineseResource = LocalizationManager.getXmlResource(Language.CHINESE)
        val englishResource = LocalizationManager.getXmlResource(Language.ENGLISH)
        
        // 测试资源是否加载成功
        assertNotNull(chineseResource)
        assertNotNull(englishResource)
        
        // 测试获取字符串
        val chineseAppName = chineseResource.get("app_name")
        val englishAppName = englishResource.get("app_name")
        
        assertEquals("Monica", chineseAppName)
        assertEquals("Monica", englishAppName)
    }

    @Test
    fun `test string resource with parameters`() {
        val chineseResource = LocalizationManager.getXmlResource(Language.CHINESE)
        val englishResource = LocalizationManager.getXmlResource(Language.ENGLISH)
        
        // 测试带参数的字符串
        val chineseParam = chineseResource.get("color_parameters_updated", "亮度+10")
        val englishParam = englishResource.get("color_parameters_updated", "brightness+10")
        
        assertTrue(chineseParam.contains("亮度+10"))
        assertTrue(englishParam.contains("brightness+10"))
    }

    @Test
    fun `test supported languages`() {
        val supportedLanguages = LocalizationManager.getSupportedLanguages()
        assertEquals(2, supportedLanguages.size)
        assertTrue(supportedLanguages.contains(Language.CHINESE))
        assertTrue(supportedLanguages.contains(Language.ENGLISH))
    }
}
