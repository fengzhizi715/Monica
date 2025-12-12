package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.ImageCompressionUtils
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFrame

@Composable
fun CompressionActionButtons(
    viewModel: CompressionViewModel,
    state: ApplicationState,
    onShowToast: (String) -> Unit,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                viewModel.applyCompressedImage(state)
                onShowToast(i18nState.getString("applied_to_editor"))
            },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Text(
                i18nState.getString("apply_to_editor"),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = {
                // 撤销：优先撤销“应用到编辑器”（如果有），同时重置参数并清理压缩结果，回到原图预览
                val ok = viewModel.undoApplied(state)
                viewModel.resetAll()
                onShowToast(
                    if (ok) i18nState.getString("undo_and_reset_success")
                    else i18nState.getString("reset_done")
                )
            },
            modifier = Modifier
                .width(92.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.7f)
            )
        ) {
            Text(
                i18nState.getString("undo"),
                color = Color.White
            )
        }
        
        Button(
            onClick = {
                val fileChooser = JFileChooser()
                fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
                fileChooser.selectedFile = File("compressed.${viewModel.selectedAlgorithm.format}")
                val result = fileChooser.showSaveDialog(JFrame())
                if (result == JFileChooser.APPROVE_OPTION) {
                    val outputFile = fileChooser.selectedFile
                    val saveResult = viewModel.saveLastCompressedToFile(outputFile)
                    if (saveResult != null) {
                        onShowToast(i18nState.getString("save_success").format(saveResult.outputFile.absolutePath))
                    } else {
                        onShowToast(i18nState.getString("save_failed"))
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.7f)
            )
        ) {
            Text(
                i18nState.getString("save"),
                color = Color.White
            )
        }
    }
}


