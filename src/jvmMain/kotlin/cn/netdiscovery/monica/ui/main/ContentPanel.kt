package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.*
import cn.netdiscovery.monica.ui.controlpanel.ai.aiView
import cn.netdiscovery.monica.ui.i18n.rememberI18nState

/**
 * 内容面板组件 - 显示选中模块的详细功能
 * @author: Tony Shen
 * @date: 2025/9/8
 * @version: V1.0
 */
@Composable
fun ContentPanel(
    state: ApplicationState,
    modifier: Modifier = Modifier
) {
    val i18nState = rememberI18nState()
    
    Card(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.isGeneralSettings -> {
                    Text(
                        text = i18nState.getString("general_settings"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    generalSettingsView(state)
                }

                state.isBasic -> {
                    Text(
                        text = i18nState.getString("basic_functions"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    basicView(state)
                }
                
                state.isColorCorrection -> {
                    Text(
                        text = i18nState.getString("image_color_correction"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    colorCorrectionView(state)
                }

                state.isFilter -> {
                    Text(
                        text = i18nState.getString("filter_effects"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    filterView(state)
                }
                
                state.isAI -> {
                    Text(
                        text = i18nState.getString("ai_laboratory"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    aiView(state)
                }
            }
        }
    }
}
