package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.config.subTitleTextSize
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.state.ColorCorrectionStatus
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.widget.checkBoxWithTitle
import cn.netdiscovery.monica.ui.widget.rememberThrottledClick
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ColorCorrectionView
 * @author: Tony Shen
 * @date: 2024/5/1 00:43
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun colorCorrectionView(state: ApplicationState) {
    val i18nState = rememberI18nState()

    checkBoxWithTitle(
        text = i18nState.getString("image_color_correction"),
        color = Color.Black,
        checked = state.isColorCorrection,
        fontSize = subTitleTextSize,
        onCheckedChange = {
            state.isColorCorrection = it

            if (!state.isColorCorrection) {
                state.resetCurrentStatus()
                logger.info("取消了图像调色")
            } else {
                logger.info("勾选了图像调色")

                state.isGeneralSettings = false
                state.isBasic = false
                state.isFilter = false
                state.isAI = false
            }
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.align(Alignment.End).padding(start = 15.dp),
            enabled = state.isColorCorrection,
            onClick = rememberThrottledClick {
                state.togglePreviewWindowAndUpdateStatus(ColorCorrectionStatus)
            }
        ) {
            Text(text = i18nState.getString("enter_image_color_correction"), color = Color.Unspecified)
        }
    }
}