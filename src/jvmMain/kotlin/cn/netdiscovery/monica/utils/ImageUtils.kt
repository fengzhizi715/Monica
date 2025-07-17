package cn.netdiscovery.monica.utils

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import cn.netdiscovery.monica.domain.HeifImage
import cn.netdiscovery.monica.domain.RawImage
import cn.netdiscovery.monica.exception.MonicaException
import cn.netdiscovery.monica.imageprocess.filter.PosterizeFilter
import cn.netdiscovery.monica.imageprocess.filter.*
import cn.netdiscovery.monica.imageprocess.filter.blur.*
import cn.netdiscovery.monica.imageprocess.filter.sharpen.LaplaceSharpenFilter
import cn.netdiscovery.monica.imageprocess.filter.sharpen.SharpenFilter
import cn.netdiscovery.monica.imageprocess.filter.sharpen.USMFilter
import cn.netdiscovery.monica.imageprocess.utils.extension.convertToRGB
import cn.netdiscovery.monica.imageprocess.utils.loadFixedSvgAsImage
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extensions.printConstructorParamsWithValues
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.ImageUtils
 * @author: Tony Shen
 * @date: 2024/4/26 22:11
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

fun getBufferedImage(file: File): BufferedImage {

    val filePath = file.absolutePath

    val imageFormat = ImageFormatDetector.detectFormat(file)
    logger.info("format: $imageFormat")

    if (imageFormat.isRaw()) {
        val rawImage = ImageProcess.decodeRawToBuffer(filePath, true)
        if (rawImage!=null) {
            return rawImageToBuffered(rawImage) // 再把 RawImage 对象转换成 BufferedImage
        } else {
            throw MonicaException("Image format is not supported")
        }
    } else {
        return when(imageFormat) {
            ImageFormat.SVG -> loadFixedSvgAsImage(file) ?: ImageIO.read(file)
            ImageFormat.HDR -> {
                ImageIO.read(file).convertToRGB()
            }
            ImageFormat.JPEG, ImageFormat.PNG, ImageFormat.WEBP -> {
                ImageIO.read(file)
            }
            ImageFormat.HEIC -> {
                val image = ImageProcess.decodeHeif(filePath)
                if (image!=null) {
                    heifImageToBuffered(image)
                }  else {
                    throw MonicaException("Image format is not supported")
                }
            }
            else -> throw MonicaException("Unsupported image format: $imageFormat")
        }
    }
}

private fun rawImageToBuffered(raw: RawImage): BufferedImage {
    val image = BufferedImage(raw.width, raw.height, BufferedImage.TYPE_3BYTE_BGR)
    val raster = image.raster
    raster.setDataElements(0, 0, raw.width, raw.height, raw.data)
    return image
}

private fun heifImageToBuffered(image: HeifImage): BufferedImage {
    val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
    bufferedImage.setRGB(0, 0, image.width, image.height, image.pixels, 0, image.width)
    return bufferedImage
}

suspend fun doFilter(filterName:String, array:MutableList<Any>, state: ApplicationState):BufferedImage {

    return withContext(IO) {
        when(filterName) {
            "AverageFilter" -> {
                AverageFilter().transform(state.currentImage!!)
            }
            "BilateralFilter" -> {
                val filter = BilateralFilter(array[0] as Double, array[1] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "BlockFilter" -> {
                val filter = BlockFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "BoxBlurFilter" -> {
                val filter = BoxBlurFilter(array[0] as Int,array[2] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "BumpFilter" -> {
                BumpFilter().transform(state.currentImage!!)
            }
            "CarveFilter" -> {
                val filter = CarveFilter()
                filter.transform(state.currentImage!!)
            }
            "ColorFilter" -> {
                val filter = ColorFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "ColorHalftoneFilter" -> {
                val filter = ColorHalftoneFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "ConBriFilter" -> {
                val filter = ConBriFilter(array[1] as Float,array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "CropFilter" -> {
                val filter = CropFilter(array[2] as Int,array[3] as Int,array[1] as Int,array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "CrystallizeFilter" -> {
                val filter = CrystallizeFilter(array[0] as Float, array[3] as Float, array[2] as Float, array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "DiffuseFilter" -> {
                val filter = DiffuseFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "EmbossFilter" -> {
                val filter = EmbossFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "EqualizeFilter" -> {
                val filter = EqualizeFilter()
                filter.transform(state.currentImage!!)
            }
            "ExposureFilter" -> {
                val filter = ExposureFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "GainFilter" -> {
                val filter = GainFilter(array[1] as Float, array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "GammaFilter" -> {
                val filter = GammaFilter(array[0] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "FastBlur2D" -> {
                FastBlur2D(array[0] as Int).transform(state.currentImage!!)
            }
            "GaussianFilter" -> {
                val filter = GaussianFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "GaussianNoiseFilter" -> {
                val filter = GaussianNoiseFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "GradientFilter" -> {
                GradientFilter().transform(state.currentImage!!)
            }
            "GrayFilter" -> {
                GrayFilter().transform(state.currentImage!!)
            }
            "HighPassFilter" -> {
                val filter = HighPassFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "HSBAdjustFilter" -> {
                val filter = HSBAdjustFilter(array[1] as Float, array[2] as Float, array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "InvertFilter" -> {
                InvertFilter().transform(state.currentImage!!)
            }
            "LaplaceSharpenFilter" -> {
                LaplaceSharpenFilter().transform(state.currentImage!!)
            }
            "LensBlurFilter" -> {
                val filter = LensBlurFilter(array[3] as Float,array[1] as Float,array[2] as Float,array[0] as Float,array[4] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "MarbleFilter" -> {
                val filter = MarbleFilter(array[1] as Float,array[2] as Float,array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "MaximumFilter" -> {
                val filter = MaximumFilter()
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "MinimumFilter" -> {
                val filter = MinimumFilter()
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "MirrorFilter" -> {
                val filter = MirrorFilter(array[2] as Float,array[0] as Float,array[1] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "MosaicFilter" -> {
                val filter = MosaicFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "MotionFilter" -> {
                val filter = MotionFilter(array[1] as Float,array[0] as Float,array[2] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "NatureFilter" -> {
                val filter = NatureFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "OffsetFilter" -> {
                val filter = OffsetFilter(array[0] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "OilPaintFilter" -> {
                val filter = OilPaintFilter(array[1] as Int,array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "PointillizeFilter" -> {
                val filter = PointillizeFilter(array[0] as Float, array[1] as Float, array[4] as Float, array[3] as Float, array[2] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "PosterizeFilter" -> {
                val filter = PosterizeFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "RippleFilter" -> {
                val filter = RippleFilter(array[1] as Float, array[3] as Float, array[2] as Float, array[4] as Float, array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "SepiaToneFilter" -> {
                SepiaToneFilter().transform(state.currentImage!!)
            }
            "SharpenFilter" -> {
                SharpenFilter().transform(state.currentImage!!)
            }
            "SmearFilter" -> {
                val filter = SmearFilter(array[0] as Float, array[1] as Float, array[2] as Int, array[4] as Int, array[3] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "SolarizeFilter" -> {
                SolarizeFilter().transform(state.currentImage!!)
            }
            "SpotlightFilter" -> {
                val filter = SpotlightFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "StrokeAreaFilter" -> {
                val filter = StrokeAreaFilter(array[0] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "SwimFilter" -> {
                val filter = SwimFilter(array[2] as Float,array[3] as Float,array[1] as Float,array[0] as Float,array[5] as Float,array[4] as Float,)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "USMFilter" -> {
                val filter = USMFilter(array[1] as Float,array[0] as Float,array[2] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!.toComposeImageBitmap().toAwtImage())
            }
            "VariableBlurFilter"-> {
                val filter = VariableBlurFilter(array[0] as Int,array[2] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "VignetteFilter"-> {
                val filter = VignetteFilter(array[0] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "WaterFilter" -> {
                val filter = WaterFilter(array[5] as Float, array[0] as Float, array[3] as Float, array[1] as Float, array[2] as Float, array[4] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }
            "WhiteImageFilter" -> {
                val filter = WhiteImageFilter(array[0] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(state.currentImage!!)
            }

            else -> {
                state.currentImage!!
            }
        }
    }
}