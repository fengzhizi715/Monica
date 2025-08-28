package cn.netdiscovery.monica.i18n

import org.junit.Test
import org.junit.Assert.*

/**
 * 字符串资源测试
 */
class StringResourceTest {

    @Test
    fun `test chinese string resources`() {
        val chineseResource = LocalizationManager.getXmlResource(Language.CHINESE)
        
        // 测试基本字符串
        assertEquals("Monica", chineseResource.get("app_name"))
        assertEquals("通用设置", chineseResource.get("general_settings"))
        assertEquals("基础功能", chineseResource.get("basic_functions"))
        assertEquals("AI 实验室", chineseResource.get("ai_laboratory"))
        
        // 测试带参数的字符串
        val paramString = chineseResource.get("color_parameters_updated", "亮度+10")
        assertTrue(paramString.contains("亮度+10"))
        
        // 测试资源信息
        val info = chineseResource.getResourceInfo()
        assertTrue(info.contains("CHINESE"))
        assertTrue(info.contains("字符串数量"))
    }

    @Test
    fun `test english string resources`() {
        val englishResource = LocalizationManager.getXmlResource(Language.ENGLISH)
        
        // 测试基本字符串
        assertEquals("Monica", englishResource.get("app_name"))
        assertEquals("General Settings", englishResource.get("general_settings"))
        assertEquals("Basic Functions", englishResource.get("basic_functions"))
        assertEquals("AI Laboratory", englishResource.get("ai_laboratory"))
        
        // 测试带参数的字符串
        val paramString = englishResource.get("color_parameters_updated", "brightness+10")
        assertTrue(paramString.contains("brightness+10"))
        
        // 测试资源信息
        val info = englishResource.getResourceInfo()
        assertTrue(info.contains("ENGLISH"))
        assertTrue(info.contains("字符串数量") || info.contains("strings"))
    }

    @Test
    fun `test string resource keys`() {
        val chineseResource = LocalizationManager.getXmlResource(Language.CHINESE)
        val englishResource = LocalizationManager.getXmlResource(Language.ENGLISH)
        
        // 测试键的数量
        val chineseKeys = chineseResource.getAllKeys()
        val englishKeys = englishResource.getAllKeys()
        
        assertTrue("中文资源应该包含多个键", chineseKeys.size > 10)
        assertTrue("英文资源应该包含多个键", englishKeys.size > 10)
        
        // 测试键的一致性
        assertEquals("中英文资源应该包含相同数量的键", chineseKeys.size, englishKeys.size)
        
        // 测试一些关键键是否存在
        val expectedKeys = setOf("app_name", "general_settings", "basic_functions", "ai_laboratory")
        expectedKeys.forEach { key ->
            assertTrue("中文资源应该包含键: $key", chineseKeys.contains(key))
            assertTrue("英文资源应该包含键: $key", englishKeys.contains(key))
        }
    }

    @Test
    fun `test localization manager integration`() {
        // 测试默认语言
        val defaultLang = LocalizationManager.currentLanguage
        assertTrue("默认语言应该是支持的语言之一", defaultLang in Language.values())
        
        // 测试获取字符串
        val appName = LocalizationManager.getString("app_name")
        assertEquals("应用名称应该是Monica", "Monica", appName)
        
        // 测试带参数的字符串
        val paramString = LocalizationManager.getString("color_parameters_updated", "测试参数")
        assertTrue("带参数字符串应该包含参数", paramString.contains("测试参数"))
    }
}
