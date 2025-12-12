package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.ui.i18n.I18nState

/**
 * 滤镜模块顶部应用栏
 */
@Composable
fun FilterTopAppBar(
    onSave: () -> Unit,
    onExport: () -> Unit,
    i18nState: I18nState
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = 2.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧标题和菜单
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = i18nState.getString("image_editor_filter_module"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
//
//                // 菜单项（简化版，仅显示文本）
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        text = "File",
//                        fontSize = 14.sp,
//                        color = Color(0xFF222222)
//                    )
//                    Text(
//                        text = "Edit",
//                        fontSize = 14.sp,
//                        color = Color(0xFF222222)
//                    )
//                    Text(
//                        text = "View",
//                        fontSize = 14.sp,
//                        color = Color(0xFF222222)
//                    )
//                    Text(
//                        text = "Help",
//                        fontSize = 14.sp,
//                        color = Color(0xFF222222)
//                    )
//                }
            }
            
            // 右侧按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save 按钮（次要按钮）
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFE0E0E0),
                        contentColor = Color(0xFF222222)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = i18nState.getString("save"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Export 按钮（主要按钮）
                Button(
                    onClick = onExport,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF007AFF),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = i18nState.getString("export"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}