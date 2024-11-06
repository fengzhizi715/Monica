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
    var hueOffset:Int,
    val saturationOffset:Int,
    val lightnessOffset:Int
)