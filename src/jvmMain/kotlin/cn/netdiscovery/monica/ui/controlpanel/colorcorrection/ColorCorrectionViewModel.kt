package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.config.MODULE_COLOR
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.history.EditHistoryCenter
import cn.netdiscovery.monica.history.HistoryEntry
import cn.netdiscovery.monica.history.modules.colorcorrection.ColorCorrectionParams
import cn.netdiscovery.monica.history.modules.colorcorrection.recordColorCorrection
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.utils.extension.image2ByteArray
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.*
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import com.safframework.rxcache.utils.GsonUtils
import org.slf4j.Logger
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ColorCorrectionViewModel
 * @author: Tony Shen
 * @date: 2024/11/5 15:17
 * @version: V1.0 <描述当前版本功能>
 */
class ColorCorrectionViewModel {

    private val logger: Logger = logger<ColorCorrectionViewModel>()
    private val manager = EditHistoryCenter.getManager<ColorCorrectionParams>(MODULE_COLOR)

    var contrast by mutableStateOf(255f )
    var hue by mutableStateOf(180f )
    var saturation by mutableStateOf(255f )
    var lightness by mutableStateOf(255f )
    var temperature by mutableStateOf(255f )
    var highlight by mutableStateOf(255f )
    var shadow by mutableStateOf(255f )
    var sharpen by mutableStateOf(0f )
    var corner by mutableStateOf(0f )

    private var cppObjectPtr:Long = 0

    private var init:AtomicBoolean = AtomicBoolean(false)

    fun updateParams(params: ColorCorrectionSettings) {
        contrast = params.contrast.toFloat()
        hue = params.hue.toFloat()
        saturation = params.saturation.toFloat()
        lightness = params.lightness.toFloat()
        temperature = params.temperature.toFloat()
        highlight = params.highlight.toFloat()
        shadow = params.shadow.toFloat()
        sharpen = params.sharpen.toFloat()
        corner = params.corner.toFloat()
    }

    /**
     * 封装图像调色的方法
     * @param state   当前应用的 state
     * @param image   需要调色的图像
     * @param colorCorrectionSettings 图像调色所需要的参数
     * @param success 成功后的回调
     */
    fun colorCorrection(state: ApplicationState,
                        image: BufferedImage,
                        colorCorrectionSettings: ColorCorrectionSettings,
                        success: CVSuccess) {

        logger.info("colorCorrectionSettings = ${GsonUtils.toJson(colorCorrectionSettings)}")

        state.scope.launchWithSuspendLoading {
            if (!init.get()) {
                init.set(true)

                val byteArray = image.image2ByteArray()
                cppObjectPtr = ImageProcess.initColorCorrection(byteArray)
            }

            OpenCVManager.invokeCV(image,
                action  = { byteArray ->
                    manager.recordColorCorrection(operation = "colorCorrection", colorCorrectionSettings = colorCorrectionSettings)

                    ImageProcess.colorCorrection(byteArray, colorCorrectionSettings, cppObjectPtr)
                },
                success = { success.invoke(it) },
                failure = { e ->
                    logger.error("colorCorrection is failed", e)
                })
        }
    }

    /**
     * 保存图像
     */
    fun save(state: ApplicationState, action: Action) {

        val imageFormat = state.rawImageFormat

        if (imageFormat!=null && imageFormat.isRaw()) {
            state.scope.launchWithLoading {

                if (!state.nativeFullImageProcessed) {
                    val filePath = state.rawImageFile?.absolutePath!!
                    val nativePtr = state.nativeImageInfo?.nativePtr!!

                    // 获取全尺寸的 raw 图像，更新金字塔对象，完成调色返回预览对象
                    val previewImage = ImageProcess.decodeRawAndColorCorrection(filePath, nativePtr, colorCorrectionSettings, cppObjectPtr)
                    if (previewImage!=null) {
                        state.addQueue(state.currentImage!!)
                        val image = BufferedImages.toBufferedImage(previewImage.previewImage, previewImage.width, previewImage.height, BufferedImage.TYPE_INT_ARGB)
                        state.currentImage = image
                        state.nativeFullImageProcessed = true
                        state.togglePreviewWindow(false)
                    }
                } else {
                    val nativePtr = state.nativeImageInfo?.nativePtr!!

                    // 更新金字塔对象，完成调色返回预览对象
                    val previewImage = ImageProcess.colorCorrectionWithPyramidImage(nativePtr, colorCorrectionSettings, cppObjectPtr)
                    if (previewImage!=null) {
                        state.addQueue(state.currentImage!!)
                        val image = BufferedImages.toBufferedImage(previewImage.previewImage, previewImage.width, previewImage.height, BufferedImage.TYPE_INT_ARGB)
                        state.currentImage = image
                        state.togglePreviewWindow(false)
                    }
                }
            }
        } else if (imageFormat!=null && imageFormat == ImageFormat.HEIC) {
            state.scope.launchWithLoading {

                val nativePtr = state.nativeImageInfo?.nativePtr!!

                // 更新金字塔对象，完成调色返回预览对象
                val previewImage = ImageProcess.colorCorrectionWithPyramidImage(nativePtr, colorCorrectionSettings, cppObjectPtr)
                if (previewImage!=null) {
                    state.addQueue(state.currentImage!!)
                    val image = BufferedImages.toBufferedImage(previewImage.previewImage, previewImage.width, previewImage.height, BufferedImage.TYPE_INT_ARGB)
                    state.currentImage = image
                    state.togglePreviewWindow(false)
                }
            }
        } else {
            action.invoke()
        }
    }

    fun undo(block: (ColorCorrectionSettings)-> Unit ) {

        val pair = manager.undo()

        if (pair!=null) {
            val lastSettings = pair.first.toSettings()
            updateParams(lastSettings)
            block.invoke(lastSettings)
        }
    }

    fun clearAllStatus() {
        init.set(false)

        contrast = 255f
        hue = 180f
        saturation = 255f
        lightness = 255f
        temperature = 255f
        highlight = 255f
        shadow = 255f
        sharpen = 0f
        corner = 0f

        colorCorrectionSettings = ColorCorrectionSettings()

        if (cppObjectPtr !=0L ) {
            ImageProcess.deleteColorCorrection(cppObjectPtr)
            cppObjectPtr = 0
        }
    }
}