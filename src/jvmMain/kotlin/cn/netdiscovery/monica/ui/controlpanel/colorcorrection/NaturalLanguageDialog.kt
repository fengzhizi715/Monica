package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.llm.DialogSession
import cn.netdiscovery.monica.llm.applyInstructionWithLLM
import kotlinx.coroutines.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorcorrection.NaturalLanguageDialog
 * @author: Tony Shen
 * @date: 2025/8/4 14:00
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun NaturalLanguageDialog(
    visible: Boolean,
    session: DialogSession,
    apiKey: String,
    onDismissRequest: () -> Unit,
    onConfirm: (ColorCorrectionSettings) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("自然语言调色") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("请输入调色指令") },
                        singleLine = false,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (loading) {
                        Spacer(modifier = Modifier.height(10.dp))
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(it, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        loading = true
                        errorMessage = null
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val updated = applyInstructionWithLLM(session, inputText, apiKey)

                                if (updated!=null) {
                                    onConfirm.invoke(updated)
                                    onDismissRequest()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                withContext(Dispatchers.Main) {
                                    errorMessage = "请求失败：" + (e.message ?: "未知错误")
                                }
                            } finally {
                                loading = false
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !loading
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("取消")
                }
            }
        )
    }
}