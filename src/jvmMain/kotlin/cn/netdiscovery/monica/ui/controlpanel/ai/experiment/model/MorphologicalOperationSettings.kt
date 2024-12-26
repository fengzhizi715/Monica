package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MorphologicalOperationSettings
 * @author: Tony Shen
 * @date: 2024/12/26 20:42
 * @version: V1.0 <描述当前版本功能>
 */
data class MorphologicalOperationSettings(
    var op:Int,
    var shape:Int,
    var width:Int,
    var height:Int
)