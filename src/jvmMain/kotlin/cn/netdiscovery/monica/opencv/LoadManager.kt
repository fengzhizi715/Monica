package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.utils.*
import org.slf4j.Logger
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
    private val logger: Logger = logger<LoadManager>()

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

//    /**
//     * 拷贝各个平台所必须的图像处理库
//     */
//    fun copyMonicaImageProcess() {
//        if (isMac) {
//            if (arch == "aarch64") { // 即使是 mac 系统，针对不同的芯片 也需要加载不同的 dylib 库
//                copyLibrary(ImageProcess.loadPath,"libMonicaImageProcess_aarch64.dylib")
//            } else {
//                copyLibrary(ImageProcess.loadPath,"libMonicaImageProcess.dylib")
//            }
//        } else if (isWindows) {
//            copyLibrary(ImageProcess.loadPath,"MonicaImageProcess.dll")
//            copyLibrary(ImageProcess.loadPath,"opencv_world481.dll")
//        }
//    }
//
//    /**
//     * 拷贝人脸检测模块所需要的模型
//     */
//    fun copyFaceDetectModels() = runBlocking {
//        listOf(
//            asyncInBackground {
//                copyLibrary(loadPath, "age_deploy.prototxt")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "age_net.caffemodel")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "gender_deploy.prototxt")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "gender_net.caffemodel")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "opencv_face_detector.pbtxt")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "opencv_face_detector_uint8.pb")
//            }
//        ).awaitAll()
//    }
//
//    /**
//     * 拷贝生成素描画的模型
//     */
//    fun copySketchDrawingModel() {
//        copyLibrary(loadPath, "opensketch_style_512x512.onnx")
//    }
//
//    /**
//     * 拷贝换脸的模型
//     */
//    fun copyFaceSwapModel() = runBlocking {
//        listOf(
//            asyncInBackground {
//                copyLibrary(loadPath, "yoloface_8n.onnx")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "2dfan4.onnx")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "model_matrix.bin")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "arcface_w600k_r50.onnx")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "inswapper_128.onnx")
//            },
//            asyncInBackground {
//                copyLibrary(loadPath, "gfpgan_1.4.onnx")
//            }
//        ).awaitAll()
//    }
//
//    private fun copyLibrary(loadPath:String, libName: String) {
//        try {
//            val resource = this.javaClass.classLoader.getResource(libName)
//
//            resource?.apply {
//                val dir = File(loadPath + libName)
//
//                val inputStream = resource.openStream()
//
//                logger.info("file compare: ${inputStream.available()} / ${dir.length()}")
//                logger.info("copyPath: $dir")
//                if (dir.parentFile != null && !dir.parentFile.exists()) {
//                    dir.parentFile.mkdirs()
//                }
//                val out = FileOutputStream(dir)
//                var i: Int
//                val buf = ByteArray(10240)
//
//                try {
//                    while (inputStream.read(buf).also { i = it } != -1) {
//                        out.write(buf, 0, i)
//                    }
//                } finally {
//                    closeQuietly(inputStream,out)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            logger.info("load jni error: ${e.message}")
//        }
//    }
}