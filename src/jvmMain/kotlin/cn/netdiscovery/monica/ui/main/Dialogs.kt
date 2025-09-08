package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                val versionInfo = if (isProVersion) i18nState.getString("pro_version") else i18nState.getString("test_version")
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
    var geminiApiKeyText by remember { mutableStateOf(state.geminiApiKeyText) }
    var algorithmUrlText by remember { mutableStateOf(state.algorithmUrlText) }
    var status by remember { mutableStateOf(-1) }
    var isInitFilterParams by mutableStateOf(false)
    var isClearCacheData by mutableStateOf(false)

    AlertDialog(
        onDismissRequest = {},
        modifier = Modifier.width(900.dp).height(900.dp),
        title = {
            Text(
                text = i18nState.getString("monica_general_settings"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 可滚动的内容区域
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 输出框颜色设置
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = i18nState.getString("output_box_color_settings"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // RGB颜色预览
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(
                                            Color(
                                                red = (rText.toIntOrNull() ?: 255).coerceIn(0, 255) / 255f,
                                                green = (gText.toIntOrNull() ?: 0).coerceIn(0, 255) / 255f,
                                                blue = (bText.toIntOrNull() ?: 0).coerceIn(0, 255) / 255f
                                            ),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .border(2.dp, Color.Gray, RoundedCornerShape(6.dp))
                                )
                                
                                // RGB输入框
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    basicTextFieldWithTitle(
                                        titleText = "R",
                                        value = rText,
                                        width = 80.dp
                                    ) { str ->
                                        rText = str
                                    }
                                    
                                    basicTextFieldWithTitle(
                                        titleText = "G",
                                        value = gText,
                                        width = 80.dp
                                    ) { str ->
                                        gText = str
                                    }
                                    
                                    basicTextFieldWithTitle(
                                        titleText = "B",
                                        value = bText,
                                        width = 80.dp
                                    ) { str ->
                                        bText = str
                                    }
                                }
                            }
                        }
                    }

                    // 区域大小设置
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = i18nState.getString("area_size_settings"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            basicTextFieldWithTitle(
                                titleText = "Size",
                                value = sizeText,
                                width = 150.dp
                            ) { str ->
                                sizeText = str
                            }
                        }
                    }

                    // 历史记录大小设置
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = i18nState.getString("max_history_size"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            basicTextFieldWithTitle(
                                titleText = "Max History",
                                value = maxHistorySizeText,
                                width = 150.dp
                            ) { str ->
                                maxHistorySizeText = str
                            }
                        }
                    }

                    // DeepSeek API设置
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "DeepSeek API Key",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            basicTextFieldWithTitle(
                                titleText = "API Key",
                                value = deepSeekApiKeyText,
                                width = 500.dp
                            ) { str ->
                                deepSeekApiKeyText = str
                            }
                        }
                    }

                    // Gemini API设置
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = i18nState.getString("gemini_api_key_title"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            basicTextFieldWithTitle(
                                titleText = "API Key",
                                value = geminiApiKeyText,
                                width = 500.dp
                            ) { str ->
                                geminiApiKeyText = str
                            }
                        }
                    }

                    // 算法服务设置
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = i18nState.getString("algorithm_service_url"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            basicTextFieldWithTitle(
                                titleText = "URL",
                                value = algorithmUrlText,
                                width = 500.dp
                            ) { str ->
                                algorithmUrlText = str
                            }
                            
                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    enabled = algorithmUrlText.isNotEmpty(),
                                    onClick = {
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
                                        } catch (e: Exception) {
                                            STATUS_HTTP_SERVER_FAILED
                                        }
                                    },
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Text(i18nState.getString("check_service_status"))
                                }
                                
                                // 状态指示器
                                when (status) {
                                    STATUS_HTTP_SERVER_OK -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "✓",
                                                color = Color.Green,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                i18nState.getString("algorithm_service_available"),
                                                color = Color.Green,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(start = 6.dp)
                                            )
                                        }
                                    }
                                    STATUS_HTTP_SERVER_FAILED -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "✗",
                                                color = Color.Red,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                i18nState.getString("algorithm_service_unavailable"),
                                                color = Color.Red,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(start = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 选项设置
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = i18nState.getString("options_settings"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Checkbox(
                                    checked = isInitFilterParams,
                                    onCheckedChange = { isInitFilterParams = it }
                                )
                                Text(
                                    text = i18nState.getString("init_filter_params"),
                                    fontSize = 14.sp
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isClearCacheData,
                                    onCheckedChange = { isClearCacheData = it }
                                )
                                Text(
                                    text = i18nState.getString("clear_cache_data"),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // 语言设置 - 确保在最后显示
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = i18nState.getString("language_settings"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Text(
                                    text = i18nState.getString("current_language") + ": ",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                
                                Text(
                                    text = i18nState.getLanguageDisplayName(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Blue,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { i18nState.toggleLanguage() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(i18nState.getToggleButtonText())
                                }
                                
                                Button(
                                    onClick = { i18nState.resetToSystemLanguage() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(i18nState.getString("reset_to_system_language"))
                                }
                            }
                        }
                    }
                    
                    // 底部额外间距，确保最后一个卡片不被按钮遮挡
                    Spacer(modifier = Modifier.height(40.dp))
                }
                
                // 底部按钮区域 - 固定位置
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = {
                            state.outputBoxRText = getValidateField(block = { rText.toInt() }, failed = { showTopToast("R 需要 int 类型") }) ?: return@Button
                            state.outputBoxGText = getValidateField(block = { gText.toInt() }, failed = { showTopToast("G 需要 int 类型") }) ?: return@Button
                            state.outputBoxBText = getValidateField(block = { bText.toInt() }, failed = { showTopToast("B 需要 int 类型") }) ?: return@Button
                            state.sizeText = getValidateField(block = { sizeText.toInt() }, failed = { showTopToast("size 需要 int 类型") }) ?: return@Button
                            state.deepSeekApiKeyText = deepSeekApiKeyText
                            state.geminiApiKeyText = geminiApiKeyText
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
                                }, failed = { showTopToast("请输入一个正确的 url") }) ?: return@Button
                            } else ""

                            state.maxHistorySizeText = getValidateField(block = { maxHistorySizeText.toInt() }, failed = { showTopToast("maxHistorySizeText 需要 int 类型") }) ?: return@Button

                            state.saveGeneralSettings()

                            if (isInitFilterParams) {
                                initFilterParamsConfig()
                            }

                            if (isClearCacheData) {
                                clearData()
                            }

                            onClick()
                        }
                    ) {
                        Text(i18nState.getString("update"))
                    }

                    Button(
                        onClick = { onClick() }
                    ) {
                        Text(i18nState.getString("close"))
                    }
                }
            }
        },
        confirmButton = {
            // 这里不需要内容，因为按钮已经在text中处理了
        },
        dismissButton = {
            // 这里不需要内容，因为按钮已经在text中处理了
        }
    )
}