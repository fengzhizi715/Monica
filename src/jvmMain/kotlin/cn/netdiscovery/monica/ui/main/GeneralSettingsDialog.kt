package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.exception.ErrorSeverity
import cn.netdiscovery.monica.exception.ErrorType
import cn.netdiscovery.monica.exception.showError
import cn.netdiscovery.monica.i18n.Language
import cn.netdiscovery.monica.i18n.LocalizationManager
import cn.netdiscovery.monica.rxcache.clearData
import cn.netdiscovery.monica.rxcache.initFilterParamsConfig
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.theme.ColorTheme
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.desktopLazyRow
import cn.netdiscovery.monica.utils.Action
import cn.netdiscovery.monica.utils.extensions.isValidUrl
import cn.netdiscovery.monica.utils.getValidateField
import showTopToast

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.main.GeneralSettingsDialog
 * @author: Tony Shen
 * @date: 2025/9/9 18:09
 * @version: V1.0 <描述当前版本功能>
 */

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
    var isInitFilterParams by mutableStateOf(false)
    var isClearCacheData by mutableStateOf(false)
    var selectedTab by remember { mutableStateOf(0) }

    val tabTitles = listOf(
        i18nState.getString("basic_settings"),
        i18nState.getString("api_settings"),
        i18nState.getString("theme_settings"),
        i18nState.getString("language_settings")
    )

    AlertDialog(
        onDismissRequest = {},
        modifier = Modifier
            .width(1000.dp)
            .height(800.dp)
            .background(MaterialTheme.colors.surface, RoundedCornerShape(16.dp)),
        text = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // 主要内容区域 - 使用TabRow进行分组
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 标题
                    Text(
                        text = i18nState.getString("monica_general_settings"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = state.getCurrentThemeValue().primary,
                        modifier = Modifier
                    )

                    // 标签页选择器
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.surface,
                        contentColor = MaterialTheme.colors.onSurface,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 3.dp,
                                color = state.getCurrentThemeValue().primary
                            )
                        }
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                modifier = Modifier.padding(vertical = 12.dp),
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 14.sp,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == index) {
                                            state.getCurrentThemeValue().primary
                                        } else {
                                            MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                        }
                                    )
                                }
                            )
                        }
                    }

                    // 标签页内容
                    Box(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                        when (selectedTab) {
                            0 -> {
                                // 基础设置
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // 输出框颜色设置
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("output_box_color_settings"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                basicTextFieldWithTitle(
                                                    titleText = "R",
                                                    value = rText,
                                                    onValueChange = { rText = it },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                basicTextFieldWithTitle(
                                                    titleText = "G",
                                                    value = gText,
                                                    onValueChange = { gText = it },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                basicTextFieldWithTitle(
                                                    titleText = "B",
                                                    value = bText,
                                                    onValueChange = { bText = it },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }

                                    // 区域大小设置
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("area_size_settings"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            basicTextFieldWithTitle(
                                                titleText = "Size",
                                                value = sizeText,
                                                onValueChange = { sizeText = it },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    // 历史记录大小设置
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("max_history_size"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            basicTextFieldWithTitle(
                                                titleText = "Max History Size",
                                                value = maxHistorySizeText,
                                                onValueChange = { maxHistorySizeText = it },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    // 选项设置
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                        ) {
                                            Text(
                                                text = i18nState.getString("options_settings"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Checkbox(
                                                    checked = isInitFilterParams,
                                                    onCheckedChange = { isInitFilterParams = it }
                                                )
                                                Text(i18nState.getString("init_filter_params_config"))
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Checkbox(
                                                    checked = isClearCacheData,
                                                    onCheckedChange = { isClearCacheData = it }
                                                )
                                                Text(i18nState.getString("clear_cache_data"))
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                // API设置
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // DeepSeek API设置
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("ai_provider_deepseek") + " API Key",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            basicTextFieldWithTitle(
                                                titleText = "DeepSeek API Key",
                                                value = deepSeekApiKeyText,
                                                onValueChange = { deepSeekApiKeyText = it },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    // Gemini API设置
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("ai_provider_gemini") + " API Key",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            basicTextFieldWithTitle(
                                                titleText = "Gemini API Key",
                                                value = geminiApiKeyText,
                                                onValueChange = { geminiApiKeyText = it },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    // 算法URL设置
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("algorithm_service_url"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            basicTextFieldWithTitle(
                                                titleText = "Algorithm URL",
                                                value = algorithmUrlText,
                                                onValueChange = { algorithmUrlText = it },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Text(
                                                text = i18nState.getString("enter_complete_algorithm_url"),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                            2 -> {
                                // 主题设置
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // 当前主题显示
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(20.dp),
                                        ) {
                                            Text(
                                                text = i18nState.getString("current_theme"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            Text(
                                                text = state.getCurrentThemeValue().getThemeDisplayName(),
                                                color = state.getCurrentThemeValue().primary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(start = 20.dp)
                                            )
                                        }
                                    }

                                    // 主题选择
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("select_theme"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            desktopLazyRow(modifier = Modifier.fillMaxWidth()) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    ColorTheme.entries.forEach { theme ->
                                                        val isSelected = state.getCurrentThemeValue() == theme
                                                        Card(
                                                            modifier = Modifier
                                                                .width(120.dp)
                                                                .height(80.dp)
                                                                .clickable {
                                                                    state.setTheme(theme)
                                                                },
                                                            elevation = if (isSelected) 8.dp else 2.dp,
                                                            shape = RoundedCornerShape(8.dp),
                                                            backgroundColor = theme.background
                                                        ) {
                                                            Box(
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                // 选中状态的边框效果
                                                                if (isSelected) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .fillMaxSize()
                                                                            .background(
                                                                                Color.Transparent,
                                                                                RoundedCornerShape(8.dp)
                                                                            )
                                                                            .border(
                                                                                width = 2.dp,
                                                                                color = theme.primary,
                                                                                shape = RoundedCornerShape(8.dp)
                                                                            )
                                                                    )
                                                                }

                                                                Text(
                                                                    text = theme.getThemeDisplayName(),
                                                                    color = theme.onBackground,
                                                                    fontSize = 12.sp,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 重置按钮
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("theme_operations"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            Button(
                                                onClick = {
                                                    state.setTheme(ColorTheme.LIGHT)
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(backgroundColor = state.getCurrentThemeValue().primary)
                                            ) {
                                                Text(i18nState.getString("reset_to_default_theme"), color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> {
                                // 语言设置
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // 当前语言显示
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Row (
                                            modifier = Modifier.padding(20.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("current_language"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            Text(
                                                text = if (LocalizationManager.currentLanguage == Language.CHINESE) i18nState.getString("chinese") else i18nState.getString("english"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colors.onSurface,
                                                modifier = Modifier.padding(start = 20.dp)
                                            )
                                        }
                                    }

                                    // 语言切换
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("language_switch"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        LocalizationManager.setLanguage(Language.CHINESE)
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        backgroundColor = if (LocalizationManager.currentLanguage == Language.CHINESE)
                                                            state.getCurrentThemeValue().primary
                                                        else
                                                            MaterialTheme.colors.surface
                                                    )
                                                ) {
                                                    Text(
                                                        text = i18nState.getString("chinese"),
                                                        color = if (LocalizationManager.currentLanguage == Language.CHINESE)
                                                            MaterialTheme.colors.onPrimary
                                                        else
                                                            MaterialTheme.colors.onSurface
                                                    )
                                                }

                                                Button(
                                                    onClick = {
                                                        LocalizationManager.setLanguage(Language.ENGLISH)
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        backgroundColor = if (LocalizationManager.currentLanguage == Language.ENGLISH)
                                                            state.getCurrentThemeValue().primary
                                                        else
                                                            MaterialTheme.colors.surface
                                                    )
                                                ) {
                                                    Text(
                                                        text = "English",
                                                        color = if (LocalizationManager.currentLanguage == Language.ENGLISH)
                                                            MaterialTheme.colors.onPrimary
                                                        else
                                                            MaterialTheme.colors.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // 重置语言
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = MaterialTheme.colors.surface
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = i18nState.getString("language_operations"),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colors.onSurface
                                            )

                                            Button(
                                                onClick = {
                                                    LocalizationManager.setLanguage(Language.CHINESE)
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(backgroundColor = state.getCurrentThemeValue().primary)
                                            ) {
                                                Text(i18nState.getString("reset_to_chinese"), color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 底部按钮区域 - 固定在底部
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            MaterialTheme.colors.surface,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            state.outputBoxRText = getValidateField(block = { rText.toInt() }, failed = {
                                    val errorMsg = i18nState.getString("r_needs_int")
                                    showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.LOW, errorMsg, errorMsg)
                                }) ?: return@Button
                            state.outputBoxGText = getValidateField(block = { gText.toInt() }, failed = {
                                    val errorMsg = i18nState.getString("g_needs_int")
                                    showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.LOW, errorMsg, errorMsg)
                                }) ?: return@Button
                            state.outputBoxBText = getValidateField(block = { bText.toInt() }, failed = {
                                val errorMsg = i18nState.getString("b_needs_int")
                                showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.LOW, errorMsg, errorMsg)
                            }) ?: return@Button
                            state.sizeText = getValidateField(block = { sizeText.toInt() }, failed = {
                                val errorMsg = i18nState.getString("size_needs_int")
                                showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.LOW, errorMsg, errorMsg)
                            }) ?: return@Button
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
                                }, failed = {
                                    val errorMsg = i18nState.getString("enter_valid_url")
                                    showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.LOW, errorMsg, errorMsg)
                                }) ?: return@Button
                            } else ""

                            state.maxHistorySizeText = getValidateField(block = { maxHistorySizeText.toInt() }, failed = {
                                val errorMsg = i18nState.getString("max_history_size_needs_int")
                                showError(ErrorType.VALIDATION_ERROR, ErrorSeverity.LOW, errorMsg, errorMsg)
                            }) ?: return@Button

                            state.saveGeneralSettings()

                            if (isInitFilterParams) {
                                initFilterParamsConfig()
                            }

                            if (isClearCacheData) {
                                clearData()
                            }

                            onClick()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = state.getCurrentThemeValue().primary)
                    ) {
                        Text(i18nState.getString("update"), color = Color.White)
                    }

                    Button(
                        onClick = { onClick() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = state.getCurrentThemeValue().primary)
                    ) {
                        Text(i18nState.getString("close"), color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            // 空内容，按钮在text中处理
        },
        dismissButton = {
            // 空内容，按钮在text中处理
        }
    )
}