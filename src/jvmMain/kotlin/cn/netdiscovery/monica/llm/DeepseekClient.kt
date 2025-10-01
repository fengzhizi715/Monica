package cn.netdiscovery.monica.llm

import cn.netdiscovery.http.core.utils.extension.asyncCall
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.exception.MonicaException
import cn.netdiscovery.monica.http.httpClient
import com.safframework.rxcache.utils.GsonUtils
import kotlinx.coroutines.runBlocking
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.DeepseekClient
 * @author: Tony Shen
 * @date: 2025/8/1 13:59
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

/**
 * 每次根据当前参数和新指令拼 prompt， 支持多轮对话
 */
@Throws(MonicaException::class)
fun applyInstructionWithLLM(
    session: DialogSession,
    instruction: String,
    apiKey: String
): ColorCorrectionSettings? {
    val prompt = buildString {
        append("当前图像的参数如下：\n")
        append(GsonUtils.toJson(session.currentSettings))
        append("\n\n")
        append("用户指令：$instruction")
    }
    val messages = mutableListOf<DeepSeekMessage>().apply {
        this.add(DeepSeekMessage(role = "system", content = systemPromptForColorCorrection))
        this.add(DeepSeekMessage(role = "user", content = prompt))
    }
    val deepSeekRequest = DeepSeekRequest("deepseek-chat", messages, false)
    val payload = GsonUtils.toJson(deepSeekRequest)

    val responseJson = sendPostJson(
        url = DEEPSEEK_URL,
        headers = mapOf("Content-Type" to "application/json",
            "Authorization" to "Bearer $apiKey"),
        body = payload
    )

    try {
        val json = extractJson(responseJson)
        val responseObj = GsonUtils.fromJson<ColorCorrectionSettings>(json, ColorCorrectionSettings::class.java)

        session.currentSettings = responseObj
        // 历史记录现在在 LLMServiceManager 中处理
        return responseObj
    } catch (e: Exception) {
        logger.error("responseJson = $responseJson")
        logger.error(e.message, e)
        throw MonicaException("无法获取调色的参数")
    }
}

fun extractJson(jsonData: String): String {
    val jsonObject: JSONObject = JSONObject(jsonData)
    var content = jsonObject.getJSONArray("choices")?.getJSONObject(0)?.getJSONObject("message")?.get("content")?.toString()?:""

    if (content.isNotEmpty()) {
        content = content.replace("```json","").replace("```","")
        return content
    } else {
        throw MonicaException("无法获取调色的参数")
    }
}

fun sendPostJson(url: String, headers: Map<String, String>, body: Any): String {

    return runBlocking {
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body.toString())

        httpClient.okHttpClient()
            .asyncCall {
                Request.Builder()
                    .url(url)
                    .headers(headers.toHeaders())
                    .post(requestBody)
                    .build()
            }
            .get().body?.string()?:""
    }
}