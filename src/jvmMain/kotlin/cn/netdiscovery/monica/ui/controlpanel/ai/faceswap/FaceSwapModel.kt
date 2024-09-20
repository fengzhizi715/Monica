package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.getImageInfo
import cn.netdiscovery.monica.imageprocess.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.logger
import cn.netdiscovery.monica.utils.showFileSelector
import org.slf4j.Logger
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JFileChooser

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapModel
 * @author: Tony Shen
 * @date: 2024/8/25 14:55
 * @version: V1.0 <描述当前版本功能>
 */
class FaceSwapModel {
    private val logger: Logger = logger<FaceSwapModel>()

    var targetImage: BufferedImage? by mutableStateOf(null)
    var lastTargetImage: BufferedImage? by mutableStateOf(null)

    fun clearTargetImage() {
        if (targetImage!=null) {
            targetImage = null
        }

        if (lastTargetImage!=null) {
            lastTargetImage = null
        }
    }

    fun chooseImage(state: ApplicationState, block:(file: File)->Unit) {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                state.scope.launchWithLoading {
                    val file = it.getOrNull(0)
                    if (file != null) {
                        logger.info("load file: ${file.absolutePath}")
                        block.invoke(file)
                    }
                }
            }
        )
    }

    fun faceLandMark(state: ApplicationState, image: BufferedImage?=null, onImageChange:OnImageChange) {

        if (image!=null) {
            state.scope.launchWithLoading {
                OpenCVManager.invokeCV(image,
                    action = { ImageProcess.faceLandMark(it) },
                    success = { onImageChange.invoke(it) },
                    failure = { e->
                        logger.error("faceLandMark is failed", e)
                    })
            }
        }
    }

    fun faceSwap(state: ApplicationState, image: BufferedImage?=null, target: BufferedImage?=null, status:Boolean, onImageChange:OnImageChange) {

        if (image!=null && target!=null) {
            state.scope.launchWithLoading {
                val srcByteArray = image.image2ByteArray()

                OpenCVManager.invokeCV(target,
                    action = { ImageProcess.faceSwap(srcByteArray, it, status) },
                    success = { onImageChange.invoke(it) },
                    failure = { e->
                        logger.error("faceSwap is failed", e)
                    })
            }
        }
    }

    fun getLastSourceImage(state: ApplicationState) {
        state.getLastImage()?.let {
            state.currentImage = it
        }
    }
}