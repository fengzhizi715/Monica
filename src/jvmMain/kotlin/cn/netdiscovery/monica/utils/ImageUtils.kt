package cn.netdiscovery.monica.utils

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import cn.netdiscovery.monica.exception.MonicaException
import cn.netdiscovery.monica.imageprocess.BufferedImages
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

fun getBufferedImage(file: File, state: ApplicationState): BufferedImage {

    val filePath = file.absolutePath

    val imageFormat = ImageFormatDetector.detectFormat(file)
    logger.info("format: $imageFormat")

    if (imageFormat.isRaw()) {
        try {
            val decodedPreviewImage = ImageProcess.decodeRawToBufferForPreView(filePath)
            if (decodedPreviewImage!=null) {
                state.nativeImageInfo = decodedPreviewImage
                state.rawImageFormat = imageFormat

                val outPixels = decodedPreviewImage.previewImage
                val width = decodedPreviewImage.width
                val height = decodedPreviewImage.height
                val image = BufferedImages.toBufferedImage(outPixels, width, height, BufferedImage.TYPE_INT_ARGB)
                return image
            } else {
                throw MonicaException("Image format is not supported")
            }
        } catch (e:Exception) {
            logger.error("decode raw image failed", e)
            throw MonicaException("decode raw image failed")
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
                try {
                    val decodedPreviewImage = ImageProcess.decodeHeif(filePath)
                    if (decodedPreviewImage!=null) {
                        state.nativeImageInfo = decodedPreviewImage
                        state.rawImageFormat = imageFormat

                        val outPixels = decodedPreviewImage.previewImage
                        val width = decodedPreviewImage.width
                        val height = decodedPreviewImage.height
                        val image = BufferedImages.toBufferedImage(outPixels, width, height, BufferedImage.TYPE_INT_ARGB)
                        return image
                    }  else {
                        throw MonicaException("Image format is not supported")
                    }
                } catch (e: Exception) {
                    logger.error("decode heif image failed", e)
                    throw MonicaException("decode heif image failed")
                }
            }
            else -> throw MonicaException("Unsupported image format: $imageFormat")
        }
    }
}

fun getBufferedImage(file: File): BufferedImage {

    val filePath = file.absolutePath

    val imageFormat = ImageFormatDetector.detectFormat(file)
    logger.info("format: $imageFormat")

    if (imageFormat.isRaw()) {
        val decodedPreviewImage = ImageProcess.decodeRawToBufferForPreView(filePath)
        if (decodedPreviewImage!=null) {
            val outPixels = decodedPreviewImage.previewImage
            val width = decodedPreviewImage.width
            val height = decodedPreviewImage.height
            val image = BufferedImages.toBufferedImage(outPixels,width,height,BufferedImage.TYPE_INT_ARGB)
            return image
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
                val decodedPreviewImage = ImageProcess.decodeHeif(filePath)
                if (decodedPreviewImage!=null) {
                    val outPixels = decodedPreviewImage.previewImage
                    val width = decodedPreviewImage.width
                    val height = decodedPreviewImage.height
                    val image = BufferedImages.toBufferedImage(outPixels, width, height, BufferedImage.TYPE_INT_ARGB)
                    return image
                }  else {
                    throw MonicaException("Image format is not supported")
                }
            }
            else -> throw MonicaException("Unsupported image format: $imageFormat")
        }
    }
}

suspend fun doFilter(
    filterName: String,
    array: MutableList<Any>,
    image: BufferedImage
): BufferedImage {

    return withContext(IO) {
        when(filterName) {
            "AverageFilter" -> {
                AverageFilter().transform(image)
            }
            "BilateralFilter" -> {
                val filter = BilateralFilter(array[0] as Double, array[1] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "BlockFilter" -> {
                val filter = BlockFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "BoxBlurFilter" -> {
                val filter = BoxBlurFilter(array[0] as Int,array[2] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "BumpFilter" -> {
                BumpFilter().transform(image)
            }
            "CarveFilter" -> {
                val filter = CarveFilter()
                filter.transform(image)
            }
            "ColorFilter" -> {
                val filter = ColorFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "ColorHalftoneFilter" -> {
                val filter = ColorHalftoneFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "ConBriFilter" -> {
                val filter = ConBriFilter(array[1] as Float,array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "CropFilter" -> {
                val filter = CropFilter(array[2] as Int,array[3] as Int,array[1] as Int,array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "CrystallizeFilter" -> {
                val filter = CrystallizeFilter(array[0] as Float, array[3] as Float, array[2] as Float, array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "DiffuseFilter" -> {
                val filter = DiffuseFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "EmbossFilter" -> {
                val filter = EmbossFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "EqualizeFilter" -> {
                val filter = EqualizeFilter()
                filter.transform(image)
            }
            "ExposureFilter" -> {
                val filter = ExposureFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "GainFilter" -> {
                val filter = GainFilter(array[1] as Float, array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "GammaFilter" -> {
                val filter = GammaFilter(array[0] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "FastBlur2D" -> {
                FastBlur2D(array[0] as Int).transform(image)
            }
            "GaussianFilter" -> {
                val filter = GaussianFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "GaussianNoiseFilter" -> {
                val filter = GaussianNoiseFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "GradientFilter" -> {
                GradientFilter().transform(image)
            }
            "GrayFilter" -> {
                GrayFilter().transform(image)
            }
            "HighPassFilter" -> {
                val filter = HighPassFilter(array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "HSBAdjustFilter" -> {
                val filter = HSBAdjustFilter(array[1] as Float, array[2] as Float, array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "InvertFilter" -> {
                InvertFilter().transform(image)
            }
            "LaplaceSharpenFilter" -> {
                LaplaceSharpenFilter().transform(image)
            }
            "LensBlurFilter" -> {
                val filter = LensBlurFilter(array[3] as Float,array[1] as Float,array[2] as Float,array[0] as Float,array[4] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "MarbleFilter" -> {
                val filter = MarbleFilter(array[1] as Float,array[2] as Float,array[0] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "MaximumFilter" -> {
                val filter = MaximumFilter()
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "MinimumFilter" -> {
                val filter = MinimumFilter()
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "MirrorFilter" -> {
                val filter = MirrorFilter(array[2] as Float,array[0] as Float,array[1] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "MosaicFilter" -> {
                val filter = MosaicFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "MotionFilter" -> {
                val filter = MotionFilter(array[1] as Float,array[0] as Float,array[2] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "NatureFilter" -> {
                val filter = NatureFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "OffsetFilter" -> {
                val filter = OffsetFilter(array[0] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "OilPaintFilter" -> {
                val filter = OilPaintFilter(array[1] as Int,array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "PointillizeFilter" -> {
                val filter = PointillizeFilter(array[0] as Float, array[1] as Float, array[4] as Float, array[3] as Float, array[2] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "PosterizeFilter" -> {
                val filter = PosterizeFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "RippleFilter" -> {
                val filter = RippleFilter(array[1] as Float, array[3] as Float, array[2] as Float, array[4] as Float, array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "SepiaToneFilter" -> {
                SepiaToneFilter().transform(image)
            }
            "SharpenFilter" -> {
                SharpenFilter().transform(image)
            }
            "SmearFilter" -> {
                val filter = SmearFilter(array[0] as Float, array[1] as Float, array[2] as Int, array[4] as Int, array[3] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "SolarizeFilter" -> {
                SolarizeFilter().transform(image)
            }
            "SpotlightFilter" -> {
                val filter = SpotlightFilter(array[0] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "StrokeAreaFilter" -> {
                val filter = StrokeAreaFilter(array[0] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "SwimFilter" -> {
                val filter = SwimFilter(array[2] as Float,array[3] as Float,array[1] as Float,array[0] as Float,array[5] as Float,array[4] as Float,)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "USMFilter" -> {
                val filter = USMFilter(array[1] as Float,array[0] as Float,array[2] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image.toComposeImageBitmap().toAwtImage())
            }
            "VariableBlurFilter"-> {
                val filter = VariableBlurFilter(array[0] as Int,array[2] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "VignetteFilter"-> {
                val filter = VignetteFilter(array[0] as Int,array[1] as Int)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "WaterFilter" -> {
                val filter = WaterFilter(array[5] as Float, array[0] as Float, array[3] as Float, array[1] as Float, array[2] as Float, array[4] as Float)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }
            "WhiteImageFilter" -> {
                val filter = WhiteImageFilter(array[0] as Double)
                filter.printConstructorParamsWithValues()
                filter.transform(image)
            }

            else -> {
                image
            }
        }
    }
}