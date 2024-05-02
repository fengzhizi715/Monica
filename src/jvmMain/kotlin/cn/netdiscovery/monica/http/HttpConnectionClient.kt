package cn.netdiscovery.monica.http

import cn.netdiscovery.monica.utils.extension.openConnection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.HttpURLConnection
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.http.HttpConnectionClient
 * @author: Tony Shen
 * @date:  2024/5/2 15:28
 * @version: V1.0 <描述当前版本功能>
 */
class HttpConnectionClient(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val timeout: Int,
    private val retryNum: Int,
) {
    fun dispatcher(): CoroutineDispatcher = dispatcher

    suspend fun getImage(url: String, ua:String?=null): BufferedImage? {
        var conn: HttpURLConnection? = null

        try {
            var retry = 0
            do {
                conn = url.openConnection(ua,timeout,timeout)
                conn.requestMethod = "GET"

                when (conn.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        println("Response status code is ${conn.responseCode}")
                        break
                    }

                    HttpURLConnection.HTTP_GATEWAY_TIMEOUT ->  {
//                        "gateway timeout".logE()
                        break
                    }

                    HttpURLConnection.HTTP_UNAVAILABLE -> {
//                        "http unavailable".logE()
                        break
                    }
                }

                retry++
            } while (retry < retryNum)

            if (conn?.responseCode != 200) {
                println("Response status code is ${conn?.responseCode}")
                return null
            }

            val contentTypeString = conn.contentType
            if (contentTypeString == null) {
                println("Content-type is null!")
                return null
            }

            val contentLength = conn.contentLength
            if (contentLength <= 0) {
                println("Content length is null!")
                return null
            }

            return ImageIO.read(conn.inputStream)
        } catch (error: Throwable) {
            error.printStackTrace()
            return null
        } finally {
            conn?.disconnect()
        }
    }
}