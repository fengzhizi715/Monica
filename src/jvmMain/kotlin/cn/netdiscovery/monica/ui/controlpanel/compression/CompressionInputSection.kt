package cn.netdiscovery.monica.ui.controlpanel.compression

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFrame

@Composable
fun CompressionInputSection(
    compressionMode: CompressionMode,
    onModeChange: (CompressionMode) -> Unit,
    selectedOutputDir: File?,
    onOutputDirSelected: (File) -> Unit,
    viewModel: CompressionViewModel,
    state: ApplicationState,
    i18nState: cn.netdiscovery.monica.ui.i18n.I18nState,
    onShowToast: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = i18nState.getString("input_selection"),
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onModeChange(CompressionMode.SINGLE) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (compressionMode == CompressionMode.SINGLE)
                        MaterialTheme.colors.primary
                    else
                        MaterialTheme.colors.surface
                )
            ) {
                Icon(
                    painter = painterResource("images/controlpanel/compress.png"),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(i18nState.getString("single_image"))
            }
            
            Button(
                onClick = { onModeChange(CompressionMode.BATCH) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (compressionMode == CompressionMode.BATCH)
                        MaterialTheme.colors.primary
                    else
                        MaterialTheme.colors.surface
                )
            ) {
                Icon(
                    painter = painterResource("images/controlpanel/compress.png"),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(i18nState.getString("batch_folder"))
            }
        }
        
        if (compressionMode == CompressionMode.SINGLE) {
            // 单张图模式：只显示"开始压缩"按钮
            Button(
                onClick = {
                    if (viewModel.selectedImage == null) {
                        onShowToast(i18nState.getString("please_select_image_in_preview"))
                        return@Button
                    }
                    
                    // 开始压缩（不选择保存位置，压缩后显示在右侧预览区）
                    viewModel.compressSingleImageToPreview(state.scope) { i18nState.getString(it) }
                },
                enabled = !viewModel.isCompressing && viewModel.selectedImage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(
                    i18nState.getString("start_compression"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Reset：重置参数 + 清掉压缩结果（恢复到原图预览）
            OutlinedButton(
                onClick = { viewModel.resetAll() },
                enabled = !viewModel.isCompressing && (!viewModel.isAtDefaultParams() || viewModel.showResult),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text(i18nState.getString("reset"))
            }
        } else {
            Button(
                onClick = {
                    val fileChooser = JFileChooser()
                    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    val result = fileChooser.showOpenDialog(JFrame())
                    if (result == JFileChooser.APPROVE_OPTION) {
                        onOutputDirSelected(fileChooser.selectedFile)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    if (selectedOutputDir == null)
                        i18nState.getString("select_input_folder")
                    else
                        "${i18nState.getString("selected")}: ${selectedOutputDir!!.name}",
                    color = Color.White
                )
            }
            
            if (selectedOutputDir != null) {
                Button(
                    onClick = {
                        val fileChooser = JFileChooser()
                        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                        fileChooser.dialogTitle = i18nState.getString("select_output_folder")
                        val result = fileChooser.showOpenDialog(JFrame())
                        if (result == JFileChooser.APPROVE_OPTION) {
                            val outputDir = fileChooser.selectedFile
                            
                            // 检查输出文件夹中是否已有文件
                            val existingFiles = outputDir.listFiles()?.filter { it.isFile }?.size ?: 0
                            if (existingFiles > 0) {
                                val confirmResult = javax.swing.JOptionPane.showConfirmDialog(
                                    null,
                                    i18nState.getString("output_folder_has_files").format(existingFiles),
                                    i18nState.getString("batch_compression_warning"),
                                    javax.swing.JOptionPane.YES_NO_OPTION,
                                    javax.swing.JOptionPane.WARNING_MESSAGE
                                )
                                if (confirmResult != javax.swing.JOptionPane.YES_OPTION) {
                                    return@Button
                                }
                            }
                            
                            viewModel.compressBatch(selectedOutputDir!!, outputDir, state.scope) { i18nState.getString(it) }
                        }
                    },
                    enabled = !viewModel.isCompressing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(
                        i18nState.getString("start_batch_compression"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

