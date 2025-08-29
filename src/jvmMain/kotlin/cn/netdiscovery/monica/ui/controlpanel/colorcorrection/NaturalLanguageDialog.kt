package cn.netdiscovery.monica.ui.controlpanel.colorcorrection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.llm.DialogSession
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.i18n.I18nState
import cn.netdiscovery.monica.llm.applyInstructionWithLLM
import cn.netdiscovery.monica.ui.widget.divider
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
    val i18nState = rememberI18nState()
    var inputText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(i18nState.getString("natural_language_color_correction")) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 500.dp)) {

                    // 上下文对话记录区
                    if (session.history.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            items(session.history) { (userText, response) ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("👤 $userText", fontWeight = FontWeight.Bold)
                                    Text(i18nState.getString("update_parameters") + formatSettingsDiff(response, i18nState), fontSize = 13.sp)
                                }
                            }
                        }
                        divider()
                    }

                    // 输入框
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text(i18nState.getString("enter_color_instruction")) },
                        singleLine = false,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 进度与错误提示
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
                                    inputText = ""
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                withContext(Dispatchers.Main) {
                                    errorMessage = i18nState.getString("request_failed") + (e.message ?: i18nState.getString("unknown_error"))
                                }
                            } finally {
                                loading = false
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !loading
                ) {
                    Text(i18nState.getString("confirm"))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(i18nState.getString("cancel"))
                }
            }
        )
    }
}

@Composable
private fun formatSettingsDiff(settings: ColorCorrectionSettings, i18nState: I18nState): String {
    val list = mutableListOf<String>()
    if (settings.status == 1) list.add("${i18nState.getString("contrast")} → ${settings.contrast}")
    if (settings.status == 2) list.add("${i18nState.getString("hue")} → ${settings.hue}")
    if (settings.status == 3) list.add("${i18nState.getString("saturation")} → ${settings.saturation}")
    if (settings.status == 4) list.add("${i18nState.getString("lightness")} → ${settings.lightness}")
    if (settings.status == 5) list.add("${i18nState.getString("temperature")} → ${settings.temperature}")
    if (settings.status == 6) list.add("${i18nState.getString("highlight")} → ${settings.highlight}")
    if (settings.status == 7) list.add("${i18nState.getString("shadow")} → ${settings.shadow}")
    if (settings.status == 8) list.add("${i18nState.getString("sharpen")} → ${settings.sharpen}")
    if (settings.status == 9) list.add("${i18nState.getString("corner")} → ${settings.corner}")
    return if (list.isEmpty()) i18nState.getString("no_significant_changes") else list.joinToString()
}