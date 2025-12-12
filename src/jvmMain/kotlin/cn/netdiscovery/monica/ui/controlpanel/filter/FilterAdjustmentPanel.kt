package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.rxcache.Param
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.rxcache.getFilterRemark
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.i18n.I18nState
import cn.netdiscovery.monica.utils.collator
import cn.netdiscovery.monica.utils.extensions.safelyConvertToInt
import filterNames
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.roundToInt

/**
 * 右侧参数调整面板
 */
@Composable
fun FilterAdjustmentPanel(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    state: ApplicationState,
    baseImage: BufferedImage?,
    viewModel: FilterViewModel,
    previewImage: BufferedImage?,
    onPreviewImageChange: (BufferedImage?) -> Unit,
    isDirty: Boolean,
    onDirtyChange: (Boolean) -> Unit,
    paramVersion: Int,
    onParamVersionChange: (Int) -> Unit,
    appliedParamSnapshot: Map<Pair<String, String>, String>,
    onAppliedParamSnapshotChange: (Map<Pair<String, String>, String>) -> Unit,
    paramMap: MutableMap<Pair<String, String>, String>,
    onClearFilter: () -> Unit,
    onShowToast: (String) -> Unit,
    i18nState: I18nState
) {
    var expanded by remember(selectedIndex) { mutableStateOf(true) }
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = Color.White,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 面板标题
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5F5F5),
                elevation = 1.dp
            ) {
                Text(
                    text = i18nState.getString("adjustments"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(20.dp)
                )
            }
            
            // 可滚动内容区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedIndex >= 0) {
                    val filterName = filterNames[selectedIndex]
                    
                    // 滤镜名称（可折叠区域）
                    FilterNameSection(
                        filterName = filterName,
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    )

                    // 收起时：展示参数摘要 + Reset 提示，让用户一眼理解当前状态
                    if (!expanded) {
                        FilterParamSummarySection(
                            filterName = filterName,
                            paramMap = paramMap,
                            i18nState = i18nState
                        )
                    }
                    
                    AnimatedVisibility(visible = expanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // 参数滑块
                            FilterParamsSection(
                                filterName = filterName,
                                selectedIndex = selectedIndex,
                                state = state,
                                baseImage = baseImage,
                                viewModel = viewModel,
                                onPreviewImageChange = onPreviewImageChange,
                                onDirtyChange = onDirtyChange,
                                paramVersion = paramVersion,
                                paramMap = paramMap,
                                onCommitted = { latest ->
                                    onAppliedParamSnapshotChange(latest)
                                },
                                i18nState = i18nState
                            )

                            // Notes 区域
                            FilterNotesSection(
                                filterName = filterName,
                                i18nState = i18nState
                            )
                        }
                    }
                } else {
                    Text(
                        text = i18nState.getString("select_filter_first"),
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            }
            
            // 底部按钮区域
            if (selectedIndex >= 0) {
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                FilterActionButtons(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    state = state,
                    baseImage = baseImage,
                    viewModel = viewModel,
                    selectedIndex = selectedIndex,
                    previewImage = previewImage,
                    onPreviewImageChange = onPreviewImageChange,
                    isDirty = isDirty,
                    onDirtyChange = onDirtyChange,
                    paramVersion = paramVersion,
                    onParamVersionChange = onParamVersionChange,
                    appliedParamSnapshot = appliedParamSnapshot,
                    onAppliedParamSnapshotChange = onAppliedParamSnapshotChange,
                    paramMap = paramMap,
                    onClearFilter = onClearFilter,
                    onShowToast = onShowToast,
                    i18nState = i18nState
                )
            }
        }
    }
}

/**
 * 滤镜名称区域（可折叠）
 */
@Composable
private fun FilterNameSection(
    filterName: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!expanded) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = filterName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
private fun FilterParamSummarySection(
    filterName: String,
    paramMap: Map<Pair<String, String>, String>,
    i18nState: I18nState
) {
    val defaultMap = remember(filterName) { buildDefaultParamMap(filterName) }
    val paramByKey = remember(filterName) {
        getFilterParam(filterName).orEmpty().associateBy { it.key }
    }
    val changedEntries = remember(filterName, defaultMap, paramMap) {
        paramMap.entries
            .filter { (k, v) -> defaultMap[k] != v }
            .sortedWith(compareBy({ it.key.first.lowercase() }, { it.key.second }))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFFFAFAFA)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = i18nState.getString("param_summary"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )

                if (changedEntries.isEmpty()) {
                    Text(
                        text = i18nState.getString("param_summary_default"),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                } else {
                    Text(
                        text = i18nState.getString("param_summary_changed_count").format(changedEntries.size),
                        fontSize = 12.sp,
                        color = Color(0xFF007AFF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (changedEntries.isNotEmpty()) {
                val previewItems = changedEntries.take(3)
                previewItems.forEach { entry ->
                    val key = entry.key.first
                    val rawValue = entry.value
                    val value = run {
                        val param = paramByKey[key]
                        if (param != null) {
                            val meta = FilterParamMetaRegistry.resolve(filterName = filterName, param = param)
                            val intVal = rawValue.safelyConvertToInt()
                            val option = intVal?.let { v -> meta.enumOptions?.firstOrNull { it.value == v } }
                            if (option != null) {
                                "${i18nState.getString(option.labelKey)} ($rawValue)"
                            } else {
                                rawValue
                            }
                        } else {
                            rawValue
                        }
                    }
                    Text(
                        text = "$key: $value",
                        fontSize = 12.sp,
                        color = Color(0xFF444444),
                        maxLines = 1
                    )
                }
                if (changedEntries.size > 3) {
                    Text(
                        text = "+${changedEntries.size - 3} ...",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }

                Text(
                    text = i18nState.getString("param_summary_reset_hint"),
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * 滤镜参数区域
 */
@Composable
private fun FilterParamsSection(
    filterName: String,
    selectedIndex: Int,
    state: ApplicationState,
    baseImage: BufferedImage?,
    viewModel: FilterViewModel,
    onPreviewImageChange: (BufferedImage?) -> Unit,
    onDirtyChange: (Boolean) -> Unit,
    paramVersion: Int,
    paramMap: MutableMap<Pair<String, String>, String>,
    onCommitted: (Map<Pair<String, String>, String>) -> Unit,
    i18nState: I18nState
) {
    val params: List<Param>? = getFilterParam(filterName)
    
    if (params != null && params.isNotEmpty()) {
        val sortedParams = remember(params) {
            params.sortedWith { o1, o2 -> collator.compare(o1.key, o2.key) }
        }
        
        sortedParams.forEach { param ->
            FilterParamSlider(
                param = param,
                filterName = filterName,
                selectedIndex = selectedIndex,
                state = state,
                baseImage = baseImage,
                viewModel = viewModel,
                onPreviewImageChange = onPreviewImageChange,
                onDirtyChange = onDirtyChange,
                paramVersion = paramVersion,
                paramMap = paramMap,
                onCommitted = onCommitted,
                i18nState = i18nState
            )
        }
    }
}

/**
 * 单个参数滑块
 */
@Composable
private fun FilterParamSlider(
    param: Param,
    filterName: String,
    selectedIndex: Int,
    state: ApplicationState,
    baseImage: BufferedImage?,
    viewModel: FilterViewModel,
    onPreviewImageChange: (BufferedImage?) -> Unit,
    onDirtyChange: (Boolean) -> Unit,
    paramVersion: Int,
    paramMap: MutableMap<Pair<String, String>, String>,
    onCommitted: (Map<Pair<String, String>, String>) -> Unit,
    i18nState: I18nState
) {
    val paramKey = param.key
    val paramType = param.type
    val focusManager = LocalFocusManager.current
    
    // 从 filterTempMap 获取当前值，如果没有则使用默认值
    val defaultValue = when (paramType) {
        "Int" -> (param.value.toString().safelyConvertToInt() ?: 0).toString()
        else -> param.value.toString()
    }
    
    val initialValue = paramMap[Pair(paramKey, paramType)] ?: defaultValue
    var draftText by remember(filterName, paramKey, paramVersion) { mutableStateOf(initialValue) }
    var lastValidText by remember(filterName, paramKey, paramVersion) { mutableStateOf(initialValue) }
    var hasFocus by remember { mutableStateOf(false) }
    var isDragging by remember(filterName, paramKey, paramVersion) { mutableStateOf(false) }
    var pendingSamplePreview by remember(filterName, paramKey, paramVersion) { mutableStateOf(false) }
    
    // 转换为数值用于滑块
    val parsedValueOrNull: Float? = when (paramType) {
        "Int" -> draftText.safelyConvertToInt()?.toFloat()
        "Float" -> draftText.toFloatOrNull()
        "Double" -> draftText.toDoubleOrNull()?.toFloat()
        else -> null
    }
    val lastValidNumeric: Float = when (paramType) {
        "Int" -> lastValidText.safelyConvertToInt()?.toFloat() ?: 0f
        "Float" -> lastValidText.toFloatOrNull() ?: 0f
        "Double" -> lastValidText.toDoubleOrNull()?.toFloat() ?: 0f
        else -> 0f
    }
    val numericValue = parsedValueOrNull ?: lastValidNumeric
    
    val meta = remember(filterName, paramKey, paramType) {
        FilterParamMetaRegistry.resolve(filterName = filterName, param = param)
    }
    val minValue = meta.min
    val maxValue = meta.max
    val step = meta.step
    val decimals = meta.decimals
    val enumOptions = meta.enumOptions

    fun triggerPreviewNow() {
        if (state.currentImage != null) {
            viewModel.applyFilterPreview(
                state = state,
                index = selectedIndex,
                paramMap = HashMap(paramMap),
                debounceMs = 0,
                onSuccess = { image -> onPreviewImageChange(image) },
                onError = { }
            )
        }
    }

    fun commitNow() {
        val base = baseImage ?: state.currentImage ?: state.rawImage
        if (base == null) return
        viewModel.applyFilter(
            state = state,
            index = selectedIndex,
            paramMap = HashMap(paramMap),
            sourceImage = base,
            pushHistory = true
        )
        onPreviewImageChange(null)
        onDirtyChange(false)
        onCommitted(HashMap(paramMap))
    }

    // Slider：拖动中不实时算，但每 300ms 抽样触发一次（仅当这段时间内有变化）
    LaunchedEffect(filterName, paramKey, paramVersion, isDragging) {
        if (!isDragging) return@LaunchedEffect
        while (isActive && isDragging) {
            delay(300)
            if (pendingSamplePreview) {
                pendingSamplePreview = false
                triggerPreviewNow()
            }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 参数名称和数值显示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = paramKey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                if (paramType == "Int" && !enumOptions.isNullOrEmpty()) {
                    EnumParamDropdown(
                        valueText = draftText,
                        options = enumOptions,
                        onSelect = { newInt ->
                            val newValue = newInt.toString()
                            draftText = newValue
                            lastValidText = newValue
                            paramMap[Pair(paramKey, paramType)] = newValue
                            onDirtyChange(true)
                            commitNow()
                        },
                        i18nState = i18nState
                    )
                } else {
                    // 数字输入框
                    OutlinedTextField(
                        value = draftText,
                        onValueChange = { newValue ->
                            draftText = newValue

                            // 仅当输入可解析时才更新参数与触发预览；否则等待失焦回退
                            val ok = when (paramType) {
                                "Int" -> newValue.safelyConvertToInt() != null
                                "Float" -> newValue.toFloatOrNull() != null
                                "Double" -> newValue.toDoubleOrNull() != null
                                else -> true
                            }

                            if (ok) {
                                lastValidText = newValue
                                paramMap[Pair(paramKey, paramType)] = newValue
                                onDirtyChange(true)
                                // 输入过程中仍做预览（防止每个字符都提交）
                                if (state.currentImage != null) {
                                    viewModel.applyFilterPreview(
                                        state = state,
                                        index = selectedIndex,
                                        paramMap = HashMap(paramMap),
                                        debounceMs = 200,
                                        onSuccess = { image -> onPreviewImageChange(image) },
                                        onError = { }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .width(140.dp)
                            .onFocusChanged { hasFocus = it.isFocused },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                // Done：直接提交
                                commitNow()
                            }
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            }
            
            // 滑块（仅对数值类型显示）
            if (paramType in listOf("Int", "Float", "Double") && (enumOptions.isNullOrEmpty() || paramType != "Int")) {
                Slider(
                    value = numericValue.coerceIn(minValue, maxValue),
                    onValueChange = { newValue ->
                        val snapped = if (step > 0f) {
                            (newValue / step).roundToInt() * step
                        } else {
                            newValue
                        }
                        val next = when (paramType) {
                            "Int" -> snapped.toInt().toString()
                            "Float" -> String.format(Locale.US, "%.${decimals}f", snapped)
                            "Double" -> String.format(Locale.US, "%.${decimals}f", snapped)
                            else -> snapped.toString()
                        }
                        draftText = next
                        lastValidText = next
                        paramMap[Pair(paramKey, paramType)] = next
                        onDirtyChange(true)
                        isDragging = true
                        pendingSamplePreview = true
                    },
                    onValueChangeFinished = {
                        // 松手后：立即算一次，确保最终值立刻出预览
                        isDragging = false
                        pendingSamplePreview = false
                        // 拖动即提交：松手时提交到编辑器（并生成一次历史节点）
                        commitNow()
                    },
                    valueRange = minValue..maxValue,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF007AFF),
                        activeTrackColor = Color(0xFF007AFF)
                    )
                )
            }

            // 失焦时如果输入非法，回退到上一次有效值（符合 Spec）
            LaunchedEffect(hasFocus) {
                if (!hasFocus) {
                    val ok = when (paramType) {
                        "Int" -> draftText.safelyConvertToInt() != null
                        "Float" -> draftText.toFloatOrNull() != null
                        "Double" -> draftText.toDoubleOrNull() != null
                        else -> true
                    }
                    if (!ok) {
                        draftText = lastValidText
                    } else if (draftText != lastValidText) {
                        // 理论上不会发生（lastValidText 会同步），兜底：失焦提交一次
                        commitNow()
                    }
                }
            }
        }
    }
}

@Composable
private fun EnumParamDropdown(
    valueText: String,
    options: List<FilterEnumOption>,
    onSelect: (Int) -> Unit,
    i18nState: I18nState
) {
    var expanded by remember { mutableStateOf(false) }
    val currentInt = valueText.safelyConvertToInt()
    val currentLabel = currentInt?.let { v ->
        options.firstOrNull { it.value == v }?.let { opt -> i18nState.getString(opt.labelKey) }
    } ?: valueText

    Box(modifier = Modifier.width(140.dp)) {
        OutlinedTextField(
            value = if (currentInt != null) "$currentLabel ($currentInt)" else currentLabel,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF007AFF),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        // 透明点击层：避免 TextField 在 Desktop 上吞掉点击事件导致无法展开
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onSelect(opt.value)
                }) {
                    Text("${i18nState.getString(opt.labelKey)} (${opt.value})")
                }
            }
        }
    }
}

/**
 * Notes 区域
 */
@Composable
private fun FilterNotesSection(
    filterName: String,
    i18nState: I18nState
) {
    val remark = getFilterRemark(filterName)
    
    if (!remark.isNullOrEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 1.dp,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFEEEEEE)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = i18nState.getString("notes"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                Text(
                    text = remark,
                    fontSize = 12.sp,
                    color = Color(0xFF222222),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * 底部操作按钮
 */
@Composable
private fun FilterActionButtons(
    modifier: Modifier = Modifier,
    state: ApplicationState,
    baseImage: BufferedImage?,
    viewModel: FilterViewModel,
    selectedIndex: Int,
    previewImage: BufferedImage?,
    onPreviewImageChange: (BufferedImage?) -> Unit,
    isDirty: Boolean,
    onDirtyChange: (Boolean) -> Unit,
    paramVersion: Int,
    onParamVersionChange: (Int) -> Unit,
    appliedParamSnapshot: Map<Pair<String, String>, String>,
    onAppliedParamSnapshotChange: (Map<Pair<String, String>, String>) -> Unit,
    paramMap: MutableMap<Pair<String, String>, String>,
    onClearFilter: () -> Unit,
    onShowToast: (String) -> Unit,
    i18nState: I18nState
) {
    val hasImage = state.currentImage != null
    val filterName = remember(selectedIndex) { filterNames[selectedIndex] }
    val defaultMap = remember(selectedIndex) { buildDefaultParamMap(filterName) }
    val isAtDefault = remember(defaultMap, paramMap) { paramMap == defaultMap }
    val canCancel = (isDirty || previewImage != null)
    val canReset = hasImage && !isAtDefault
    val canClear = hasImage

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：Reset + 清除滤镜
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    paramMap.clear()
                    paramMap.putAll(defaultMap)
                    // 拖动即提交：Reset 也直接提交
                    val base = baseImage ?: state.currentImage ?: state.rawImage
                    if (base != null) {
                        viewModel.applyFilter(
                            state = state,
                            index = selectedIndex,
                            paramMap = HashMap(paramMap),
                            sourceImage = base,
                            pushHistory = true
                        )
                    }
                    onDirtyChange(false)
                    onPreviewImageChange(null)
                    onAppliedParamSnapshotChange(HashMap(paramMap))
                    onParamVersionChange(paramVersion + 1)
                },
                enabled = canReset,
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF222222)
                )
            ) {
                Text(
                    text = i18nState.getString("reset_filter"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick = onClearFilter,
                enabled = canClear,
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF222222)
                )
            ) {
                Text(
                    text = i18nState.getString("clear_filter"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 右侧：Cancel + Apply
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    // Cancel：回到上次提交后的参数（仅用于取消未松手/未提交的预览状态）
                    paramMap.clear()
                    paramMap.putAll(appliedParamSnapshot)
                    onParamVersionChange(paramVersion + 1)
                    onPreviewImageChange(null)
                    onDirtyChange(false)
                },
                enabled = canCancel,
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF222222)
                )
            ) {
                Text(
                    text = i18nState.getString("cancel"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

