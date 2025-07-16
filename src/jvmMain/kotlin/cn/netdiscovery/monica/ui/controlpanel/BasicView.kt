package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.config.subTitleTextSize
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.getValidateField
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import showCenterToast

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

    val viewModel: PreviewViewModel = koinInject()

    checkBoxWithTitle(
        text = "基础功能",
        color = Color.Black,
        checked = state.isBasic,
        fontSize = subTitleTextSize,
        onCheckedChange = {
            state.isBasic = it

            if (!state.isBasic) {
                state.resetCurrentStatus()
                logger.info("取消了基础功能")
            } else {
                logger.info("勾选了基础功能")

                state.isGeneralSettings = false
                state.isColorCorrection = false
                state.isFilter = false
                state.isAI = false
            }
        }
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
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
                state.togglePreviewWindowAndUpdateStatus(DoodleStatus)
            })

        toolTipButton(text = "形状绘制",
            painter = painterResource("images/controlpanel/shape-drawing.png"),
            enable = { state.isBasic },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(ShapeDrawingStatus)
            })

        toolTipButton(text = "图像取色",
            painter = painterResource("images/controlpanel/color-picker.png"),
            enable = { state.isBasic },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(ColorPickStatus)
            })

        toolTipButton(text = "生成gif",
            painter = painterResource("images/controlpanel/gif.png"),
            enable = { state.isBasic },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(GenerateGifStatus)
            })
    }

    Row(verticalAlignment = Alignment.CenterVertically) {

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
                state.togglePreviewWindowAndUpdateStatus(CropSizeStatus)
            })
    }

    Column {
        when(state.currentStatus) {
            ResizeStatus   -> generateResizeParams(state,viewModel)

            ShearingStatus -> generateShearingParams(state,viewModel)
        }
    }
}


@Composable
private fun generateResizeParams(state: ApplicationState, viewModel: PreviewViewModel) {

    var widthText by remember {
        mutableStateOf("${state.currentImage?.width?:400}")
    }

    var heightText by remember {
        mutableStateOf("${state.currentImage?.height?:400}")
    }

    Row {
        basicTextFieldWithTitle(titleText = "width", widthText, Modifier.padding(top = 5.dp)) { str ->
            widthText = str
        }

        basicTextFieldWithTitle(titleText = "height", heightText, Modifier.padding(top = 5.dp)) { str ->
            heightText = str
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        confirmButton(state.isBasic) {

            val width = getValidateField(block = { widthText.toInt() } , failed = { showCenterToast("width 需要 int 类型") }) ?: return@confirmButton
            val height = getValidateField(block = { heightText.toInt() } , failed = { showCenterToast("height 需要 int 类型") }) ?: return@confirmButton
            viewModel.resize(width, height, state)
        }
    }
}


@Composable
private fun generateShearingParams(state: ApplicationState, viewModel: PreviewViewModel) {

    var xText by remember {
        mutableStateOf("${0}")
    }

    var yText by remember {
        mutableStateOf("${0}")
    }

    Row {
        basicTextFieldWithTitle(titleText = "x 方向", xText, Modifier.padding(top = 5.dp)) { str ->
            xText = str
        }

        basicTextFieldWithTitle(titleText = "y 方向", yText, Modifier.padding(top = 5.dp)) { str ->
            yText = str
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        confirmButton(state.isBasic) {

            val x = getValidateField(block = { xText.toFloat() } , failed = { showCenterToast("x 方向 需要 float 类型") }) ?: return@confirmButton
            val y = getValidateField(block = { yText.toFloat() } , failed = { showCenterToast("y 方向 需要 float 类型") }) ?: return@confirmButton
            viewModel.shearing(x, y, state)
        }
    }
}