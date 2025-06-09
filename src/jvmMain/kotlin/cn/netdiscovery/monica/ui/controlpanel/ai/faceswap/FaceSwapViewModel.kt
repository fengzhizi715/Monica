package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.netdiscovery.monica.http.createRequest
import cn.netdiscovery.monica.http.createRequestBody
import cn.netdiscovery.monica.imageprocess.utils.writeImageFile
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.CVFailure
import cn.netdiscovery.monica.utils.CVSuccess
import cn.netdiscovery.monica.utils.ImageFormatDetector
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import cn.netdiscovery.monica.utils.logger
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.slf4j.Logger
import java.awt.image.BufferedImage
import java.io.File

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

    fun faceLandMark(state: ApplicationState, image: BufferedImage?=null, file: File?=null,
                     success:CVSuccess,
                     failure:CVFailure) {

        if (image == null || file == null) return

        state.scope.launchWithSuspendLoading {
            createRequest(request = {
                val format = ImageFormatDetector.getImageFormat(state.rawImageFile!!)?:"jpg"

                val requestBody: RequestBody = createRequestBody(state.currentImage!!,format)

                Request.Builder()
                    .url( "${state.algorithmUrlText}api/faceLandMark")
                    .post(requestBody)
                    .build()
            }, success = {
                success.invoke(it)
            }, failure = {
                logger.error(it.message)
                failure.invoke(it)
            })
        }
    }

    fun faceSwap(state: ApplicationState, image: BufferedImage?=null, target: BufferedImage?=null, status:Boolean,
                 success:CVSuccess,
                 failure:CVFailure) {

        if (image == null || target == null) return

        state.scope.launchWithSuspendLoading {
            val srcFileName = "temp_src.jpg"
            val targetFileName = "temp_target.jpg"
            writeImageFile(image,srcFileName,"jpg")
            writeImageFile(target,targetFileName,"jpg")

            val srcFile = File(srcFileName)
            val targetFile = File(targetFileName)

            createRequest(request = {
                // 构建 multipart 请求体
                val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("src", srcFileName, srcFile.asRequestBody("image/jpeg".toMediaType()))
                .addFormDataPart("target", targetFileName, targetFile.asRequestBody("image/jpeg".toMediaType()))
                .build()

                Request.Builder()
                    .url("${state.algorithmUrlText}api/faceSwap?status=$status")
                    .post(requestBody)
                    .build()
            }, success = {
                success.invoke(it)
                srcFile.delete()
                targetFile.delete()
            }, failure = {
                logger.error(it.message)
                failure.invoke(it)
            })
        }
    }

    fun getLastSourceImage(state: ApplicationState) {
        state.getLastImage()?.let {
            state.currentImage = it
        }
    }
}