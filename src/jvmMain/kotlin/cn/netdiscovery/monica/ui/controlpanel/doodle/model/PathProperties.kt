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
class PathProperties(
    var strokeWidth: Float = 10f,
    var color: Color = Color.Black,
    var alpha: Float = 1f,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
    var eraseMode: Boolean = false
)