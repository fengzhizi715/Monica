package cn.netdiscovery.monica.config

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.opencv.ImageProcess

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.config.Constant
 * @author: Tony Shen
 * @date: 2024/5/7 10:55
 * @version: V1.0 <描述当前版本功能>
 */

val imageProcessVersion by lazy { // 本地算法库的版本
    ImageProcess.getVersion()
}

val openCVVersion by lazy { // OpenCV 的版本
    ImageProcess.getOpenCVVersion()
}

val onnxRuntimeVersion by lazy { // ONNXRuntime 的版本
    ImageProcess.getONNXRuntimeVersion()
}

val width = (previewWidth * 2.toFloat()).dp
val height = 1000.dp
val loadingWidth = (previewWidth*2*0.7).dp

val titleTextSize = 32.sp
val subTitleTextSize = 20.sp