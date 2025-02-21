package cn.netdiscovery.monica.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MatchTemplateSettings
 * @author: Tony Shen
 * @date: 2025/1/4 20:29
 * @version: V1.0 <描述当前版本功能>
 */
data class MatchTemplateSettings (
    var matchType:Int = 0,                   // 0 表示原图匹配，1 表示灰度匹配 2 表示边缘匹配
    var angleStart:Int = 0,
    var angleEnd:Int = 360,
    var angleStep:Int = 10,
    var scaleStart:Double = 0.0,
    var scaleEnd:Double = 1.0,
    var scaleStep:Double = 0.1,
    var matchTemplateThreshold:Double = 0.8, // 模版匹配的阈值
    var scoreThreshold: Float = 0.6f ,       // 置信分数的阈值(nms 相关)
    var nmsThreshold: Float = 0.3f           // 非极大值抑制的阈值(nms 相关)
)