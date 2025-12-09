package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompressionProgressSection(
    viewModel: CompressionViewModel,
    onCancel: () -> Unit,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 压缩消息和取消按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 压缩消息
            if (viewModel.compressionMessage.isNotEmpty()) {
                Text(
                    text = viewModel.compressionMessage,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1f),
                    color = when {
                        viewModel.compressionMessage.contains(i18nState.getString("compression_success").replace("！", "")) || 
                        viewModel.compressionMessage.contains(i18nState.getString("batch_compression_completed").split("（")[0]) -> MaterialTheme.colors.primary
                        viewModel.compressionMessage.contains(i18nState.getString("compression_failed")) || 
                        viewModel.compressionMessage.contains(i18nState.getString("compression_error").split(":")[0]) ||
                        viewModel.compressionMessage.contains(i18nState.getString("compression_cancelled")) -> MaterialTheme.colors.error
                        else -> MaterialTheme.colors.onSurface
                    },
                    fontWeight = FontWeight.Medium
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // 取消按钮（仅在压缩中显示）
            if (viewModel.isCompressing) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    )
                ) {
                    Text(
                        i18nState.getString("cancel"),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        // 进度条
        if (viewModel.isCompressing) {
            LinearProgressIndicator(
                progress = viewModel.compressionProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colors.primary
            )
            
            Text(
                text = "${(viewModel.compressionProgress * 100).toInt()}%",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


