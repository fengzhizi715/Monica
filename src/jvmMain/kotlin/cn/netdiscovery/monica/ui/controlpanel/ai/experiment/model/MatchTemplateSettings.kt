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
    var angleStep:Int,
    var scaleStart:Double,
    var scaleEnd:Double,
    var scaleStep:Double,
    var matchTemplateThreshold:Double, // 模版匹配的阈值
    var scoreThreshold: Float,         // 置信分数的阈值(nms 相关)
    var nmsThreshold: Float            // 非极大值抑制的阈值(nms 相关)
)