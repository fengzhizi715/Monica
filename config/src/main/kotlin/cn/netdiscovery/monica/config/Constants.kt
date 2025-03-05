package cn.netdiscovery.monica.config

import java.text.SimpleDateFormat


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.config.Constants
 * @author: Tony Shen
 * @date: 2024/5/7 10:55
 * @version: V1.0 <描述当前版本功能>
 */
val appVersion by lazy {
    Monica.config.BuildConfig.APP_VERSION
}

val kotlinVersion by lazy {
    Monica.config.BuildConfig.KOTLIN_VERSION
}

val composeVersion by lazy {
    Monica.config.BuildConfig.COMPOSE_VERSION
}

val isProVersion by lazy {
    Monica.config.BuildConfig.IS_PRO_VERSION
}

val buildTime:String by lazy {
   val time = Monica.config.BuildConfig.BUILD_TIME

    val dateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    dateformat.format(time)
}

const val previewWidth = 750

const val timeout = 6000
const val retryNum = 3


const val KEY_CROP_FIRST = "key_crop_first"
const val KEY_CROP_SECOND = "key_crop_second"
const val KEY_CROP = "key_crop"
const val KEY_GENERAL_SETTINGS = "key_general_settings"