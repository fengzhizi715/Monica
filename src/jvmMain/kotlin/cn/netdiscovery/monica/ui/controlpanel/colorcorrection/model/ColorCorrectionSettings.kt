package cn.netdiscovery.monica.ui.controlpanel.colorcorrection.model

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorcorrection.model.ColorCorrectionSettings
 * @author: Tony Shen
 * @date: 2024/11/6 10:40
 * @version: V1.0 <描述当前版本功能>
 */
data class ColorCorrectionSettings(
    val contrast:Int = 255,
    val hue:Int = 180,
    val saturation:Int = 255,
    val lightness:Int = 255,
    val temperature:Int = 255,
    val highlight:Int = 255,
    val shadow:Int = 255,
    val sharpen:Int = 0,
    val corner:Int = 0,

    val status:Int = 0 // 1 contrast, 2 hue, 3 saturation, 4 lightness, 5 temperature, 6 highlight, 7 shadow, 8 sharpen, 9 corner
)