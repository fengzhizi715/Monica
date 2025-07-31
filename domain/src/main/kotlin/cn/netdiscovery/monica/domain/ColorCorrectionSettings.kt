package cn.netdiscovery.monica.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.domain.ColorCorrectionSettings
 * @author: Tony Shen
 * @date: 2024/11/6 10:40
 * @version: V1.0 <描述当前版本功能>
 */
data class ColorCorrectionSettings(
    val contrast:Int = 255,     // 对比度，范围 0-510
    val hue:Int = 180,          // 色调，范围 0-360
    val saturation:Int = 255,   // 饱和度，范围 0-510
    val lightness:Int = 255,    // 亮度，范围 0-510
    val temperature:Int = 255,  // 色温，范围 0-510
    val highlight:Int = 255,    // 高光，范围 0-510
    val shadow:Int = 255,       // 阴影，范围 0-510
    val sharpen:Int = 0,        // 锐化，范围 0-255
    val corner:Int = 0,         // 暗角，范围 0-255

    val status:Int = 0 // 1 contrast, 2 hue, 3 saturation, 4 lightness, 5 temperature, 6 highlight, 7 shadow, 8 sharpen, 9 corner
)