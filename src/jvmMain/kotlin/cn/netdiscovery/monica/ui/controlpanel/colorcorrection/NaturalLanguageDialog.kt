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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.llm.DialogSession
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.i18n.I18nState
import cn.netdiscovery.monica.llm.applyInstructionWithLLM
import cn.netdiscovery.monica.llm.applyInstructionWithGemini
import cn.netdiscovery.monica.llm.LLMProvider
import cn.netdiscovery.monica.llm.LLMServiceManager
import cn.netdiscovery.monica.llm.rememberLLMServiceManager
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
    deepSeekApiKey: String,
    geminiApiKey: String,
    onDismissRequest: () -> Unit,
    onConfirm: (ColorCorrectionSettings) -> Unit
) {
    val i18nState = rememberI18nState()
    val llmServiceManager = rememberLLMServiceManager()
    var inputText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedProvider by remember { mutableStateOf(LLMProvider.DEEPSEEK) }
    
    // 检查当前选择的提供商是否有 API Key
    val hasApiKey = when (selectedProvider) {
        LLMProvider.DEEPSEEK -> deepSeekApiKey.isNotBlank()
        LLMProvider.GEMINI -> geminiApiKey.isNotBlank()
    }

    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(i18nState.getString("natural_language_color_correction")) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 500.dp)) {

                    // AI 服务提供商选择
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = i18nState.getString("ai_provider_selection") + ": ",
                            fontWeight = FontWeight.Medium
                        )
                        
                        androidx.compose.material.RadioButton(
                            selected = selectedProvider == LLMProvider.DEEPSEEK,
                            onClick = { selectedProvider = LLMProvider.DEEPSEEK }
                        )
                        Text(
                            text = i18nState.getString("ai_provider_deepseek"),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        androidx.compose.material.RadioButton(
                            selected = selectedProvider == LLMProvider.GEMINI,
                            onClick = { selectedProvider = LLMProvider.GEMINI }
                        )
                        Text(text = i18nState.getString("ai_provider_gemini"))
                    }
                    
                    // API Key 状态提示
                    if (!hasApiKey) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️ ",
                                fontSize = 16.sp,
                                color = Color(0xFFFF8C00), // Orange
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = i18nState.getString("api_key_required"),
                                fontSize = 12.sp,
                                color = Color(0xFFFF8C00) // Orange
                            )
                        }
                    }

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
                                val apiKey = when (selectedProvider) {
                                    LLMProvider.DEEPSEEK -> deepSeekApiKey
                                    LLMProvider.GEMINI -> geminiApiKey
                                }
                                
                                // 检查 API Key 是否已配置
                                if (apiKey.isBlank()) {
                                    withContext(Dispatchers.Main) {
                                        errorMessage = when (selectedProvider) {
                                            LLMProvider.DEEPSEEK -> i18nState.getString("deepseek_api_key_missing")
                                            LLMProvider.GEMINI -> i18nState.getString("gemini_api_key_missing")
                                        }
                                    }
                                    return@launch
                                }
                                
                                val updated = llmServiceManager.applyInstructionWithLLM(
                                    provider = selectedProvider,
                                    session = session,
                                    instruction = inputText,
                                    apiKey = apiKey
                                )

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
                    enabled = inputText.isNotBlank() && !loading && hasApiKey
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