package cn.netdiscovery.monica.ui.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.utils.Action
import cn.netdiscovery.monica.utils.composeClick
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
                  modifier:Modifier = Modifier,
                  onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = composeClick {
            onClick.invoke()
        },
        enabled = enabled
    ) {
        Text(text = "确定",
            color = if (enabled) Color.Unspecified else Color.LightGray)
    }
}