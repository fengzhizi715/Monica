package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.http.core.utils.extension.asyncCall
import cn.netdiscovery.monica.http.httpClient
import cn.netdiscovery.monica.imageprocess.utils.extension.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.CVSuccess
import cn.netdiscovery.monica.utils.extensions.getImageFormat
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import cn.netdiscovery.monica.utils.logger
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import org.slf4j.Logger
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapModel
 * @author: Tony Shen
 * @date: 2024/8/25 14:55
 * @version: V1.0 <描述当前版本功能>
 */
class FaceSwapViewModel {
    private val logger: Logger = logger<FaceSwapViewModel>()

    var targetImage: BufferedImage? by mutableStateOf(null)
    var targetImageFile: File? = null
    var lastTargetImage: BufferedImage? by mutableStateOf(null)

    fun clearTargetImage() {
        if (targetImage!=null) {
            targetImage = null
        }

        if (lastTargetImage!=null) {
            lastTargetImage = null
        }
    }

    fun faceLandMark(state: ApplicationState, image: BufferedImage?=null, file: File?=null, success:CVSuccess) {

//        if (image!=null) {
//            state.scope.launchWithLoading {
//                OpenCVManager.invokeCV(image,
//                    action = {
//                        val scalar = state.toOutputBoxScalar()
//                        ImageProcess.faceLandMark(it, scalar)
//                    },
//                    success = { success.invoke(it) },
//                    failure = { e->
//                        logger.error("faceLandMark is failed", e)
//                    })
//            }
//        }

        if (image == null || file == null) return

        state.scope.launchWithSuspendLoading {
            val format = file.getImageFormat()

            val requestBody: RequestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return "image/jpeg".toMediaTypeOrNull()
                }

                override fun writeTo(sink: BufferedSink) {
                    // 使用 try-with-resources 确保流关闭
                    val outputStream = sink.outputStream()
                    outputStream.use { outputStream ->

                        if (!ImageIO.write(image, format, outputStream)) {
                            throw IOException("Unsupported image format: $format")
                        }
                    }
                }
            }

            val request: Request = Request.Builder()
                .url( "${state.algorithmUrlText}api/faceLandMark")
                .post(requestBody)
                .build()

            try {
                httpClient.okHttpClient().asyncCall { request }.get().use { response->
                    val bufferedImage = ByteArrayInputStream(response.body?.bytes()).use { inputStream ->
                        ImageIO.read(inputStream)
                    }

                    success.invoke(bufferedImage)
                }
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun faceSwap(state: ApplicationState, image: BufferedImage?=null, target: BufferedImage?=null, status:Boolean, success:CVSuccess) {

        if (image!=null && target!=null) {
            state.scope.launchWithLoading {
                val srcByteArray = image.image2ByteArray()

                OpenCVManager.invokeCV(target,
                    action = { ImageProcess.faceSwap(srcByteArray, it, status) },
                    success = { success.invoke(it) },
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