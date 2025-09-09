package cn.netdiscovery.monica.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.preview.preview
import org.koin.compose.koinInject

/**
 * 主页面视图 - 现代化布局
 * @author: Tony Shen
 * @date: 2025/9/8
 * @version: V1.0
 */
@Composable
fun mainView(
    state: ApplicationState
) {
    val viewModel: MainViewModel = koinInject()

    viewModel.dropFile(state)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background,
                        MaterialTheme.colors.surface
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp), // 增加整体边距
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp) // 增加组件间距
        ) {
            // 左侧菜单栏
            SidebarView(state = state)

            val hasSelectedItem by remember {
                derivedStateOf {
                    state.isGeneralSettings || state.isBasic ||
                    state.isColorCorrection || state.isFilter || state.isAI
                }
            }

            // 中间内容面板，根据是否有选中项显示
            AnimatedVisibility(
                visible = hasSelectedItem,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                ContentPanel(state = state)
            }

            // 右侧预览区域
            preview(state)
        }
    }
}