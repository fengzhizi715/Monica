package cn.netdiscovery.monica.imageprocess.lut

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.lut.LUT
 * @author: Tony Shen
 * @date: 2024/6/16 15:43
 * @version: V1.0 <描述当前版本功能>
 */
const val AUTUMN_STYLE: Int = 0
const val BONE_STYLE = 1
const val COOL_STYLE = 2
const val HOT_STYLE = 3
const val HSV_STYLE = 4
const val JET_STYLE = 5
const val OCEAN_STYLE = 6
const val PINK_STYLE = 7
const val RAINBOW_STYLE = 8
const val SPRING_STYLE = 9
const val SUMMER_STYLE = 10
const val WINTER_STYLE = 11

fun getColorFilterLUT(style: Int): Array<IntArray> {

    return when (style) {
        AUTUMN_STYLE -> AutumnLUT.AUTUMN_LUT
        BONE_STYLE -> BoneLUT.BONE_LUT
        COOL_STYLE -> CoolLUT.COOL_LUT
        HOT_STYLE -> HotLUT.HOT_LUT
        HSV_STYLE -> HsvLUT.HSV_LUT
        JET_STYLE -> JetLUT.JET_LUT
//        OCEAN_STYLE -> OCEAN_LUT
//        PINK_STYLE -> PINK_LUT
//        RAINBOW_STYLE -> RAINBOW_LUT
//        SPRING_STYLE -> SPRING_LUT
//        SUMMER_STYLE -> SUMMER_LUT
//        WINTER_STYLE -> WINTER_LUT
        else -> AutumnLUT.AUTUMN_LUT
    }
}