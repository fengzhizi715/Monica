package cn.netdiscovery.monica.opencv

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
    val loadPath = File("").absolutePath + File.separator

    fun copy() {
        println("loadPath: $loadPath")
        copyLibrary("libMonicaImageProcess.dylib")
    }

    private fun copyLibrary(libName: String) {

        try {

            val resource = this.javaClass.classLoader.getResource(libName)

            resource?.apply {

                val dir = File(loadPath + libName)

                val inputStream = resource.openStream()

                println("file compare: ${inputStream.available()} / ${dir.length()}")

//                if (inputStream.available().toLong() == dir.length()) return

                println("copyPath: $dir")
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
                    inputStream.close()
                    out.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("load jni error: ${e.message}")
        }
    }
}