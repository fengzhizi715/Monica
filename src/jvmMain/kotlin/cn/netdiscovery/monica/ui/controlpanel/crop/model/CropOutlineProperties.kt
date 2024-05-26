package cn.netdiscovery.monica.ui.controlpanel.crop.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.model.CropOutlineProperties
 * @author: Tony Shen
 * @date: 2024/5/26 12:23
 * @version: V1.0 <描述当前版本功能>
 */
@Immutable
data class CornerRadiusProperties(
    val topStartPercent: Int = 20,
    val topEndPercent: Int = 20,
    val bottomStartPercent: Int = 20,
    val bottomEndPercent: Int = 20
)

@Immutable
data class PolygonProperties(
    val sides: Int = 6,
    val angle: Float = 0f,
    val offset: Offset = Offset.Zero
)

@Immutable
data class OvalProperties(
    val startAngle: Float = 0f,
    val sweepAngle: Float = 360f,
    val offset: Offset = Offset.Zero
)