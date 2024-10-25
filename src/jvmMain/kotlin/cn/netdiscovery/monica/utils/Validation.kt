package cn.netdiscovery.monica.utils

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.Validation
 * @author: Tony Shen
 * @date: 2024/10/25 10:24
 * @version: V1.0 <描述当前版本功能>
 */
fun <T> getValidateField(block:()-> T, failed:()->Unit): T? {

    return try {
        block.invoke()
    } catch (e:Exception) {
        failed.invoke()
        null
    }
}