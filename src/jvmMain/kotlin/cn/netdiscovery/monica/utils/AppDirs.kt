package cn.netdiscovery.monica.utils

import Monica.config.BuildConfig
import cn.netdiscovery.monica.config.*
import java.io.File

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.AppDirs
 * @author: Tony Shen
 * @date:  2025/6/2 16:08
 * @version: V1.0 <描述当前版本功能>
 */
object AppDirs {

    private const val appName = "Monica"

    val cacheDir: File by lazy {
        val path = when {
            isMac -> {
                if (BuildConfig.IS_PRO_VERSION) {
                    "$workDirectory/Library/Caches/$appName/rxcache"
                } else {
                    "$workDirectory/rxcache"
                }
            }

            isLinux -> {
                if (BuildConfig.IS_PRO_VERSION) {
                    "$userHome/.cache/$appName"
                } else {
                    "$workDirectory/rxcache"
                }
            }

            isWindows -> "${getWindowsAppData()}/$appName/Cache"

            else -> "$userHome/.cache/$appName"
        }
        createDir(path)
    }

    val logDir: File by lazy {
        val path = when {
            isMac -> {
                if (BuildConfig.IS_PRO_VERSION) {
                    "$workDirectory/Library/Logs/$appName"
                } else {
                    "$workDirectory/log"
                }
            }

            isLinux -> {
                if (BuildConfig.IS_PRO_VERSION) {
                    "$userHome/.local/share/$appName/logs"
                } else {
                    "$workDirectory/log"
                }
            }

            isWindows -> "${getWindowsAppData()}/$appName/Logs"

            else -> "$userHome/.local/share/$appName/logs"
        }
        createDir(path)
    }

    private fun getWindowsAppData(): String {
        return System.getenv("APPDATA") ?: "$userHome/AppData/Roaming"
    }

    private fun createDir(path: String): File {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }
}