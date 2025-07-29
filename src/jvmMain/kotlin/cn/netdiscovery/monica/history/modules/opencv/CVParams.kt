package cn.netdiscovery.monica.history.modules.opencv

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.history.modules.opencv.CVParams
 * @author: Tony Shen
 * @date: 2025/7/29 17:17
 * @version: V1.0 <描述当前版本功能>
 */
data class CVParams(
    val operation: String = "",
    val parameters: MutableMap<String, Any> = HashMap()
)