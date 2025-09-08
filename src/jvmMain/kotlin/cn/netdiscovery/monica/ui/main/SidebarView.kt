package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.state.ColorCorrectionStatus
import cn.netdiscovery.monica.state.FilterStatus
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.widget.rememberThrottledClick
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import showGeneralSettings

/**
 * 侧边栏组件 - NavigationRail 风格
 * @author: Tony Shen
 * @date: 2025/9/8
 * @version: V1.0
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

enum class SidebarItem(
    val titleKey: String,
    val iconPath: String,
    val isEnabled: (ApplicationState) -> Boolean,
    val onClick: (ApplicationState) -> Unit
) {
    GENERAL_SETTINGS(
        titleKey = "general_settings",
        iconPath = "images/controlpanel/settings.png",
        isEnabled = { state -> state.isGeneralSettings },
        onClick = { state ->
            showGeneralSettings = true

            state.isBasic = false
            state.isColorCorrection = false
            state.isFilter = false
            state.isAI = false
        }
    ),
    BASIC_FUNCTIONS(
        titleKey = "basic_functions",
        iconPath = "images/controlpanel/settings.png",
        isEnabled = { state -> state.isBasic },
        onClick = { state ->
            state.isBasic = !state.isBasic

            state.isGeneralSettings = false
            state.isColorCorrection = false
            state.isFilter = false
            state.isAI = false
        }
    ),
    COLOR_CORRECTION(
        titleKey = "image_color_correction",
        iconPath = "images/controlpanel/settings.png",
        isEnabled = { state -> state.isColorCorrection },
        onClick = { state ->
            state.togglePreviewWindowAndUpdateStatus(ColorCorrectionStatus)

            state.isGeneralSettings = false
            state.isBasic = false
            state.isFilter = false
            state.isAI = false
        }
    ),
    FILTER(
        titleKey = "filter_effects",
        iconPath = "images/controlpanel/settings.png",
        isEnabled = { state -> state.isFilter },
        onClick = { state ->
            state.togglePreviewWindowAndUpdateStatus(FilterStatus)

            state.isBasic = false
            state.isGeneralSettings = false
            state.isColorCorrection = false
            state.isAI = false
        }
    ),
    AI_LAB(
        titleKey = "ai_laboratory",
        iconPath = "images/controlpanel/settings.png",
        isEnabled = { state -> state.isAI },
        onClick = { state ->
            state.isAI = !state.isAI

            state.isBasic = false
            state.isGeneralSettings = false
            state.isColorCorrection = false
            state.isFilter = false
        }
    )
}

@Composable
fun SidebarView(
    state: ApplicationState,
    modifier: Modifier = Modifier
) {
    val i18nState = rememberI18nState()
    
    Card(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 标题
            Text(
                text = i18nState.getString("app_name"),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 菜单项目
            SidebarItem.entries.forEach { item ->
                SidebarMenuItem(
                    item = item,
                    state = state,
                    i18nState = i18nState
                )
            }
        }
    }
}

@Composable
private fun SidebarMenuItem(
    item: SidebarItem,
    state: ApplicationState,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    val isSelected = item.isEnabled(state)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                onClick = rememberThrottledClick {
                    logger.info("点击了侧边栏项目: ${item.titleKey}")
                    item.onClick(state)
                }
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = if (isSelected) 4.dp else 0.dp,
        backgroundColor = if (isSelected) 
            MaterialTheme.colors.primary.copy(alpha = 0.1f) 
        else 
            Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(item.iconPath),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) 
                    MaterialTheme.colors.primary 
                else 
                    MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            
            Text(
                text = i18nState.getString(item.titleKey),
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) 
                    MaterialTheme.colors.primary 
                else 
                    MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}