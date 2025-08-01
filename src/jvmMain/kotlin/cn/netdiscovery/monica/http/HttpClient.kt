package cn.netdiscovery.monica.http

import cn.netdiscovery.http.core.HttpClientBuilder
import cn.netdiscovery.http.core.request.converter.GlobalRequestJSONConverter
import cn.netdiscovery.http.core.response.StringResponseMapper
import cn.netdiscovery.http.core.utils.extension.asyncCall
import cn.netdiscovery.http.interceptor.LoggingInterceptor
import cn.netdiscovery.http.interceptor.log.LogManager
import cn.netdiscovery.http.interceptor.log.LogProxy
import cn.netdiscovery.monica.utils.CVFailure
import cn.netdiscovery.monica.utils.CVSuccess
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.http.HttpClient
 * @author: Tony Shen
 * @date: 2025/3/26 16:45
 * @version: V1.0 <描述当前版本功能>
 */

const val DEFAULT_CONN_TIMEOUT = 30

private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val loggingInterceptor by lazy {
    LogManager.logProxy(object : LogProxy {  // 必须要实现 LogProxy ，否则无法打印网络请求的 request 、response
        override fun e(tag: String, msg: String) {
        }

        override fun w(tag: String, msg: String) {
        }

        override fun i(tag: String, msg: String) {
            logger.info("$tag:$msg")
        }

        override fun d(tag: String, msg: String) {
            logger.info("$tag:$msg")
        }
    })

    LoggingInterceptor.Builder()
        .loggable(true) // TODO: 发布到生产环境需要改成false
        .request()
        .requestTag("Request")
        .response()
        .responseTag("Response")
//        .hideVerticalLine()// 隐藏竖线边框
        .build()
}

val httpClient by lazy {
    HttpClientBuilder()
        .allTimeouts(DEFAULT_CONN_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .serializer(GsonSerializer())
        .jsonConverter(GlobalRequestJSONConverter::class)
        .responseMapper(StringResponseMapper::class)
        .build()
}

fun healthCheck(baseUrl:String):Boolean = httpClient.get(url = "${baseUrl}health").code == 200

/**
 * 封装 RequestBody
 */
fun createRequestBody(image: BufferedImage, format:String): RequestBody {
    return object : RequestBody() {
        override fun contentType(): MediaType? {
            return "image/jpeg".toMediaTypeOrNull()
        }

        override fun writeTo(sink: BufferedSink) {
            // 使用 try-with-resources 确保流关闭
            val outputStream = sink.outputStream()
            outputStream.use {

                if (!ImageIO.write(image, format, it)) {
                    throw IOException("Unsupported image format: $format")
                }
            }
        }
    }
}

/**
 * 封装 http 请求
 */
fun createRequest(request: ()->Request,
                  success: CVSuccess,
                  failure: CVFailure) {

    try {
        httpClient.okHttpClient()
            .asyncCall { request.invoke() }
            .get()
            .use { response->

                val bufferedImage = ByteArrayInputStream(response.body?.bytes()).use { inputStream -> ImageIO.read(inputStream) }
                success.invoke(bufferedImage)
            }
    } catch (e:Exception){
        e.printStackTrace()
        failure.invoke(e)
    }
}