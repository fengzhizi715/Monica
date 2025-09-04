package cn.netdiscovery.monica.llm

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.GeminiRequest
 * @author: Tony Shen
 * @date: 2025/9/4 16:30
 * @version: V1.0 <描述当前版本功能>
 */

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent
)
