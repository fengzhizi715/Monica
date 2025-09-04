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
 *          cn.netdiscovery.monica.llm.GeminiClient
 * @author: Tony Shen
 * @date: 2025/9/4 16:30
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

/**
 * 使用 Gemini API 进行自然语言调色
 */
@Throws(MonicaException::class)
suspend fun applyInstructionWithGemini(
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
    
    val geminiRequest = GeminiRequest(
        contents = listOf(
            GeminiContent(
                parts = listOf(
                    GeminiPart(text = systemPromptForColorCorrection + "\n\n" + prompt)
                )
            )
        )
    )
    
    val payload = GsonUtils.toJson(geminiRequest)

    val responseJson = sendPostJsonToGemini(
        url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey",
        headers = mapOf("Content-Type" to "application/json"),
        body = payload
    )

    try {
        val json = extractJsonFromGemini(responseJson)
        val responseObj = GsonUtils.fromJson<ColorCorrectionSettings>(json, ColorCorrectionSettings::class.java)

        session.currentSettings = responseObj
        session.history.add(Pair(instruction, responseObj)) // 保存用户指令、调色参数
        return responseObj
    } catch (e: Exception) {
        logger.error("responseJson = $responseJson")
        logger.error(e.message, e)
        throw MonicaException("无法获取调色的参数")
    }
}

fun extractJsonFromGemini(jsonData: String): String {
    val jsonObject: JSONObject = JSONObject(jsonData)
    var content = jsonObject.getJSONArray("candidates")?.getJSONObject(0)?.getJSONObject("content")?.getJSONArray("parts")?.getJSONObject(0)?.get("text")?.toString()?:""

    if (content.isNotEmpty()) {
        content = content.replace("```json","").replace("```","")
        return content
    } else {
        throw MonicaException("无法获取调色的参数")
    }
}

fun sendPostJsonToGemini(url: String, headers: Map<String, String>, body: Any): String {
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
