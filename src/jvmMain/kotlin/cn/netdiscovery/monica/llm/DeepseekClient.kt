package cn.netdiscovery.monica.llm

import cn.netdiscovery.http.core.utils.extension.asyncCall
import cn.netdiscovery.monica.http.httpClient
import com.safframework.rxcache.utils.GsonUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.DeepseekClient
 * @author: Tony Shen
 * @date: 2025/8/1 13:59
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

fun deepSeekColorCorrection(apiKey: String,
                            content: String,
                            failure: (e:Exception) -> Unit) {

    val messages = mutableListOf<DeepSeekMessage>().apply {
        this.add(DeepSeekMessage(role = "system",
            content = "你是一个图像调色助手。用户会输入一句话，你需要将这句话转换为一组 JSON 格式的调色参数，字段说明如下：\\n\\n- contrast: 对比度，整数，范围 0 - 510，默认值 255。\\n- hue: 色调，整数，范围 0 - 360，默认值 180。\\n- saturation: 饱和度，整数，范围 0 - 510，默认值 255。\\n- lightness: 亮度，整数，范围 0 - 510，默认值 255。\\n- temperature: 色温，整数，范围 0 - 510，默认值 255。\\n- highlight: 高光，整数，范围 0 - 510，默认值 255。\\n- shadow: 阴影，整数，范围 0 - 510，默认值 255。\\n- sharpen: 锐化，整数，范围 0 - 255，默认值 0。\\n- corner: 暗角，整数，范围 0 - 255，默认值 0。\\n- status: 表示用户意图主要修改了哪一项（用于前端高亮显示），值如下：\\n  - 1 表示 contrast\\n  - 2 表示 hue\\n  - 3 表示 saturation\\n  - 4 表示 lightness\\n  - 5 表示 temperature\\n  - 6 表示 highlight\\n  - 7 表示 shadow\\n  - 8 表示 sharpen\\n  - 9 表示 corner\\n\\n要求：\\n- 不要输出解释。\\n- 严格输出 JSON 格式，字段顺序与上方一致。\\n- 如果用户输入不涉及某些参数，请保留默认值。\\n- 请根据语义合理推测用户意图。"))
        this.add(DeepSeekMessage(role = "user", content = content))
    }
    val deepSeekRequest = DeepSeekRequest("deepseek-chat", messages, false)
    val json = GsonUtils.toJson(deepSeekRequest)
    val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)

    try {
        httpClient.okHttpClient()
            .asyncCall {
                Request.Builder()
                    .url("https://api.deepseek.com/chat/completions")
                    .header("Content-Type","application/json")
                    .header("Authorization","Bearer $apiKey")
                    .post(requestBody)
                    .build()
            }
            .get()
            .use { response->

                val jsonData = response.body?.string()
                val jsonObject: JSONObject = JSONObject(jsonData)
                val content = jsonObject.getJSONArray("choices")?.getJSONObject(0)?.getJSONObject("message")?.get("content")?.toString()?:""
            }
    } catch (e:Exception){
        e.printStackTrace()
        failure.invoke(e)
    }
}