package cn.netdiscovery.monica.config

import androidx.compose.ui.unit.dp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.config.Constant
 * @author: Tony Shen
 * @date: 2024/5/7 10:55
 * @version: V1.0 <描述当前版本功能>
 */
const val appVersion = "v0.2.6.1"

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