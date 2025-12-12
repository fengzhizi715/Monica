package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.i18n.I18nState
import filterMaps
import filterNames

/**
 * 左侧滤镜列表面板
 */
@Composable
fun FilterListPanel(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedIndex: Int,
    onFilterSelected: (Int) -> Unit,
    state: ApplicationState,
    i18nState: I18nState
) {
    val currentImagePainter = remember(state.currentImage) { state.currentImage?.toPainter() }

    Surface(
        modifier = modifier.fillMaxHeight(),
        color = Color.White,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 搜索栏
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                placeholder = {
                    Text(
                        text = i18nState.getString("search"),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF666666)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            // 滤镜列表
            val filteredFilters = remember(searchQuery, filterNames) {
                if (searchQuery.isBlank()) {
                    filterNames.indices.toList()
                } else {
                    filterNames.indices.filter { index ->
                        filterNames[index].contains(searchQuery, ignoreCase = true) ||
                        (filterMaps[filterNames[index]]?.contains(searchQuery, ignoreCase = true) == true)
                    }
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredFilters.isEmpty()) {
                    item {
                        Text(
                            text = i18nState.getString("no_filters_found"),
                            fontSize = 12.sp,
                            color = Color(0xFF999999),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                    return@LazyColumn
                }

                itemsIndexed(items = filteredFilters, key = { _, filterIndex -> filterIndex }) { _, filterIndex ->
                    val isSelected = filterIndex == selectedIndex
                    val filterName = filterNames[filterIndex]
                    
                    FilterListItem(
                        filterName = filterName,
                        isSelected = isSelected,
                        onClick = { onFilterSelected(filterIndex) },
                        state = state,
                        imagePainter = currentImagePainter,
                        noImageText = i18nState.getString("no_image"),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * 单个滤镜列表项
 */
@Composable
private fun FilterListItem(
    filterName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    state: ApplicationState,
    imagePainter: androidx.compose.ui.graphics.painter.Painter?,
    noImageText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        elevation = if (isSelected) 4.dp else 1.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = if (isSelected) {
            Color(0xFFE3F2FD) // 淡蓝背景
        } else {
            Color.White
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 左侧选中指示条
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(
                            color = Color(0xFF007AFF),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }
            
            // 缩略图预览（使用当前图像的小缩略图）
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imagePainter != null) {
                    // 这里可以显示应用滤镜后的缩略图，简化版先显示原图
                    Image(
                        painter = imagePainter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = noImageText,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
            
            // 滤镜名称
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = filterName,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) {
                        Color(0xFF007AFF)
                    } else {
                        Color(0xFF222222)
                    }
                )
                // 滤镜描述（如果有）
                filterMaps[filterName]?.split("-")?.firstOrNull()?.let { desc ->
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        maxLines = 1
                    )
                }
            }
        }
    }
}