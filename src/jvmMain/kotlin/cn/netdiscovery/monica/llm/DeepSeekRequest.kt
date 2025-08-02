package cn.netdiscovery.monica.llm

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.DeepSeekRequest
 * @author: Tony Shen
 * @date: 2025/8/2 17:08
 * @version: V1.0 <描述当前版本功能>
 */
data class DeepSeekRequest(
    val model: String,
    val messages: List<DeepSeekMessage>,
    val stream: Boolean
)

data class DeepSeekMessage(
    val role: String,
    val content: String
)