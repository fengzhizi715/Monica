package cn.netdiscovery.monica.utils

import ch.qos.logback.core.PropertyDefinerBase
import java.io.File
import cn.netdiscovery.monica.config.workDirectory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.LogHomeProperty
 * @author: Tony Shen
 * @date: 2022/4/21 4:23 下午
 * @version: V1.0 <描述当前版本功能>
 */
class LogHomeProperty : PropertyDefinerBase() {

    private val LOG_HOME: String by lazy {
        val dirPath = workDirectory + File.separator + "log"
        val dir = File(dirPath)

        if (!dir.isDirectory) {
            dir.mkdirs()
        }

        dirPath + File.separator
    }

    override fun getPropertyValue(): String {
        return LOG_HOME
    }
}