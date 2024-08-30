package cn.netdiscovery.monica.config

import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.opencv.ImageProcess
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.config.Constant
 * @author: Tony Shen
 * @date: 2024/5/7 10:55
 * @version: V1.0 <描述当前版本功能>
 */
val appVersion by lazy {
    val dir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "jvmMain" + File.separator + "resources"

    try {
        val properties = Properties().apply {
            load(FileInputStream(File(dir, "config.properties")))
        }

        "v"+properties.getProperty("app_version")
    } catch (e:Exception) {
        "v1.0.0"
    }
}

val imageProcessVersion by lazy {
    ImageProcess.getVersion()
}

val openCVVersion by lazy {
    ImageProcess.getOpenCVVersion()
}

val onnxRuntimeVersion by lazy {
    ImageProcess.getONNXRuntimeVersion()
}

const val previewWidth = 750

val width = (previewWidth * 2.toFloat()).dp
val height = 1000.dp
val loadingWidth = (previewWidth*2*0.7).dp

const val timeout = 6000
const val retryNum = 3


const val KEY_CROP_FIRST = "key_crop_first"
const val KEY_CROP_SECOND = "key_crop_second"
const val KEY_CROP = "key_crop"
const val KEY_FILTER_REMARK = "key_filter_remark_"