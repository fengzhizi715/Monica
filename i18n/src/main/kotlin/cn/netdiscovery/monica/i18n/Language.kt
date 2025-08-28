package cn.netdiscovery.monica.i18n

/**
 * 支持的语言枚举
 */
enum class Language(val code: String, val displayName: String, val flag: String) {
    CHINESE("zh", "中文", "🇨🇳"),
    ENGLISH("en", "English", "🇺🇸");
    
    companion object {
        fun fromCode(code: String): Language {
            return values().find { it.code == code } ?: CHINESE
        }
        
        fun getSystemLanguage(): Language {
            val systemLang = java.util.Locale.getDefault().language
            return when (systemLang) {
                "zh" -> CHINESE
                "en" -> ENGLISH
                else -> ENGLISH // 默认英文
            }
        }
    }
}
