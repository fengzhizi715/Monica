package cn.netdiscovery.monica.ui.controlpanel.filter

import cn.netdiscovery.monica.rxcache.Param
import kotlin.math.max

/**
 * 滤镜参数的 UI 元信息（范围/步长/格式），用于让 Slider 的体验可配置且一致。
 */
data class FilterEnumOption(
    val value: Int,
    val labelKey: String
)

data class FilterParamMeta(
    val min: Float,
    val max: Float,
    val step: Float,
    val decimals: Int,
    val enumOptions: List<FilterEnumOption>? = null
)

object FilterParamMetaRegistry {

    private const val DEFAULT_INT_MIN = 0f
    private const val DEFAULT_INT_MAX = 100f

    private const val DEFAULT_FLOAT_MIN = 0f
    private const val DEFAULT_FLOAT_MAX = 10f

    /**
     * 基于参数名/类型给出一个“默认但可维护”的范围配置。
     * 后续如需更精细（按 filterName+paramKey），可以在这里加覆盖表。
     */
    fun resolve(filterName: String, param: Param): FilterParamMeta {
        val key = param.key.lowercase()

        // 常见参数的优先覆盖（比“按类型默认”更符合直觉）
        if (key == "blocksize") {
            // BlockFilter：blockSize 会被用作 Kotlin range 的 step，必须 > 0
            return FilterParamMeta(min = 1f, max = 128f, step = 1f, decimals = 0)
        }
        if (filterName == "ColorFilter" && key == "style" && param.type == "Int") {
            return FilterParamMeta(
                min = 0f,
                max = 11f,
                step = 1f,
                decimals = 0,
                enumOptions = listOf(
                    FilterEnumOption(0, "color_filter_style_0"),
                    FilterEnumOption(1, "color_filter_style_1"),
                    FilterEnumOption(2, "color_filter_style_2"),
                    FilterEnumOption(3, "color_filter_style_3"),
                    FilterEnumOption(4, "color_filter_style_4"),
                    FilterEnumOption(5, "color_filter_style_5"),
                    FilterEnumOption(6, "color_filter_style_6"),
                    FilterEnumOption(7, "color_filter_style_7"),
                    FilterEnumOption(8, "color_filter_style_8"),
                    FilterEnumOption(9, "color_filter_style_9"),
                    FilterEnumOption(10, "color_filter_style_10"),
                    FilterEnumOption(11, "color_filter_style_11")
                )
            )
        }
        if (filterName == "NatureFilter" && key == "style" && param.type == "Int") {
            return FilterParamMeta(
                min = 1f,
                max = 8f,
                step = 1f,
                decimals = 0,
                enumOptions = listOf(
                    FilterEnumOption(1, "nature_filter_style_1"),
                    FilterEnumOption(2, "nature_filter_style_2"),
                    FilterEnumOption(3, "nature_filter_style_3"),
                    FilterEnumOption(4, "nature_filter_style_4"),
                    FilterEnumOption(5, "nature_filter_style_5"),
                    FilterEnumOption(6, "nature_filter_style_6"),
                    FilterEnumOption(7, "nature_filter_style_7"),
                    FilterEnumOption(8, "nature_filter_style_8")
                )
            )
        }
        if (key.contains("brightness")) {
            return FilterParamMeta(min = 0f, max = 2f, step = 0.01f, decimals = 2)
        }
        if (key.contains("contrast")) {
            return FilterParamMeta(min = 0f, max = 3f, step = 0.01f, decimals = 2)
        }
        if (key.contains("hue")) {
            return FilterParamMeta(min = 0f, max = 360f, step = 1f, decimals = 0)
        }
        if (key.contains("saturation")) {
            return FilterParamMeta(min = 0f, max = 2f, step = 0.01f, decimals = 2)
        }

        // 类型默认
        return when (param.type) {
            "Int" -> FilterParamMeta(
                min = DEFAULT_INT_MIN,
                max = max(DEFAULT_INT_MAX, DEFAULT_INT_MIN + 1f),
                step = 1f,
                decimals = 0
            )
            "Float", "Double" -> FilterParamMeta(
                min = DEFAULT_FLOAT_MIN,
                max = max(DEFAULT_FLOAT_MAX, DEFAULT_FLOAT_MIN + 0.01f),
                step = 0.01f,
                decimals = 2
            )
            else -> FilterParamMeta(
                min = DEFAULT_INT_MIN,
                max = DEFAULT_INT_MAX,
                step = 1f,
                decimals = 0
            )
        }
    }
}



