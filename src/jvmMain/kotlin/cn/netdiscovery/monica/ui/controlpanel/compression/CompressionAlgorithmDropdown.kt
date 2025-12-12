package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.utils.CompressionAlgorithm

@Composable
fun CompressionAlgorithmDropdown(
    viewModel: CompressionViewModel,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(
            text = i18nState.getString("compression_algorithm"),
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = MaterialTheme.colors.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.selectedAlgorithm.displayName,
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = "▼",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                CompressionAlgorithm.entries.forEach { algorithm ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.selectedAlgorithm = algorithm
                            expanded = false
                        }
                    ) {
                        Text(algorithm.displayName)
                    }
                }
            }
        }
    }
}





