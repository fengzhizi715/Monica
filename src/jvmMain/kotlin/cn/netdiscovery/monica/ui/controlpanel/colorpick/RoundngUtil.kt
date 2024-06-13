package cn.netdiscovery.monica.ui.controlpanel.colorpick

import kotlin.math.roundToInt

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.RoundngUtil
 * @author: Tony Shen
 * @date: 2024/6/13 20:37
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * Converts alpha, red, green or blue values from range of [0f-1f] to [0-255].
 */
fun Float.fractionToRGBRange() = (this * 255.0f).toInt()

/**
 * Converts alpha, red, green or blue values from range of [0f-1f] to [0-255] and returns
 * it as [String].
 */
fun Float.fractionToRGBString() = this.fractionToRGBRange().toString()

/**
 * Rounds this [Float] to another with 2 significant numbers
 * 0.1234 is rounded to 0.12
 * 0.127 is rounded to 0.13
 */
fun Float.roundToTwoDigits() = (this * 100.0f).roundToInt() / 100.0f

/**
 * Rounds this [Float] to closest int.
 */
fun Float.round() = this.roundToInt()

/**
 * Converts **HSV** or **HSL** colors that are in range of [0f-1f] to [0-100] range in [Integer]
 * with [Float.roundToInt]
 */
fun Float.fractionToPercent() = (this * 100.0f).roundToInt()

/**
 * Converts **HSV** or **HSL** colors that are in range of [0f-1f] to [0-100] range in [Integer]
 * with [Float.toInt]
 */
fun Float.fractionToIntPercent() = (this * 100.0f).toInt()