package cn.netdiscovery.monica.i18n

import androidx.compose.runtime.*
import java.util.prefs.Preferences

/**
 * 国际化管理器
 *
 * 负责管理应用的语言设置和本地化资源
 */
object LocalizationManager {
    private val prefs = Preferences.userNodeForPackage(LocalizationManager::class.java)
    private const val LANGUAGE_KEY = "selected_language"

    // 当前语言状态
    private val _currentLanguage = mutableStateOf(getSavedLanguage())
    val currentLanguage: State<Language> = _currentLanguage

    /**
     * 获取保存的语言设置
     */
    private fun getSavedLanguage(): Language {
        val savedCode = prefs.get(LANGUAGE_KEY, null)
        return if (savedCode != null) {
            Language.fromCode(savedCode)
        } else {
            Language.getSystemLanguage()
        }
    }

    /**
     * 设置当前语言
     */
    fun setLanguage(language: Language) {
        _currentLanguage.value = language
        prefs.put(LANGUAGE_KEY, language.code)
    }

    // XML资源缓存
    private var chineseXmlResource: XmlBasedStringResource? = null
    private var englishXmlResource: XmlBasedStringResource? = null

    /**
     * 获取XML字符串资源
     */
    fun getXmlResource(language: Language): XmlBasedStringResource {
        return when (language) {
            Language.CHINESE -> {
                if (chineseXmlResource == null) {
                    chineseXmlResource = XmlBasedStringResource(Language.CHINESE)
                }
                chineseXmlResource ?: throw IllegalStateException("中文资源文件未加载")
            }
            Language.ENGLISH -> {
                if (englishXmlResource == null) {
                    englishXmlResource = XmlBasedStringResource(Language.ENGLISH)
                }
                englishXmlResource ?: throw IllegalStateException("英文资源文件未加载")
            }
        }
    }

    /**
     * 获取当前语言的字符串资源
     */
    fun getString(key: String): String {
        val xmlResource = getXmlResource(_currentLanguage.value)
        return xmlResource.get(key)
    }

    /**
     * 获取带参数的字符串资源
     */
    fun getString(key: String, vararg args: Any): String {
        val xmlResource = getXmlResource(_currentLanguage.value)
        return xmlResource.get(key, *args)
    }
}

/**
 * Compose中使用的国际化Hook
 */
@Composable
fun rememberStrings(): StringResource {
    val currentLanguage by LocalizationManager.currentLanguage
    return remember(currentLanguage) {
        StringResource(currentLanguage)
    }
}

/**
 * 字符串资源访问器
 */
class StringResource(private val language: Language) {
    private val xmlResource by lazy { LocalizationManager.getXmlResource(language) }

    fun get(key: String): String = LocalizationManager.getString(key)
    fun get(key: String, vararg args: Any): String = LocalizationManager.getString(key, *args)

    /**
     * 直接从XML资源获取字符串（用于测试和调试）
     */
    fun getFromXml(key: String): String = xmlResource.get(key)

    /**
     * 检查XML资源中是否包含指定key
     */
    fun containsInXml(key: String): Boolean = xmlResource.contains(key)

    /**
     * 获取XML资源信息
     */
    fun getXmlResourceInfo(): String = xmlResource.getResourceInfo()
}
