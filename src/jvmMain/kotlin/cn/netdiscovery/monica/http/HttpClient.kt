package cn.netdiscovery.monica.http

import cn.netdiscovery.http.core.HttpClientBuilder
import cn.netdiscovery.http.core.request.converter.GlobalRequestJSONConverter
import cn.netdiscovery.http.core.response.StringResponseMapper
import cn.netdiscovery.http.interceptor.LoggingInterceptor
import cn.netdiscovery.http.interceptor.log.LogManager
import cn.netdiscovery.http.interceptor.log.LogProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


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
//        .baseUrl("http://localhost:8080")
        .allTimeouts(DEFAULT_CONN_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .serializer(GsonSerializer())
        .jsonConverter(GlobalRequestJSONConverter::class)
        .responseMapper(StringResponseMapper::class)
        .build()
}