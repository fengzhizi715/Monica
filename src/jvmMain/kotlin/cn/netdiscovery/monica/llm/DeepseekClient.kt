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

val systemPromptForColorCorrection = "你是一个图像调色助手。用户会输入一句话，你需要将这句话转换为一组 JSON 格式的调色参数，字段说明如下：\\n\\n- contrast: 对比度，整数，范围 0 - 510，默认值 255。\\n- hue: 色调，整数，范围 0 - 360，默认值 180。\\n- saturation: 饱和度，整数，范围 0 - 510，默认值 255。\\n- lightness: 亮度，整数，范围 0 - 510，默认值 255。\\n- temperature: 色温，整数，范围 0 - 510，默认值 255。\\n- highlight: 高光，整数，范围 0 - 510，默认值 255。\\n- shadow: 阴影，整数，范围 0 - 510，默认值 255。\\n- sharpen: 锐化，整数，范围 0 - 255，默认值 0。\\n- corner: 暗角，整数，范围 0 - 255，默认值 0。\\n- status: 表示用户意图主要修改了哪一项（用于前端高亮显示），值如下：\\n  - 1 表示 contrast\\n  - 2 表示 hue\\n  - 3 表示 saturation\\n  - 4 表示 lightness\\n  - 5 表示 temperature\\n  - 6 表示 highlight\\n  - 7 表示 shadow\\n  - 8 表示 sharpen\\n  - 9 表示 corner\\n\\n要求：\\n- 不要输出解释。\\n- 严格输出 JSON 格式，字段顺序与上方一致。\\n- 如果用户输入不涉及某些参数，请保留默认值。\\n- 请根据语义合理推测用户意图。".trimIndent()

/**
 * 每次根据当前参数和新指令拼 prompt， 支持多轮对话
 */
@Throws(MonicaException::class)
suspend fun applyInstructionWithLLM(
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
        url = "https://api.deepseek.com/chat/completions",
        headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $apiKey"),
        body = payload
    )

    try {
        val json = extractJson(responseJson)
        val responseObj = GsonUtils.fromJson<ColorCorrectionSettings>(json, ColorCorrectionSettings::class.java)

        session.currentSettings = responseObj
        session.history.add(mapOf("user" to instruction))
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