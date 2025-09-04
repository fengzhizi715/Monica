package cn.netdiscovery.monica.llm

import androidx.compose.runtime.*
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.exception.MonicaException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.LLMComposable
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
 * LLM 调用状态
 */
data class LLMCallState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val result: ColorCorrectionSettings? = null
)

/**
 * 记住 LLM 调用状态
 */
@Composable
fun rememberLLMCallState(): MutableState<LLMCallState> {
    return remember { mutableStateOf(LLMCallState()) }
}

/**
 * LLM 调用结果回调
 */
typealias LLMCallCallback = (ColorCorrectionSettings?) -> Unit

/**
 * 使用 LLM 服务进行自然语言调色
 * 
 * @param provider LLM 服务提供商
 * @param session 对话会话
 * @param instruction 用户指令
 * @param apiKey API 密钥
 * @param onResult 结果回调
 * @param onError 错误回调
 */
@Composable
fun useLLMService(
    provider: LLMProvider,
    session: DialogSession,
    instruction: String,
    apiKey: String,
    onResult: LLMCallCallback,
    onError: (String) -> Unit
) {
    val llmServiceManager = rememberLLMServiceManager()
    val callState = rememberLLMCallState()
    
    LaunchedEffect(provider, session, instruction, apiKey) {
        if (instruction.isBlank() || apiKey.isBlank()) {
            return@LaunchedEffect
        }
        
        callState.value = callState.value.copy(isLoading = true, error = null)
        
        try {
            val result = llmServiceManager.applyInstructionWithLLM(
                provider = provider,
                session = session,
                instruction = instruction,
                apiKey = apiKey
            )
            
            callState.value = callState.value.copy(
                isLoading = false,
                result = result,
                error = null
            )
            
            onResult(result)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is MonicaException -> e.message ?: "LLM 调用失败"
                else -> "未知错误: ${e.message}"
            }
            
            callState.value = callState.value.copy(
                isLoading = false,
                error = errorMessage,
                result = null
            )
            
            logger.error("LLM 调用失败: $errorMessage", e)
            onError(errorMessage)
        }
    }
}

/**
 * 验证 LLM API 密钥
 * 
 * @param provider LLM 服务提供商
 * @param apiKey API 密钥
 * @param onValid 验证成功回调
 * @param onInvalid 验证失败回调
 */
@Composable
fun useLLMValidation(
    provider: LLMProvider,
    apiKey: String,
    onValid: () -> Unit,
    onInvalid: (String) -> Unit
) {
    val llmServiceManager = rememberLLMServiceManager()
    
    LaunchedEffect(provider, apiKey) {
        if (apiKey.isBlank()) {
            onInvalid("API 密钥不能为空")
            return@LaunchedEffect
        }
        
        try {
            val isValid = llmServiceManager.validateApiKey(provider, apiKey)
            if (isValid) {
                onValid()
            } else {
                onInvalid("API 密钥无效")
            }
        } catch (e: Exception) {
            val errorMessage = "验证 API 密钥时发生错误: ${e.message}"
            logger.error(errorMessage, e)
            onInvalid(errorMessage)
        }
    }
}
