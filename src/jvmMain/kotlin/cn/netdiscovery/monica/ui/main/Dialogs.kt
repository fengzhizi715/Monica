package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.http.healthCheck
import cn.netdiscovery.monica.i18n.Language
import cn.netdiscovery.monica.i18n.LocalizationManager
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.rxcache.clearData
import cn.netdiscovery.monica.rxcache.initFilterParamsConfig
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.confirmButton
import cn.netdiscovery.monica.utils.Action
import cn.netdiscovery.monica.utils.extensions.isValidUrl
import cn.netdiscovery.monica.utils.getValidateField
import showTopToast
import picUrl

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.main.Dialogs
 * @author: Tony Shen
 * @date: 2025/4/17 11:57
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 加载网络图片的对话框
 */
@Composable
fun openURLDialog(onConfirm: Action, onDismiss: Action) {
    val i18nState = rememberI18nState()
    
    AlertDialog(
        modifier = Modifier.width(600.dp).height(200.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(text = i18nState.getString("load_network_image_dialog"))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = picUrl,
                    onValueChange = { picUrl = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm.invoke()
                }
            ) {
                Text(i18nState.getString("confirm"))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss.invoke()
                }
            ) {
                Text(i18nState.getString("cancel"))
            }
        }
    )
}

@Composable
fun showVersionInfo(onClick: Action) {
    val i18nState = rememberI18nState()
    
    AlertDialog(onDismissRequest = {},
        title = {
            Text(i18nState.getString("monica_software_info"))
        },
        text = {
            Column {
                val versionInfo = if (isProVersion) "正式版本" else "测试版本"
                Text(i18nState.getString("monica_version_info", appVersion, versionInfo, buildTime))
                Text("OS: $os, $osVersion, $arch")
                Text("JDK: $javaVersion, $javaVendor")
                Text("Kotlin: $kotlinVersion, Compose Desktop: $composeVersion")
                Text(i18nState.getString("opencv_version_info", openCVVersion, imageProcessVersion))
                Text(i18nState.getString("copyright_info"))
                Text("Wechat: fengzhizi715")
                Text(i18nState.getString("github_url"))
            }
        },
        confirmButton = {
            Button(onClick = {
                onClick.invoke()
            }) {
                Text(i18nState.getString("close"))
            }
        })
}

@Composable
fun generalSettings(state: ApplicationState, onClick: Action) {
    val i18nState = rememberI18nState()

    var rText by remember { mutableStateOf(state.outputBoxRText.toString()) }
    var gText by remember { mutableStateOf(state.outputBoxGText.toString()) }
    var bText by remember { mutableStateOf(state.outputBoxBText.toString()) }
    var sizeText by remember { mutableStateOf(state.sizeText.toString()) }
    var maxHistorySizeText by remember { mutableStateOf(state.maxHistorySizeText.toString()) }
    var deepSeekApiKeyText by remember { mutableStateOf(state.deepSeekApiKeyText) }
    var algorithmUrlText by remember { mutableStateOf(state.algorithmUrlText) }
    var status by remember { mutableStateOf(-1) }
    var isInitFilterParams by mutableStateOf(false)
    var isClearCacheData by mutableStateOf(false)

    AlertDialog(onDismissRequest = {},
        title = {
            Text(i18nState.getString("monica_general_settings"), modifier = Modifier.padding(start = 12.dp), fontSize = subTitleTextSize, color = Color.Black)
        },
        text = {
            Column {
                Row(modifier = Modifier.padding(start = 12.dp)) {
                    Text(i18nState.getString("output_box_color_settings"))

                    basicTextFieldWithTitle(textModifier = Modifier.padding(start = 20.dp), modifier = Modifier.padding(top = 5.dp), titleText = "R", value = rText, width = 80.dp) { str ->
                        rText = str
                    }

                    basicTextFieldWithTitle(titleText = "G",  modifier = Modifier.padding(top = 5.dp), value = gText, width = 80.dp) { str ->
                        gText = str
                    }

                    basicTextFieldWithTitle(titleText = "B", modifier = Modifier.padding(top = 5.dp), value = bText, width = 80.dp) { str ->
                        bText = str
                    }
                }

                Row(modifier = Modifier.padding(top = 10.dp, start = 12.dp)) {
                    basicTextFieldWithTitle(titleText = i18nState.getString("area_size_settings"), modifier = Modifier.padding(top = 3.dp), value = sizeText, width = 80.dp) { str ->
                        sizeText = str
                    }
                }

                Row(modifier = Modifier.padding(top = 10.dp, start = 12.dp)) {
                    basicTextFieldWithTitle(titleText = i18nState.getString("max_history_size"), modifier = Modifier.padding(top = 3.dp), value = maxHistorySizeText, width = 80.dp) { str ->
                        maxHistorySizeText = str
                    }
                }

                Row(modifier = Modifier.padding(top = 10.dp, start = 12.dp)) {
                    basicTextFieldWithTitle(titleText = "deepseek: api key", modifier = Modifier.padding(top = 3.dp), value = deepSeekApiKeyText, width = 400.dp) { str ->
                        deepSeekApiKeyText = str
                    }
                }

                Column(modifier = Modifier.padding(top = 10.dp, start = 12.dp), horizontalAlignment = Alignment.Start) {
                    basicTextFieldWithTitle(titleText = i18nState.getString("algorithm_service_url"), value = algorithmUrlText, width = 400.dp) { str ->
                        algorithmUrlText = str
                    }

                    confirmButton(enabled = algorithmUrlText.isNotEmpty(), "检测算法服务状态") {
                        status = try {
                            val baseUrl = if (algorithmUrlText.last() == '/') {
                                algorithmUrlText
                            } else {
                                "$algorithmUrlText/"
                            }

                            if (healthCheck(baseUrl)) {
                                STATUS_HTTP_SERVER_OK
                            } else {
                                STATUS_HTTP_SERVER_FAILED
                            }
                        } catch (e:Exception) {
                            STATUS_HTTP_SERVER_FAILED
                        }
                    }

                    if (status == STATUS_HTTP_SERVER_OK) {
                        Text("算法服务可用", modifier = Modifier.padding(start = 10.dp))
                    } else if (status == 0) {
                        Text("算法服务不可用", modifier = Modifier.padding(start = 10.dp), color = Color.Red)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(isInitFilterParams, onCheckedChange = {
                        isInitFilterParams = it
                    })
                    Text(text = i18nState.getString("init_filter_params"))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(isClearCacheData, onCheckedChange = {
                        isClearCacheData = it
                    })
                    Text(text = i18nState.getString("clear_cache_data"))
                }

                // 语言设置
                Row(modifier = Modifier.padding(top = 10.dp, start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("语言设置: ", modifier = Modifier.padding(end = 8.dp))
                    
                    // 显示当前语言
                    Text(
                        text = i18nState.getLanguageDisplayName(),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    
                    // 语言切换按钮
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            i18nState.toggleLanguage()
                        }
                    ) {
                        Text(i18nState.getToggleButtonText())
                    }
                    
                    // 重置为系统语言按钮
                    Button(
                        onClick = {
                            i18nState.resetToSystemLanguage()
                        }
                    ) {
                        Text(i18nState.getString("reset_to_system_language"))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                state.outputBoxRText   = getValidateField(block = { rText.toInt() } , failed = { showTopToast("R 需要 int 类型") }) ?: return@Button
                state.outputBoxGText   = getValidateField(block = { gText.toInt() } , failed = { showTopToast("G 需要 int 类型") }) ?: return@Button
                state.outputBoxBText   = getValidateField(block = { bText.toInt() } , failed = { showTopToast("B 需要 int 类型") }) ?: return@Button
                state.sizeText         = getValidateField(block = { sizeText.toInt() } , failed = { showTopToast("size 需要 int 类型") }) ?: return@Button
                state.deepSeekApiKeyText = deepSeekApiKeyText
                state.algorithmUrlText = if (algorithmUrlText.isNotEmpty()) {
                    getValidateField(block = {
                        if (algorithmUrlText.isValidUrl()) {

                            if (algorithmUrlText.last() == '/') {
                                algorithmUrlText
                            } else {
                                "$algorithmUrlText/"
                            }
                        } else {
                            throw RuntimeException()
                        }
                    } , failed = { showTopToast("请输入一个正确的 url") }) ?: return@Button
                } else ""

                state.maxHistorySizeText = getValidateField(block = { maxHistorySizeText.toInt() } , failed = { showTopToast("maxHistorySizeText 需要 int 类型") }) ?: return@Button

                state.saveGeneralSettings()

                if (isInitFilterParams) {
                    initFilterParamsConfig()
                }

                if (isClearCacheData) {
                    clearData()
                }

                onClick()
            }) {
                Text(i18nState.getString("update"))
            }

            Button(onClick = {
                onClick()
            }) {
                Text(i18nState.getString("close"))
            }
        })
}