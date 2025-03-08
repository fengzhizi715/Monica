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
 * 防止 view 的重复点击，默认是 1s 间隔，不同的 view 可以有不同的间隔时间。
 */
@Composable
inline fun composeClick(
    time: Int = VIEW_CLICK_INTERVAL_TIME,
    crossinline filter: () -> Boolean = { true },
    crossinline onClick: Action
): Action {
    var lastClickTime by remember { mutableStateOf(value = 0L) } // 使用remember函数记录上次点击的时间

    return {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime >= time) {          // 判断点击间隔,如果在间隔内则不回调

            if (filter.invoke()) {
                onClick()
            }

            lastClickTime = currentTimeMillis
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
            onClick =  composeClick { // 防止重复点击，1秒内只有1次点击是有效的

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
                  text:String = "确定",
                  modifier:Modifier = Modifier,
                  onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = composeClick {
            onClick.invoke()
        },
        enabled = enabled
    ) {
        Text(text = text,
            color = if (enabled) Color.Unspecified else Color.LightGray)
    }
}