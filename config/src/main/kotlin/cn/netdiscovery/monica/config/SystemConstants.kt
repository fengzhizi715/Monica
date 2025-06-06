package cn.netdiscovery.monica.config


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.SystemUtils
 * @author: Tony Shen
 * @date:  2024/7/6 14:36
 * @version: V1.0 <描述当前版本功能>
 */

val os: String = System.getProperty("os.name")
val arch: String = System.getProperty("os.arch")
val osVersion: String = System.getProperty("os.version")
val javaVersion: String = System.getProperty("java.version")
val javaVendor: String = System.getProperty("java.vendor")
val workDirectory: String = System.getProperty("user.dir")
val userHome: String = System.getProperty("user.home")

val isMac by lazy {
    os.contains("Mac")
}

val isWindows by lazy {
    os.startsWith("Win")
}

val isLinux by lazy {
    os.contains("nux") || os.contains("nix")
}