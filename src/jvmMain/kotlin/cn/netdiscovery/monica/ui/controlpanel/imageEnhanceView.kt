package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.imageEnhanceView
 * @author: Tony Shen
 * @date: 2024/7/13 21:08
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun imageEnhanceView(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isEnhance, onCheckedChange = {
            state.isEnhance = it

            if (!state.isEnhance) {
                state.resetCurrentStatus()
                logger.info("取消了图像增强")
            } else {
                logger.info("勾选了图像增强")
            }
        })
        Text("图像增强", color = Color.Black, fontSize = 20.sp)
    }
}