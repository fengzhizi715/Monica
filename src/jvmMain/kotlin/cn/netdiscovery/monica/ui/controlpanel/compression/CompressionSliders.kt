package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.ui.i18n.I18nState

@Composable
fun QualitySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = i18nState.getString("quality_setting"),
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0", style = MaterialTheme.typography.caption)
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
fun CompressionLevelSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    // 使用浮点值保持拖动时的平滑性
    var sliderValue by remember(value) { mutableStateOf(value.toFloat()) }
    
    // 当外部值改变时同步更新滑块值
    LaunchedEffect(value) {
        sliderValue = value.toFloat()
    }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = i18nState.getString("compression_level"),
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${sliderValue.toInt()}/9",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
        }
        
        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                // 拖动过程中保持浮点值，确保平滑
                sliderValue = newValue.coerceIn(0f, 9f)
            },
            onValueChangeFinished = {
                // 拖动结束时才转换为整数并通知外部
                onValueChange(sliderValue.toInt().coerceIn(0, 9))
            },
            valueRange = 0f..9f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
    }
}

