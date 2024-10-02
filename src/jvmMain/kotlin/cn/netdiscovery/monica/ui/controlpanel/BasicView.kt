package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import cn.netdiscovery.monica.config.subTitleTextSize
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
import cn.netdiscovery.monica.ui.widget.confirmButton
import cn.netdiscovery.monica.ui.widget.desktopLazyRow
import cn.netdiscovery.monica.ui.widget.toolTipButton
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.BasicView
 * @author: Tony Shen
 * @date: 2024/5/1 00:39
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun basicView(state: ApplicationState) {

    val viewModel: CropViewModel = koinInject()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isBasic, onCheckedChange = {
            state.isBasic = it

            if (!state.isBasic) {
                state.resetCurrentStatus()
                logger.info("取消了基础功能")
            } else {
                logger.info("勾选了基础功能")
            }
        })
        Text("基础功能", color = Color.Black, fontSize = subTitleTextSize)
    }
    desktopLazyRow {
        toolTipButton(text = "图像模糊",
            painter = painterResource("images/controlpanel/blur.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = BlurStatus
            })

        toolTipButton(text = "图像马赛克",
            painter = painterResource("images/controlpanel/mosaic.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = MosaicStatus
            })

        toolTipButton(text = "图像涂鸦",
            painter = painterResource("images/controlpanel/doodle.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = DoodleStatus
                state.togglePreviewWindow(true)
            })

        toolTipButton(text = "图像取色",
            painter = painterResource("images/controlpanel/color-picker.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = ColorPickStatus
                state.togglePreviewWindow(true)
            })

        toolTipButton(text = "图像翻转",
            painter = painterResource("images/controlpanel/flip.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = FlipStatus
                viewModel.flip(state)
            })

        toolTipButton(text = "图像旋转",
            painter = painterResource("images/controlpanel/rotate.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = RotateStatus
                viewModel.rotate(state)
            })

        toolTipButton(text = "图像缩放",
            painter = painterResource("images/controlpanel/resize.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = ResizeStatus
            })


        toolTipButton(text = "图像错切",
            painter = painterResource("images/controlpanel/shearing.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = ShearingStatus
            })

        toolTipButton(text = "图像裁剪",
            painter = painterResource("images/controlpanel/crop.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = CropSizeStatus
                state.togglePreviewWindow(true)
            })
    }

    Column {
        when(state.currentStatus) {
            ResizeStatus -> {
                generateResizeParams(state,viewModel)
            }

            ShearingStatus -> {
                generateShearingParams(state,viewModel)
            }
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

        confirmButton(state.isBasic) {
            viewModel.resize(widthText.toInt(),heightText.toInt(),state)
        }
    }
}


@Composable
fun generateShearingParams(state: ApplicationState,viewModel: CropViewModel) {

    var xText by remember {
        mutableStateOf("${0}")
    }

    var yText by remember {
        mutableStateOf("${0}")
    }

    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "x 方向")

        BasicTextField(
            value = xText,
            onValueChange = { str ->
                xText = str
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
        Text(text = "y 方向")

        BasicTextField(
            value = yText,
            onValueChange = { str ->
                yText = str
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )

        confirmButton(state.isBasic) {
            viewModel.shearing(xText.toFloat(),yText.toFloat(),state)
        }
    }
}