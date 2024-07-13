package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.runtime.Composable
import cn.netdiscovery.monica.state.ApplicationState
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ImageEnhanceView
 * @author: Tony Shen
 * @date: 2024/7/13 21:22
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun imageEnhanceView(state: ApplicationState) {

//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Checkbox(state.isEnhance, onCheckedChange = {
//            state.isEnhance = it
//
//            if (!state.isEnhance) {
//                state.resetCurrentStatus()
//                logger.info("取消了图像增强")
//            } else {
//                logger.info("勾选了图像增强")
//            }
//        })
//        Text("图像增强", color = Color.Black, fontSize = 20.sp)
//    }
//    Row (
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        toolTipButton(text = "图像模糊",
//            painter = painterResource("images/controlpanel/blur.png"),
//            enable = { state.isBasic },
//            onClick = {
//                state.currentStatus = BlurStatus
//            })
//    }
}