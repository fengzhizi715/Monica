package cn.netdiscovery.monica.llm

import cn.netdiscovery.monica.domain.ColorCorrectionSettings

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.llm.DialogSession
 * @author: Tony Shen
 * @date: 2025/8/1 17:27
 * @version: V1.0 封装一个会话上下文类（保留系统提示 + 当前参数）
 */
data class DialogSession(
    val systemPrompt: String,
    var currentSettings: ColorCorrectionSettings,
//    val history: MutableList<Map<String, String>> = mutableListOf()
    val history: MutableList<Pair<String, ColorCorrectionSettings>> = mutableListOf()
)