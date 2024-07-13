package cn.netdiscovery.monica.utils


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.SystemUtils
 * @author: Tony Shen
 * @date:  2024/7/6 14:36
 * @version: V1.0 <描述当前版本功能>
 */

val os = System.getProperty("os.name")
val arch = System.getProperty("os.arch")
val osVersion = System.getProperty("os.version")
val javaVersion = System.getProperty("java.version")

val isMac by lazy {
    os.contains("Mac")
}