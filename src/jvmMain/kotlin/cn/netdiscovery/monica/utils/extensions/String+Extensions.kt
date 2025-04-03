package cn.netdiscovery.monica.utils.extensions

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extensions.`String+Extension`
 * @author: Tony Shen
 * @date:  2024/5/2 15:30
 * @version: V1.0 <描述当前版本功能>
 */
import java.net.URL

/**
 * 将 string 字符串安全地转换成 int 类型
 */
fun String.safelyConvertToInt(): Int? = this.toDoubleOrNull()?.takeIf { it % 1 == 0.0 }?.toInt()


fun String.isValidUrl(): Boolean {
    return try {
        URL(this).toURI()
        true
    } catch (e: Exception) {
        false
    }
}
