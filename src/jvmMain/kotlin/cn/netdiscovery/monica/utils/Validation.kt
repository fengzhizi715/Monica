package cn.netdiscovery.monica.utils

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.Validation
 * @author: Tony Shen
 * @date: 2024/10/25 10:24
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 对字段进行转换和验证
 * @param block 对字段进行转换
 * @param failed 对字段转换失败的回调
 */
fun <T> getValidateField(block:()-> T,
                         failed:()->Unit): T? {

    return try {
        block.invoke()
    } catch (e:Exception) {
        failed.invoke()
        null
    }
}

/**
 * 对字段进行转换和验证
 * @param block 对字段进行转换
 * @param condition 对字段的值进行校验
 * @param failed 对字段转换失败/校验失败的回调
 */
fun <T> getValidateField(block:()-> T,
                         condition: (T) -> Boolean,
                         failed:()->Unit): T? {

    return try {
        val field = block.invoke()
        if (condition.invoke(field)) {
            field
        } else {
            failed.invoke()
            null
        }
    } catch (e:Exception) {
        failed.invoke()
        null
    }
}