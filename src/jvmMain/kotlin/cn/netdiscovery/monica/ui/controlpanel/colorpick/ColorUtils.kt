package cn.netdiscovery.monica.ui.controlpanel.colorpick

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ColorUtils
 * @author: Tony Shen
 * @date: 2024/6/13 17:10
 * @version: V1.0 <描述当前版本功能>
 */
fun hexToRGB(colorString: String): IntArray {

    val completeColorString = if (colorString.first() == '#') colorString else "#$colorString"
    val rgb = RGB(completeColorString)
    return intArrayOf(rgb.redInt,rgb.greenInt, rgb.blueInt)
}

fun colorToHex(color: Color): String {

    val rgb = RGBInt(color.toArgb().toUInt()).toSRGB()
    return rgb.toHex()
}

fun colorToHSL(color: Color): FloatArray {

    val rgb = RGBInt(color.toArgb().toUInt()).toSRGB()
    return rgb.toHSL().toArray()
}

fun colorToHSV(color: Color): FloatArray {

    val rgb = RGBInt(color.toArgb().toUInt()).toSRGB()
    return rgb.toHSV().toArray()
}

fun colorToRGBArray(color: Color): IntArray {

    val rgb = RGBInt(color.toArgb().toUInt()).toSRGB()
    return intArrayOf(rgb.redInt,rgb.greenInt, rgb.blueInt)
}