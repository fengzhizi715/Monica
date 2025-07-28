package cn.netdiscovery.monica.history

import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.history.HistoryEntry
 * @author: Tony Shen
 * @date:  2025/7/26 10:21
 * @version: V1.0 记录对象
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