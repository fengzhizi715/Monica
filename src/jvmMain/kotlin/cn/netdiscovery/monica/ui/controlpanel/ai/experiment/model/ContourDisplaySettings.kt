package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.ContourDisplaySettings
 * @author: Tony Shen
 * @date: 2024/10/29 14:26
 * @version: V1.0 <描述当前版本功能>
 */
data class ContourDisplaySettings(
    var showOriginalImage: Boolean = false,
    var showBoundingRect: Boolean = false,
    var showMinAreaRect: Boolean = false,
    var showCenter: Boolean = false
)