package cn.netdiscovery.monica.ui.controlpanel.colorpick.model

import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.colorpick.colorNameMap
import cn.netdiscovery.monica.ui.controlpanel.colorpick.hexToRGB
import cn.netdiscovery.monica.ui.controlpanel.colorpick.toRGBArray
import kotlin.math.sqrt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ColorNameParser
 * @author: Tony Shen
 * @date: 2024/6/13 17:04
 * @version: V1.0 <描述当前版本功能>
 */
const val Unspecified = "?????"

internal data class RGBData(val x: Int, val y: Int, val z: Int, val label: String)

class ColorNameParser internal constructor() {

    private val rbgData: List<RGBData> by lazy {
        colorNameMap.map { entry: Map.Entry<String, String> ->
            val rgbArray = hexToRGB(entry.key)
            val label = entry.value
            RGBData(
                x = rgbArray[0],
                y = rgbArray[1],
                z = rgbArray[2],
                label = label
            )
        }
    }

    /**
     * Parse name of [Color]
     */
    fun parseColorName(color: Color): String {
        val rgbArray = color.toRGBArray()

        val red: Int = rgbArray[0]
        val green: Int = rgbArray[1]
        val blue: Int = rgbArray[2]

        var distance: Int=Int.MAX_VALUE

        var colorId = -1

        rbgData.forEachIndexed { index, rgbData ->
            val currentDistance = sqrt(
                (
                        (rgbData.x - red) * (rgbData.x - red) +
                                (rgbData.y - green) * (rgbData.y - green) +
                                (rgbData.z - blue) * (rgbData.z - blue)
                        ).toDouble()
            ).toInt()

            if (currentDistance < distance) {
                distance = currentDistance
                colorId = index
            }
        }

        return   if (colorId >= 0) {
            rbgData[colorId].label
        } else Unspecified
    }
}