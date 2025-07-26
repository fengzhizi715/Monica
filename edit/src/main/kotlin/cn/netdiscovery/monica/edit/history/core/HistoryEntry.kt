package cn.netdiscovery.monica.edit.history.core

import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.edit.history.core.HistoryEntry
 * @author: Tony Shen
 * @date:  2025/7/26 10:21
 * @version: V1.0 <描述当前版本功能>
 */
data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val module: String,
    val operation: String,
    val parameters: Map<String, Any>,
    val previewImagePath: String = "",
    val sourceImageHash: String = "",
    val description: String = ""
)