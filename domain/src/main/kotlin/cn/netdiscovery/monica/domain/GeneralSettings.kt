package cn.netdiscovery.monica.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.domain.GeneralSettings
 * @author: Tony Shen
 * @date: 2025/2/7 10:27
 * @version: V1.0 <描述当前版本功能>
 */
data class GeneralSettings(
    var outputBoxR: Int,
    var outputBoxG: Int,
    var outputBoxB: Int,
    var size: Int,
    var algorithmUrl: String
)