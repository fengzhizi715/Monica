package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MatchTemplateSettings
 * @author: Tony Shen
 * @date: 2025/1/4 20:29
 * @version: V1.0 <描述当前版本功能>
 */
data class MatchTemplateSettings (
    var matchType:Int = 0, // 0 表示原图匹配，1 表示灰度匹配 2 表示边缘匹配
    var angleStart:Int,
    var angleEnd:Int,
    var angleStep:Int
)