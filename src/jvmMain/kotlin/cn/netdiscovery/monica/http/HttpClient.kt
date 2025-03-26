package cn.netdiscovery.monica.http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.HttpTimeout

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.http.HttpClient
 * @author: Tony Shen
 * @date: 2025/3/26 16:45
 * @version: V1.0 <描述当前版本功能>
 */
val httpClient by lazy {

    HttpClient(CIO) {
        install(ContentNegotiation) // 安装内容协商插件（可选）

        install(Logging) { // 安装日志插件
            level = LogLevel.HEADERS
        }
        install(HttpTimeout) { // 设置超时
            requestTimeoutMillis = 30000
        }
    }
}