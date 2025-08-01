package cn.netdiscovery.monica.history.modules.opencv

import cn.netdiscovery.monica.config.MODULE_OPENCV
import cn.netdiscovery.monica.history.EditHistoryManager
import cn.netdiscovery.monica.history.HistoryEntry

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

fun <T> EditHistoryManager<T>.recordCVOperation(
    module: String = MODULE_OPENCV,
    operation: String,
    description: String = "",
    buildParams: CVParams.() -> Unit
) {
    val params = CVParams(operation).apply(buildParams)
    val entry = HistoryEntry(module = module, operation = operation, parameters = params.parameters, description = description)
    push(params as T, entry)
    logOnly(entry)
}