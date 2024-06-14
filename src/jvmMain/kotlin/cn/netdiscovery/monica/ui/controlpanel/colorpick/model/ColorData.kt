package cn.netdiscovery.monica.ui.controlpanel.colorpick.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import cn.netdiscovery.monica.ui.controlpanel.colorpick.*
import kotlin.math.roundToInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorData
 * @author: Tony Shen
 * @date: 2024/6/13 20:27
 * @version: V1.0 <描述当前版本功能>
 */
@Immutable
data class ColorData(val color: Color, val name: String) {

    @Stable
    val hexText: String
        get() = colorToHex(color = color)

    @Stable
    val hslString: String
        get() {
            val arr: FloatArray = colorToHSL(color)
            return try {
                "H: ${arr[0].roundToInt()}° " +
                        "S: ${arr[1].fractionToIntPercent()}% L: ${arr[2].fractionToIntPercent()}%"
            } catch (e:Exception) {
                ""
            }
        }

    @Stable
    val hsvString: String
        get() {
            val arr: FloatArray = colorToHSV(color)
            return try {
                "H: ${arr[0].roundToInt()}° " +
                        "S: ${arr[1].fractionToIntPercent()}% V: ${arr[2].fractionToIntPercent()}%"
            } catch (e:Exception) {
                ""
            }
        }

    @Stable
    val rgb: String
        get() {
            val rgb = colorToRGBArray(color)
            return "R: ${rgb[0]}, G: ${rgb[1]}, B: ${rgb[2]}"
        }
}