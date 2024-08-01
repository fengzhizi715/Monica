package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.utils.*
import org.slf4j.Logger
import java.io.File
import java.io.FileOutputStream

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.opencv.FileUtil
 * @author: Tony Shen
 * @date: 2024/7/16 14:06
 * @version: V1.0 <描述当前版本功能>
 */
object FileUtil {
    private val logger: Logger = logger<FileUtil>()

    val loadPath = File("").absolutePath + File.separator + "resources" + File.separator + if (isMac) ("macos" + File.separator) else if(isWindows) ("windows" + File.separator) else ("linux" + File.separator)

    fun copy() {
        logger.info("loadPath: $loadPath")

        if (isMac) {
            if (arch == "aarch64") { // 即使是 mac 系统，针对不同的芯片 也需要加载不同的 dylib 库
                copyLibrary("libMonicaImageProcess_aarch64.dylib")
            } else {
                copyLibrary("libMonicaImageProcess.dylib")
            }
        } else if (isWindows) {
            copyLibrary("MonicaImageProcess.dll")
            copyLibrary("opencv_world481.dll")
        }
    }

    fun copyFaceDetectModels() {
        copyLibrary("age_deploy.prototxt")
        copyLibrary("age_net.caffemodel")
        copyLibrary("gender_deploy.prototxt")
        copyLibrary("gender_net.caffemodel")
        copyLibrary("opencv_face_detector.pbtxt")
        copyLibrary("opencv_face_detector_uint8.pb")
    }

    private fun copyLibrary(libName: String) {
        try {
            val resource = this.javaClass.classLoader.getResource(libName)

            resource?.apply {

                val dir = File(loadPath + libName)

                val inputStream = resource.openStream()

                logger.info("file compare: ${inputStream.available()} / ${dir.length()}")

//                if (inputStream.available().toLong() == dir.length()) return

                logger.info("copyPath: $dir")
                if (dir.parentFile != null && !dir.parentFile.exists()) {
                    dir.parentFile.mkdirs()
                }
                val out = FileOutputStream(dir) //缓存dll位置
                var i: Int
                val buf = ByteArray(1024)

                try {
                    while (inputStream.read(buf).also { i = it } != -1) {
                        out.write(buf, 0, i)
                    }
                } finally {
                    closeQuietly(inputStream,out)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logger.info("load jni error: ${e.message}")
        }
    }
}