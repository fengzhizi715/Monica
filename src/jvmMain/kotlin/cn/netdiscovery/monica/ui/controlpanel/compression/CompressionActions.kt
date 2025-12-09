package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.centerToast
import cn.netdiscovery.monica.utils.ImageCompressionUtils
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFrame

@Composable
fun CompressionActionButtons(
    viewModel: CompressionViewModel,
    state: ApplicationState,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState
) {
    var showApplyToast by remember { mutableStateOf(false) }
    var showSaveToast by remember { mutableStateOf(false) }
    var saveToastMessage by remember { mutableStateOf("") }
    
    if (showApplyToast) {
        centerToast(
            modifier = Modifier,
            message = i18nState.getString("applied_to_editor")
        ) {
            showApplyToast = false
        }
    }
    
    if (showSaveToast) {
        centerToast(
            modifier = Modifier,
            message = saveToastMessage
        ) {
            showSaveToast = false
        }
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                viewModel.applyCompressedImage(state)
                showApplyToast = true
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
                val fileChooser = JFileChooser()
                fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
                fileChooser.selectedFile = File("compressed.${viewModel.selectedAlgorithm.format}")
                val result = fileChooser.showSaveDialog(JFrame())
                if (result == JFileChooser.APPROVE_OPTION) {
                    val outputFile = fileChooser.selectedFile
                    if (viewModel.compressedImage != null) {
                        val params = viewModel.getCurrentParams()
                        val saveResult = ImageCompressionUtils.compressAndSaveImage(
                            viewModel.compressedImage!!,
                            outputFile,
                            params
                        )
                        if (saveResult != null) {
                            saveToastMessage = i18nState.getString("save_success").format(outputFile.absolutePath)
                        } else {
                            saveToastMessage = i18nState.getString("save_failed")
                        }
                        showSaveToast = true
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


