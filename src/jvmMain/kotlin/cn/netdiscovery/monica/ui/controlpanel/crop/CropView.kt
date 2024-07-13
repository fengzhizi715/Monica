package cn.netdiscovery.monica.ui.controlpanel.crop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.click
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.CropView
 * @author: Tony Shen
 * @date: 2024/5/7 13:56
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun cropView(state: ApplicationState) {
    val viewModel: CropViewModel = koinInject()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isCrop, onCheckedChange = {
            state.isCrop = it

            if (!state.isCrop) {
                state.resetCurrentStatus()
                logger.info("取消了裁剪")
            } else {
                logger.info("勾选了裁剪")
            }
        })
        Text("裁剪", color = Color.Black, fontSize = 20.sp)
    }

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = "图像翻转",
            painter = painterResource("images/controlpanel/flip.png"),
            enable = { state.isCrop },
            onClick = {
                state.currentStatus = FlipStatus
                viewModel.flip(state)
            })

        toolTipButton(text = "图像旋转",
            painter = painterResource("images/controlpanel/rotate.png"),
            enable = { state.isCrop },
            onClick = {
                state.currentStatus = RotateStatus
                viewModel.rotate(state)
            })

        toolTipButton(text = "图像缩放",
            painter = painterResource("images/controlpanel/resize.png"),
            enable = { state.isCrop },
            onClick = {
                state.currentStatus = ResizeStatus
            })

        toolTipButton(text = "图像裁剪",
            painter = painterResource("images/controlpanel/crop.png"),
            enable = { state.isCrop },
            onClick = {
                state.currentStatus = CropSizeStatus
                state.togglePreviewWindow(true)
            })
    }

    Column {
        if (state.currentStatus == ResizeStatus && state.currentImage!=null) {
            generateResizeParams(state,viewModel)
        }
    }
}

@Composable
fun generateResizeParams(state: ApplicationState,viewModel: CropViewModel) {

    var widthText by remember {
        mutableStateOf("${state.currentImage?.width?:400}")
    }

    var heightText by remember {
        mutableStateOf("${state.currentImage?.height?:400}")
    }

    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "width")

        BasicTextField(
            value = widthText,
            onValueChange = { str ->
                widthText = str
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )
    }

    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "height")

        BasicTextField(
            value = heightText,
            onValueChange = { str ->
                heightText = str
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )

        Row {
            Button(
                modifier = Modifier.offset(x = 140.dp,y = 0.dp),
                onClick = {
                    click {
                        viewModel.resize(widthText.toInt(),heightText.toInt(),state)
                    }
                },
                enabled = state.isCrop
            ) {
                Text(text = "确定",
                color = if (state.isCrop) Color.Unspecified else Color.LightGray)
            }
        }
    }
}
