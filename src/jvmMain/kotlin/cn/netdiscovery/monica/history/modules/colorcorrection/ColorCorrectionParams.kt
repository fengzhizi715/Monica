package cn.netdiscovery.monica.history.modules.colorcorrection

import cn.netdiscovery.monica.domain.ColorCorrectionSettings

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.history.modules.colorcorrection.ColorCorrectionParams
 * @author: Tony Shen
 * @date:  2025/7/27 13:16
 * @version: V1.0 调色参数封装
 */
data class ColorCorrectionParams(
    val contrast: Int = 255,
    val hue: Int = 180,
    val saturation: Int = 255,
    val lightness: Int = 255,
    val temperature: Int = 255,
    val highlight: Int = 255,
    val shadow: Int = 255,
    val sharpen: Int = 0,
    val corner: Int = 0,
    val status: Int = 0 // 1 ~ 9 表示最近调整项，可选
) {

    fun toMap(): Map<String, Any> = mapOf(
        "contrast" to contrast,
        "hue" to hue,
        "saturation" to saturation,
        "lightness" to lightness,
        "temperature" to temperature,
        "highlight" to highlight,
        "shadow" to shadow,
        "sharpen" to sharpen,
        "corner" to corner,
        "status" to status
    )

    fun toSettings(): ColorCorrectionSettings = ColorCorrectionSettings(
        contrast, hue, saturation, lightness, temperature,
        highlight, shadow, sharpen, corner, status
    )

    companion object {
        fun fromMap(map: Map<String, Any>): ColorCorrectionParams = ColorCorrectionParams(
            contrast = (map["contrast"] as? Number)?.toInt() ?: 255,
            hue = (map["hue"] as? Number)?.toInt() ?: 180,
            saturation = (map["saturation"] as? Number)?.toInt() ?: 255,
            lightness = (map["lightness"] as? Number)?.toInt() ?: 255,
            temperature = (map["temperature"] as? Number)?.toInt() ?: 255,
            highlight = (map["highlight"] as? Number)?.toInt() ?: 255,
            shadow = (map["shadow"] as? Number)?.toInt() ?: 255,
            sharpen = (map["sharpen"] as? Number)?.toInt() ?: 0,
            corner = (map["corner"] as? Number)?.toInt() ?: 0,
            status = (map["status"] as? Number)?.toInt() ?: 0
        )

        fun fromSettings(settings: ColorCorrectionSettings): ColorCorrectionParams =
            ColorCorrectionParams(
                contrast = settings.contrast,
                hue = settings.hue,
                saturation = settings.saturation,
                lightness = settings.lightness,
                temperature = settings.temperature,
                highlight = settings.highlight,
                shadow = settings.shadow,
                sharpen = settings.sharpen,
                corner = settings.corner,
                status = settings.status
            )
    }
}
