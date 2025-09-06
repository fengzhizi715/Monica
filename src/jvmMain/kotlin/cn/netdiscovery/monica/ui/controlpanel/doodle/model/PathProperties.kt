package cn.netdiscovery.monica.ui.controlpanel.doodle.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.doodle.model.PathProperties
 * @author: Tony Shen
 * @date: 2024/6/14 15:57
 * @version: V1.0 <描述当前版本功能>
 */
data class PathProperties(
    val strokeWidth: Float = 10f,
    val color: Color = Color.Black,
    val alpha: Float = 1f,
    val strokeCap: StrokeCap = StrokeCap.Round,
    val strokeJoin: StrokeJoin = StrokeJoin.Round
)