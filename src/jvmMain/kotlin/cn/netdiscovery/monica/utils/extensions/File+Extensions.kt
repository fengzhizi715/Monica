package cn.netdiscovery.monica.utils.extensions

import java.io.File

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extensions.`File+Extension`
 * @author: Tony Shen
 * @date:  2025/2/28 23:03
 * @version: V1.0 <描述当前版本功能>
 */

fun File.getUniqueFile(sourceFile: File = File("")): File {
    var newFile = this

    if (newFile.isDirectory) {
        newFile = File(newFile, sourceFile.name)
    }

    var index = 1
    while (newFile.exists()) {
        newFile = File(newFile.parentFile, "${newFile.nameWithoutExtension}($index).${newFile.extension}")
        index++
    }

    return newFile
}