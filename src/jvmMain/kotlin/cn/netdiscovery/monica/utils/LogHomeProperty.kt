package cn.netdiscovery.monica.utils

import ch.qos.logback.core.PropertyDefinerBase
import java.io.File

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.LogHomeProperty
 * @author: Tony Shen
 * @date: 2022/4/21 4:23 下午
 * @version: V1.0 <描述当前版本功能>
 */
class LogHomeProperty : PropertyDefinerBase() {

    override fun getPropertyValue(): String {

        return AppDirs.logDir.absolutePath + File.separator
    }
}