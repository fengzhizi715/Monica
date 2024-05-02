package cn.netdiscovery.monica.utils

import java.io.Closeable
import java.io.IOException

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.IOUtils
 * @author: Tony Shen
 * @date: 2024/5/2 21:47
 * @version: V1.0 <描述当前版本功能>
 */
/**
 * 安全关闭io流
 * @param closeable
 */
fun closeQuietly(closeable: Closeable?) {
    if (closeable != null) {
        try {
            closeable.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 安全关闭io流
 * @param closeables
 */
fun closeQuietly(vararg closeables: Closeable?) {
    if (closeables.isNotEmpty()) {
        for (closeable in closeables) {
            closeQuietly(closeable)
        }
    }
}