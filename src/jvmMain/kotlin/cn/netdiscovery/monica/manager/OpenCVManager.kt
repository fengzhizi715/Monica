package cn.netdiscovery.monica.manager

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.utils.extension.toImageInfo
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.CVAction
import cn.netdiscovery.monica.utils.CVFailure
import cn.netdiscovery.monica.utils.CVSuccess
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume
import java.awt.image.BufferedImage


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.manager.OpenCVManager
 * @author: Tony Shen
 * @date: 2024/8/13 19:54
 * @version: V1.0
 */
object OpenCVManager {

    /**
     * 封装调用 OpenCV 的方法
     * 便于"当前的图像"进行调用 OpenCV 的方法，以及对返回的 IntArray 进行处理返回成 BufferedImage
     *
     * @param state   当前应用的 state
     * @param type    生成图像的类型
     * @param action  通过 jni 调用 OpenCV 的方法
     * @param failure 失败的回调
     */
    fun invokeCV(state: ApplicationState,
                 type:Int = BufferedImage.TYPE_INT_ARGB,
                 action: CVAction,
                 failure: CVFailure) {

        if (state.currentImage!=null) {
            val (width,height,byteArray) = state.currentImage!!.toImageInfo()

            try {
                val outPixels = action.invoke(byteArray)
                state.addQueue(state.currentImage!!)
                state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height,type)
            } catch (e:Exception) {
                failure.invoke(e)
            }
        }
    }

    /**
     * 封装调用 OpenCV 的方法
     * 便于对某个图像调用 OpenCV 的方法，以及对返回的 IntArray 进行处理返回成 BufferedImage
     *
     * @param image   对该图片进行处理
     * @param type    生成图像的类型
     * @param action  通过 jni 调用 OpenCV 的方法
     * @param success 成功的回调
     * @param failure 失败的回调
     */
    fun invokeCV(image: BufferedImage,
                 type:Int = BufferedImage.TYPE_INT_ARGB,
                 action: CVAction,
                 success: CVSuccess,
                 failure: CVFailure) {
        val (width,height,byteArray) = image.toImageInfo()

        try {
            val outPixels = action.invoke(byteArray)
            success.invoke(BufferedImages.toBufferedImage(outPixels,width,height,type))
        } catch (e:Exception) {
            failure.invoke(e)
        }
    }

    suspend fun invokeCVSuspend(
        image: BufferedImage,
        type: Int = BufferedImage.TYPE_INT_ARGB,
        action: CVAction
    ): BufferedImage = suspendCancellableCoroutine { cont ->
        invokeCV(
            image = image,
            type = type,
            action = action,
            success = { result ->
                if (cont.isActive) cont.resume(result)
            },
            failure = { e ->
                if (cont.isActive) cont.resumeWithException(e)
            }
        )
    }
}