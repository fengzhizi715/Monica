package cn.netdiscovery.monica.ui.controlpanel.filter

import cn.netdiscovery.monica.rxcache.Param
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.utils.extensions.safelyConvertToInt
import java.util.Locale
import kotlin.math.max

/**
 * 生成某个滤镜的“默认参数”Map，用于 Reset / 初始化 / 判断是否处于默认状态。
 *
 * 注意：
 * - Float/Double 会按 [FilterParamMetaRegistry] 的 decimals 格式化，避免 UI 显示不一致。
 */
fun buildDefaultParamMap(filterName: String): Map<Pair<String, String>, String> {
    val params: List<Param> = getFilterParam(filterName).orEmpty()
    val result = LinkedHashMap<Pair<String, String>, String>(params.size)

    params.forEach { param ->
        val meta = FilterParamMetaRegistry.resolve(filterName = filterName, param = param)
        val key = Pair(param.key, param.type)
        val defaultValue = when (param.type) {
            "Int" -> {
                val raw = param.value.toString().safelyConvertToInt() ?: 0
                // 兜底：像 BlockFilter 的 blockSize 这种会作为 step 的参数，必须 >= meta.min
                max(meta.min.toInt(), raw).toString()
            }
            "Float" -> {
                val v = param.value.toString().toDoubleOrNull() ?: 0.0
                String.format(Locale.US, "%.${meta.decimals}f", v)
            }
            "Double" -> {
                val v = param.value.toString().toDoubleOrNull() ?: 0.0
                String.format(Locale.US, "%.${meta.decimals}f", v)
            }
            else -> param.value.toString()
        }
        result[key] = defaultValue
    }
    return result
}


