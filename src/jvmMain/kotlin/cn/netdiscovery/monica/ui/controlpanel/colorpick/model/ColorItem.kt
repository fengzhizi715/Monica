package cn.netdiscovery.monica.ui.controlpanel.colorpick.model

import androidx.compose.ui.graphics.Color

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorItem
 * @author: Tony Shen
 * @date: 2024/6/13 19:58
 * @version: V1.0 <描述当前版本功能>
 */
data class ColorItem(var color: Color) {
//    val hexARGB
//        get() = ColorUtil.colorToHexAlpha(color)
//
//    val hex
//        get() = ColorUtil.colorToHex(color)
//
//    val hsvArray
//        get() = ColorUtil.colorToHSV(color)
//
//    val hslArray
//        get() = ColorUtil.colorToHSL(color)
//
//    val rgb
//        get() = ColorUtil.colorToARGBArray(color)
//
//    val alpha:Float
//        get() = color.alpha
//
//    val red: Int
//        get() = color.red.fractionToRGBRange()
//
//    val green: Int
//        get() = color.green.fractionToRGBRange()
//
//    val blue: Int
//        get() = color.blue.fractionToRGBRange()
//
//    var label: String = Unspecified

    companion object {
        const val Unspecified = "?????"
    }
}