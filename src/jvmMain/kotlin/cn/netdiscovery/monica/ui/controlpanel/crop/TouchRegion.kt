package cn.netdiscovery.monica.ui.controlpanel.crop

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.TouchRegion
 * @author: Tony Shen
 * @date: 2024/5/26 12:17
 * @version: V1.0 <描述当前版本功能>
 */
enum class TouchRegion {
    TopLeft, TopRight, BottomLeft, BottomRight, Inside, None
}

fun handlesTouched(touchRegion: TouchRegion) = touchRegion == TouchRegion.TopLeft ||
        touchRegion == TouchRegion.TopRight ||
        touchRegion == TouchRegion.BottomLeft ||
        touchRegion == TouchRegion.BottomRight