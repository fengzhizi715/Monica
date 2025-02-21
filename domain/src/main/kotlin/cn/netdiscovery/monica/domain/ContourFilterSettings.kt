package cn.netdiscovery.monica.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.ContourFilterSettings
 * @author: Tony Shen
 * @date: 2024/10/29 17:52
 * @version: V1.0 <描述当前版本功能>
 */
data class ContourFilterSettings (
    var minPerimeter:Double = 0.0,
    var maxPerimeter:Double = 0.0,

    var minArea:Double = 0.0,
    var maxArea:Double = 0.0,

    var minRoundness:Double = 0.0,
    var maxRoundness:Double = 0.0,

    var minAspectRatio:Double = 0.0,
    var maxAspectRatio:Double = 0.0
)