package cn.netdiscovery.monica.llm

import androidx.compose.runtime.*
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.exception.MonicaException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.LLMServiceManager
 * @author: Tony Shen
 * @date: 2025/9/4 17:45
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

/**
 * 记住 LLM 服务管理器实例
 */
@Composable
fun rememberLLMServiceManager(): LLMServiceManager {
    return remember { LLMServiceManager() }
}

/**
 * LLM 服务提供商枚举
 */
enum class LLMProvider {
    DEEPSEEK,
    GEMINI
}

/**
 * LLM 服务管理器
 * 统一管理不同的 LLM 服务提供商
 */

/**
 * LLM 服务管理器
 * 统一管理不同的 LLM 服务提供商
 */
class LLMServiceManager {
    
    /**
     * 使用指定的 LLM 服务提供商进行自然语言调色
     * 
     * @param provider LLM 服务提供商
     * @param session 对话会话
     * @param instruction 用户指令
     * @param apiKey API 密钥
     * @return 更新后的颜色校正设置
     * @throws MonicaException 当调用失败时抛出异常
     */
    suspend fun applyInstructionWithLLM(
        provider: LLMProvider,
        session: DialogSession,
        instruction: String,
        apiKey: String
    ): ColorCorrectionSettings? {
        val result = when (provider) {
            LLMProvider.DEEPSEEK -> {
                logger.info("使用 DeepSeek 进行自然语言调色")
                applyInstructionWithLLM(session, instruction, apiKey)
            }
            LLMProvider.GEMINI -> {
                logger.info("使用 Gemini 进行自然语言调色")
                applyInstructionWithGemini(session, instruction, apiKey)
            }
        }
        
        // 如果调用成功，记录历史记录
        result?.let { settings ->
            val historyItem = ColorCorrectionHistoryItem(
                userInstruction = instruction,
                resultSettings = settings,
                usedProvider = provider
            )
            session.history.add(historyItem)
            logger.info("记录调色历史: 使用 ${provider.name} 处理指令 '$instruction'")
        }
        
        return result
    }
    
    /**
     * 验证 API 密钥是否有效
     * 
     * @param provider LLM 服务提供商
     * @param apiKey API 密钥
     * @return 是否有效
     */
    fun validateApiKey(provider: LLMProvider, apiKey: String): Boolean {
        return try {
            when (provider) {
                LLMProvider.DEEPSEEK -> validateDeepSeekApiKey(apiKey)
                LLMProvider.GEMINI -> validateGeminiApiKey(apiKey)
            }
        } catch (e: Exception) {
            logger.error("验证 ${provider.name} API 密钥失败: ${e.message}")
            false
        }
    }
}

/**
 * 扩展函数：验证 DeepSeek API 密钥
 */
private fun validateDeepSeekApiKey(apiKey: String): Boolean {
    // 这里可以实现一个简单的 API 调用来验证密钥
    // 暂时返回 true，实际项目中可以调用一个简单的 API 端点
    return apiKey.isNotBlank()
}

/**
 * 扩展函数：验证 Gemini API 密钥
 */
private fun validateGeminiApiKey(apiKey: String): Boolean {
    // 这里可以实现一个简单的 API 调用来验证密钥
    // 暂时返回 true，实际项目中可以调用一个简单的 API 端点
    return apiKey.isNotBlank()
}
