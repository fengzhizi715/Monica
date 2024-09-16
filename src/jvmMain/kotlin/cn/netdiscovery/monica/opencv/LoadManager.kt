package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.utils.*
import java.io.File

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.opencv.LoadManager
 * @author: Tony Shen
 * @date: 2024/7/16 14:06
 * @version: V1.0 <描述当前版本功能>
 */
object LoadManager {

    val resourcesDir by lazy {
        File(ImageProcess.loadPath)
    }

    /**
     * 对于不同的平台加载的库是不同的，mac 是 dylib 库，windows 是 dll 库，linux 是 so 库
     */
    fun loadMonicaImageProcess() {
        if (isMac) {
            if (arch == "aarch64") { // 即使是 mac 系统，针对不同的芯片 也需要加载不同的 dylib 库
                System.load("${ImageProcess.loadPath}libMonicaImageProcess_aarch64.dylib")
            } else {
                System.load("${ImageProcess.loadPath}libMonicaImageProcess.dylib")
            }
        } else if (isWindows) {
            System.load("${ImageProcess.loadPath}MonicaImageProcess.dll")
        }
    }
}