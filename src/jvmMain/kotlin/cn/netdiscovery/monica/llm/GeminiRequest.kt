package cn.netdiscovery.monica.llm

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.GeminiRequest
 * @author: Tony Shen
 * @date: 2025/9/4 16:30
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * Gemini API 请求数据类
 */
data class GeminiRequest(
    val contents: List<GeminiContent>
)

/**
 * Gemini API 内容数据类
 */
data class GeminiContent(
    val parts: List<GeminiPart>
)

/**
 * Gemini API 部分数据类
 */
data class GeminiPart(
    val text: String
)
