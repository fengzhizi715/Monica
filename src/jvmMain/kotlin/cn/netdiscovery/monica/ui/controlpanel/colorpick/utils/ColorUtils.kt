package cn.netdiscovery.monica.ui.controlpanel.colorpick.utils

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

fun Color.toHex():String = RGBInt(this.toArgb().toUInt()).toSRGB().toHex()

fun Color.toHSL():FloatArray = RGBInt(this.toArgb().toUInt()).toSRGB().toHSL().toArray()

fun Color.toHSV():FloatArray = RGBInt(this.toArgb().toUInt()).toSRGB().toHSV().toArray()

fun Color.toRGBArray(): IntArray {
    val rgb = RGBInt(this.toArgb().toUInt()).toSRGB()
    return intArrayOf(rgb.redInt,rgb.greenInt, rgb.blueInt)
}