package cn.netdiscovery.monica.ui.controlpanel.ai

import cn.netdiscovery.http.core.utils.extension.asyncCall
import cn.netdiscovery.monica.http.httpClient
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extensions.launchWithLoading
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import cn.netdiscovery.monica.utils.logger
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import org.slf4j.Logger
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.AIViewModel
 * @author: Tony Shen
 * @date: 2024/7/28 11:21
 * @version: V1.0 <描述当前版本功能>
 */
class AIViewModel {
    private val logger: Logger = logger<AIViewModel>()

    fun faceDetect(state: ApplicationState) {
//        state.scope.launchWithLoading {
//
//            OpenCVManager.invokeCV(state, action = { byteArray ->
//                ImageProcess.faceDetect(byteArray)
//            }, failure = { e ->
//                logger.error("faceDetect is failed", e)
//            })
//        }

        if (state.currentImage == null) return

        state.scope.launchWithSuspendLoading {
            val format = state.rawImageFile!!.extension

            val requestBody: RequestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return "image/jpeg".toMediaTypeOrNull()
                }

                override fun writeTo(sink: BufferedSink) {
                    // 使用 try-with-resources 确保流关闭
                    val outputStream = sink.outputStream()
                    outputStream.use { outputStream ->

                        if (!ImageIO.write(state.currentImage, format, outputStream)) {
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
                    state.addQueue(state.currentImage!!)
                    state.currentImage = ByteArrayInputStream(response.body?.bytes()).use { inputStream ->
                        ImageIO.read(inputStream)
                    }
                }
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun sketchDrawing(state: ApplicationState) {
//        state.scope.launchWithLoading {
//
//            OpenCVManager.invokeCV(state, type = BufferedImage.TYPE_BYTE_GRAY, action = { byteArray ->
//                ImageProcess.sketchDrawing(byteArray)
//            }, failure = { e ->
//                logger.error("sketchDrawing is failed", e)
//            })
//        }

        if (state.currentImage == null) return

        state.scope.launchWithSuspendLoading {
            val format = state.rawImageFile!!.extension

            val requestBody: RequestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return "image/jpeg".toMediaTypeOrNull()
                }

                override fun writeTo(sink: BufferedSink) {
                    // 使用 try-with-resources 确保流关闭
                    val outputStream = sink.outputStream()
                    outputStream.use { outputStream ->

                        if (!ImageIO.write(state.currentImage, format, outputStream)) {
                            throw IOException("Unsupported image format: $format")
                        }
                    }
                }
            }

            val request: Request = Request.Builder()
                .url( "${state.algorithmUrlText}api/sketchDrawing")
                .post(requestBody)
                .build()

            try {
                httpClient.okHttpClient().asyncCall { request }.get().use { response->
                    state.addQueue(state.currentImage!!)
                    state.currentImage = ByteArrayInputStream(response.body?.bytes()).use { inputStream ->
                        ImageIO.read(inputStream)
                    }
                }
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}