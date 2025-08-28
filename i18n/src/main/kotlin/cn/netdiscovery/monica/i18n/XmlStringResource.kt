package cn.netdiscovery.monica.i18n

import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.InputStream
import org.slf4j.LoggerFactory
import javax.xml.parsers.DocumentBuilderFactory

/**
 * XML格式的字符串资源加载器
 * 
 * 支持从XML文件加载国际化字符串资源
 */
object XmlStringResource {
    private val logger = LoggerFactory.getLogger(XmlStringResource::class.java.name)
    
    /**
     * 从XML文件加载字符串资源
     */
    fun loadStrings(resourcePath: String): Map<String, String> {
        return try {
            val inputStream: InputStream? = this::class.java.classLoader.getResourceAsStream(resourcePath)
            if (inputStream == null) {
                logger.warn("无法找到资源文件: $resourcePath")
                return emptyMap()
            }
            
            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val document: Document = documentBuilder.parse(inputStream)
            
            val stringMap = mutableMapOf<String, String>()
            val stringNodes = document.getElementsByTagName("string")
            
            for (i in 0 until stringNodes.length) {
                val stringNode = stringNodes.item(i) as Element
                val name = stringNode.getAttribute("name")
                val value = stringNode.textContent
                
                if (name.isNotEmpty() && value.isNotEmpty()) {
                    stringMap[name] = value
                } else {
                    logger.warn("跳过无效的字符串资源: name='$name', value='$value'")
                }
            }
            
            logger.info("成功加载 ${stringMap.size} 个字符串资源从: $resourcePath")
            stringMap
            
        } catch (e: Exception) {
            logger.error("加载XML字符串资源失败: $resourcePath, 错误: ${e.message}")
            e.printStackTrace()
            emptyMap()
        }
    }
    
    /**
     * 获取指定语言的字符串资源
     */
    fun getStringsForLanguage(language: Language): Map<String, String> {
        val resourcePath = when (language) {
            Language.CHINESE -> "strings/strings_zh.xml"
            Language.ENGLISH -> "strings/strings_en.xml"
        }
        
        return loadStrings(resourcePath)
    }
}

/**
 * 基于XML的字符串资源实现
 */
class XmlBasedStringResource(
    private val language: Language
) {
    
    private val strings: Map<String, String> = XmlStringResource.getStringsForLanguage(language)
    private val logger = LoggerFactory.getLogger(XmlBasedStringResource::class.java.name)
    
    fun get(key: String): String {
        val value = strings[key]
        if (value == null) {
            logger.warn("未找到字符串资源: $key (语言: ${language.name})")
            return "[$key]" // 返回带方括号的key作为fallback
        }
        return value
    }
    
    /**
     * 获取带参数替换的字符串
     */
    fun get(key: String, vararg args: Any): String {
        val template = get(key)
        return try {
            String.format(template, *args)
        } catch (e: Exception) {
            logger.warn("字符串格式化失败: $key, 模板: '$template', 参数: ${args.contentToString()}")
            template
        }
    }
    
    /**
     * 检查是否包含指定的key
     */
    fun contains(key: String): Boolean {
        return strings.containsKey(key)
    }
    
    /**
     * 获取所有可用的keys
     */
    fun getAllKeys(): Set<String> {
        return strings.keys
    }
    
    /**
     * 获取资源统计信息
     */
    fun getResourceInfo(): String {
        return "语言: ${language.name}, 字符串数量: ${strings.size}"
    }
}
