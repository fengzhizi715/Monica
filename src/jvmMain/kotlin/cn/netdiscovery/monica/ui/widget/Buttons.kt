package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.utils.Action
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import cn.netdiscovery.monica.i18n.LocalizationManager

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.Buttons
 * @author: Tony Shen
 * @date: 2024/5/11 10:46
 * @version: V1.0 <描述当前版本功能>
 */
val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

const val VIEW_CLICK_INTERVAL_TIME = 1000 // View 的 click 方法的两次点击间隔时间

/**
 * 可复用的点击节流函数，支持状态隔离、高精度时间、加载状态拦截与过滤函数。
 */
@Composable
fun rememberThrottledClick(
    intervalMs: Int = VIEW_CLICK_INTERVAL_TIME,
    isLoading: Boolean = false,
    filter: () -> Boolean = { true },
    onClick: Action
): Action {
    // 使用 nanoTime，避免 system time 被修改时导致节流失效
    var lastClickNanoTime by remember { mutableStateOf(0L) }
    val intervalNs = intervalMs * 1_000_000L

    return {
        val now = System.nanoTime()
        val elapsed = now - lastClickNanoTime

        if (elapsed >= intervalNs && !isLoading && filter()) {
            lastClickNanoTime = now
            onClick()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun toolTipButton(
    text:String,
    painter: Painter,
    buttonModifier: Modifier = Modifier.padding(5.dp),
    iconModifier: Modifier = Modifier.size(36.dp),
    enable: ()-> Boolean = { true },
    onClick: Action,
) {
    TooltipArea(
        tooltip = {
            // composable tooltip content
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = Color(255, 255, 210),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(10.dp)
                )
            }
        },
        delayMillis = 600, // in milliseconds
        tooltipPlacement = TooltipPlacement.CursorPoint(
            alignment = Alignment.BottomEnd,
            offset = DpOffset((-16).dp, 0.dp)
        )
    ) {
        IconButton(
            modifier = buttonModifier,
            onClick =  rememberThrottledClick { // 防止重复点击，1秒内只有1次点击是有效的

                logger.info("点击了 $text 按钮")
                onClick()
            },
            enabled = enable()
        ) {
            Icon(
                painter = painter,
                contentDescription = text,
                modifier = iconModifier
            )
        }
    }
}

@Composable
fun confirmButton(enabled:Boolean,
                  text:String = LocalizationManager.getString("confirm"),
                  modifier:Modifier = Modifier,
                  onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = rememberThrottledClick {
            onClick.invoke()
        },
        enabled = enabled
    ) {
        Text(text = text,
            color = if (enabled) Color.Unspecified else Color.LightGray)
    }
}