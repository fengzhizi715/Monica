package cn.netdiscovery.monica.config

import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.opencv.ImageProcess
import java.text.SimpleDateFormat
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
    cn.netdiscovery.monica.Monica.BuildConfig.APP_VERSION
}

val kotlinVersion by lazy {
    cn.netdiscovery.monica.Monica.BuildConfig.KOTLIN_VERSION
}

val composeVersion by lazy {
    cn.netdiscovery.monica.Monica.BuildConfig.COMPOSE_VERSION
}

val buildTime by lazy {
   val time = cn.netdiscovery.monica.Monica.BuildConfig.BUILD_TIME

    val dateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    dateformat.format(time)
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