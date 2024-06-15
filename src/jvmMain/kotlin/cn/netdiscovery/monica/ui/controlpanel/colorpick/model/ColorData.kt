package cn.netdiscovery.monica.ui.controlpanel.colorpick.model

import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.colorpick.utils.*
import kotlin.math.roundToInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorData
 * @author: Tony Shen
 * @date: 2024/6/13 20:27
 * @version: V1.0 <描述当前版本功能>
 */
data class ColorData(val color: Color, val name: String) {

    val hexText: String
        get() = color.toHex()

    val hslString: String
        get() {
            val arr: FloatArray = color.toHSL()
            return try {
                "H: ${arr[0].roundToInt()}° " +
                        "S: ${arr[1].fractionToIntPercent()}% L: ${arr[2].fractionToIntPercent()}%"
            } catch (e:Exception) {
                ""
            }
        }

    val hsvString: String
        get() {
            val arr: FloatArray = color.toHSV()
            return try {
                "H: ${arr[0].roundToInt()}° " +
                        "S: ${arr[1].fractionToIntPercent()}% V: ${arr[2].fractionToIntPercent()}%"
            } catch (e:Exception) {
                ""
            }
        }

    val rgb: String
        get() {
            val rgb = color.toRGBArray()
            return "R: ${rgb[0]}, G: ${rgb[1]}, B: ${rgb[2]}"
        }
}