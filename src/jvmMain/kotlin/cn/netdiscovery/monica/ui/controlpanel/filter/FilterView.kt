package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.rxcache.Param
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.rxcache.getFilterRemark
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.collator
import cn.netdiscovery.monica.utils.extensions.safelyConvertToInt
import cn.netdiscovery.monica.utils.getBufferedImage
import filterMaps
import filterNames
import loadingDisplay
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

/**
 * 重构后的滤镜模块 UI
 * 
 * @author: Tony Shen
 * @date: 2025/12/07
 * @version: V2.0
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun filter(state: ApplicationState) {
    val i18nState = rememberI18nState()
    val viewModel: FilterViewModel = koinInject()

    // Toast 状态
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // 搜索状态
    var searchQuery by remember { mutableStateOf("") }

    // 预览图像状态（用于实时预览）
    var previewImage by remember { mutableStateOf<java.awt.image.BufferedImage?>(null) }
    var isDirty by remember { mutableStateOf(false) } // 是否有未应用的更改
    var paramVersion by remember { mutableStateOf(0) } // 用于强制刷新参数控件状态
    var appliedParamSnapshot by remember { mutableStateOf<Map<Pair<String, String>, String>>(emptyMap()) } // 上次 Apply 的参数快照
    val selectedIndexState = remember { mutableStateOf(-1) }
    val paramMap = remember { androidx.compose.runtime.mutableStateMapOf<Pair<String, String>, String>() } // 当前参数（UI 状态源）
    // 进入滤镜模块前的基线图：用于“清除滤镜”恢复原效果
    val baseImageSnapshot = remember { mutableStateOf<java.awt.image.BufferedImage?>(null) }
    // 当前选中滤镜的基线图：用于“同一滤镜内调参不叠加”，但允许滤镜之间叠加
    val currentFilterBaseImageSnapshot = remember { mutableStateOf<java.awt.image.BufferedImage?>(null) }

    // 打开滤镜模块时，锁定一次基线（仅首次）；后续如用户重新加载图片，会在 onImageClick 中更新
    LaunchedEffect(Unit) {
        if (baseImageSnapshot.value == null) {
            // 基线要以“进入滤镜模块前的效果”为准，所以优先 currentImage
            baseImageSnapshot.value = state.currentImage ?: state.rawImage
        }
    }

    // 缩放状态
    var zoomLevel by remember { mutableStateOf(1.0f) }

    PageLifecycle(
        onInit = {
            logger.info("FilterView 启动时初始化")
        },
        onDisposeEffect = {
            logger.info("FilterView 关闭时释放资源")
            viewModel.clear()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar
        FilterTopAppBar(
            onSave = {
                state.closePreviewWindow()
            },
            onExport = {
                // TODO: 实现导出功能
            },
            i18nState = i18nState
        )

        // Main Content Area
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Left Sidebar - Filter List
            FilterListPanel(
                modifier = Modifier.width(280.dp),
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedIndex = selectedIndexState.value,
                onFilterSelected = { index ->
                    selectedIndexState.value = index
                    isDirty = false
                    previewImage = null
                    // 重置参数为默认值
                    paramMap.clear()
                    val filterName = filterNames[index]
                    paramMap.putAll(buildDefaultParamMap(filterName))
                    // 切换滤镜时：默认参数也作为“已应用”的基线（直到用户 Apply）
                    appliedParamSnapshot = HashMap(paramMap)
                    paramVersion++
                    // 方式1：滤镜之间叠加 —— 新滤镜基于当前画布图像继续算
                    val base = state.currentImage ?: state.rawImage
                    if (base != null) {
                        currentFilterBaseImageSnapshot.value = base
                        viewModel.applyFilter(
                            state = state,
                            index = index,
                            paramMap = HashMap(paramMap),
                            sourceImage = base,
                            pushHistory = true
                        )
                    }
                },
                state = state,
                i18nState = i18nState
            )

            // Center - Image Preview Area
            FilterPreviewArea(
                modifier = Modifier.weight(1f),
                state = state,
                previewImage = previewImage,
                zoomLevel = zoomLevel,
                onZoomChange = { zoomLevel = it },
                onImageClick = {
                    if (state.currentImage == null) {
                        chooseImage(state) { file ->
                            state.rawImage = getBufferedImage(file, state)
                            state.currentImage = state.rawImage
                            state.rawImageFile = file
                            baseImageSnapshot.value = state.currentImage
                            currentFilterBaseImageSnapshot.value = state.currentImage
                            previewImage = null
                            isDirty = false
                        }
                    }
                },
                i18nState = i18nState
            )

            // Right Sidebar - Adjustment Panel
            FilterAdjustmentPanel(
                modifier = Modifier.width(300.dp),
                selectedIndex = selectedIndexState.value,
                state = state,
                filterBaseImage = currentFilterBaseImageSnapshot.value,
                viewModel = viewModel,
                previewImage = previewImage,
                onPreviewImageChange = { previewImage = it },
                isDirty = isDirty,
                onDirtyChange = { isDirty = it },
                paramVersion = paramVersion,
                onParamVersionChange = { paramVersion = it },
                appliedParamSnapshot = appliedParamSnapshot,
                onAppliedParamSnapshotChange = { appliedParamSnapshot = it },
                paramMap = paramMap,
                onClearFilter = {
                    val base = baseImageSnapshot.value ?: return@FilterAdjustmentPanel
                    val before = state.currentImage ?: base
                    // 回到进入滤镜模块前的效果，并记录一次历史便于撤销
                    state.currentImage = base
                    state.addQueue(before)
                    // 清理 UI 状态：取消选中滤镜，避免“选中但未应用”的错觉
                    selectedIndexState.value = -1
                    paramMap.clear()
                    appliedParamSnapshot = emptyMap()
                    currentFilterBaseImageSnapshot.value = null
                    previewImage = null
                    isDirty = false
                    paramVersion++
                },
                onShowToast = { message ->
                    toastMessage = message
                    showToast = true
                },
                i18nState = i18nState
            )
        }
    }

    // Loading Indicator
    if (loadingDisplay) {
        showLoading()
    }

    // Toast Message
    if (showToast) {
        centerToast(
            modifier = Modifier,
            message = toastMessage
        ) {
            showToast = false
        }
    }
}
