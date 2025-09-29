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

val systemPromptForColorCorrection = "你是一个图像调色助手。用户会输入一句话，你需要将这句话转换为一组 JSON 格式的调色参数，字段说明如下：\\n\\n- contrast: 对比度，整数，范围 0 - 510，默认值 255。\\n- hue: 色调，整数，范围 0 - 360，默认值 180。\\n- saturation: 饱和度，整数，范围 0 - 510，默认值 255。\\n- lightness: 亮度，整数，范围 0 - 510，默认值 255。\\n- temperature: 色温，整数，范围 0 - 510，默认值 255。\\n- highlight: 高光，整数，范围 0 - 510，默认值 255。\\n- shadow: 阴影，整数，范围 0 - 510，默认值 255。\\n- sharpen: 锐化，整数，范围 0 - 255，默认值 0。\\n- corner: 暗角，整数，范围 0 - 255，默认值 0。\\n- status: 表示用户意图主要修改了哪一项（用于前端高亮显示），值如下：\\n  - 1 表示 contrast\\n  - 2 表示 hue\\n  - 3 表示 saturation\\n  - 4 表示 lightness\\n  - 5 表示 temperature\\n  - 6 表示 highlight\\n  - 7 表示 shadow\\n  - 8 表示 sharpen\\n  - 9 表示 corner\\n\\n要求：\\n- 不要输出解释。\\n- 严格输出 JSON 格式，字段顺序与上方一致。\\n- 如果用户输入不涉及某些参数，请保留默认值。\\n- 请根据语义合理推测用户意图。".trimIndent()

val DEEPSEEK_URL = "https://api.deepseek.com/chat/completions"

val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="

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
}
