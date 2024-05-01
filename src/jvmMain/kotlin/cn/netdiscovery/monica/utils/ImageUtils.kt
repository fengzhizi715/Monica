package cn.netdiscovery.monica.utils

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import cn.netdiscovery.monica.imageprocess.filter.*
import cn.netdiscovery.monica.state.ApplicationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Color
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.ImageUtils
 * @author: Tony Shen
 * @date: 2024/4/26 22:11
 * @version: V1.0 <描述当前版本功能>
 */

fun clamp(c: Int): Int {
    return if (c > 255) 255 else if (c < 0) 0 else c
}

fun clamp(x: Int, a: Int, b: Int): Int {
    return if (x < a) a else if (x > b) b else x
}

/**
 * 调整色相、饱和度、亮度
 *
 * @param image 图片
 * @param satuPer 饱和度
 * @param huePer 色相
 * @param lumPer 亮度
 * */
suspend fun hsl(image: BufferedImage, satuPer: Float, huePer: Float, lumPer: Float): BufferedImage {

    return withContext(Dispatchers.IO) {
        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until bufferedImage.height) {
            for (x in 0 until bufferedImage.width) {
                val pixel = image.getRGB(x, y)
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF

                val hsb: FloatArray = Color.RGBtoHSB(r, g, b, null)
                val hue = (hsb[0] + hsb[0] * huePer).coerceIn(0f, 1f)
                val saturation = (hsb[1] + hsb[1] * satuPer).coerceIn(0f, 1f)
                val brightness = (hsb[2] + hsb[2] * lumPer).coerceIn(0f, 1f)

                val rgb = Color.HSBtoRGB(hue, saturation, brightness)

                bufferedImage.setRGB(x, y, rgb)
            }
        }
        bufferedImage
    }
}

suspend fun doFilter(filterName:String, array:MutableList<Any>, state: ApplicationState):BufferedImage {

    return withContext(Dispatchers.IO) {
        when(filterName) {
            "BilateralFilter" -> {
                BilateralFilter(array[0] as Double,array[1] as Double).transform(state.currentImage!!)
            }
            "BoxBlurFilter" -> {
                BoxBlurFilter(array[0] as Int,array[1] as Int,array[2] as Int).transform(state.currentImage!!)
            }
            "ConBriFilter" -> {
                ConBriFilter(array[0] as Float,array[1] as Float).transform(state.currentImage!!)
            }
            "GammaFilter" -> {
                GammaFilter(array[0] as Double).transform(state.currentImage!!)
            }
            "GaussianFilter" -> {
                GaussianFilter(array[0] as Float).transform(state.currentImage!!.toComposeImageBitmap().toAwtImage())
            }
            "GradientFilter" -> {
                GradientFilter().transform(state.currentImage!!)
            }
            "SpotlightFilter" -> {
                SpotlightFilter(array[0] as Int).transform(state.currentImage!!)
            }

            else -> {
                state.currentImage!!
            }
        }
    }

}