package cn.netdiscovery.monica.ui.controlpanel

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.config.subTitleTextSize
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
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
    val i18nState = rememberI18nState()
    val viewModel: PreviewViewModel = koinInject()

    checkBoxWithTitle(
        text = i18nState.getString("basic_functions"),
        color = Color.Black,
        checked = state.isBasic,
        fontSize = subTitleTextSize,
        onCheckedChange = {
            state.isBasic = it

            if (!state.isBasic) {
                state.resetCurrentStatus()
                logger.info(i18nState.getString("basic_function_cancelled"))
            } else {
                logger.info(i18nState.getString("basic_function_selected"))

                state.isGeneralSettings = false
                state.isColorCorrection = false
                state.isFilter = false
                state.isAI = false
            }
        }
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        toolTipButton(text = i18nState.getString("image_blur"),
            painter = painterResource("images/controlpanel/blur.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = BlurStatus
            })

        toolTipButton(text = i18nState.getString("image_mosaic"),
            painter = painterResource("images/controlpanel/mosaic.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = MosaicStatus
            })

        toolTipButton(text = i18nState.getString("image_doodle"),
            painter = painterResource("images/controlpanel/doodle.png"),
            enable = { state.isBasic },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(DoodleStatus)
            })

        toolTipButton(text = i18nState.getString("shape_drawing"),
            painter = painterResource("images/controlpanel/shape-drawing.png"),
            enable = { state.isBasic },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(ShapeDrawingStatus)
            })

        toolTipButton(text = i18nState.getString("color_picker"),
            painter = painterResource("images/controlpanel/color-picker.png"),
            enable = { state.isBasic },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(ColorPickStatus)
            })

        toolTipButton(text = i18nState.getString("generate_gif"),
            painter = painterResource("images/controlpanel/gif.png"),
            enable = { state.isBasic },
            onClick = {
                state.togglePreviewWindowAndUpdateStatus(GenerateGifStatus)
            })
    }

    Row(verticalAlignment = Alignment.CenterVertically) {

                        toolTipButton(text = i18nState.getString("image_flip"),
            painter = painterResource("images/controlpanel/flip.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = FlipStatus
                viewModel.flip(state)
            })

                        toolTipButton(text = i18nState.getString("image_rotate"),
            painter = painterResource("images/controlpanel/rotate.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = RotateStatus
                viewModel.rotate(state)
            })

                        toolTipButton(text = i18nState.getString("image_scale"),
            painter = painterResource("images/controlpanel/resize.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = ResizeStatus
            })

                        toolTipButton(text = i18nState.getString("image_shear"),
            painter = painterResource("images/controlpanel/shearing.png"),
            enable = { state.isBasic },
            onClick = {
                state.currentStatus = ShearingStatus
            })

                        toolTipButton(text = i18nState.getString("image_crop"),
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
    val i18nState = rememberI18nState()

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

                            val width = getValidateField(block = { widthText.toInt() } , failed = { showCenterToast(i18nState.getString("width_needs_int")) }) ?: return@confirmButton
                val height = getValidateField(block = { heightText.toInt() } , failed = { showCenterToast(i18nState.getString("height_needs_int")) }) ?: return@confirmButton
            viewModel.resize(width, height, state)
        }
    }
}


@Composable
private fun generateShearingParams(state: ApplicationState, viewModel: PreviewViewModel) {
    val i18nState = rememberI18nState()

    var xText by remember {
        mutableStateOf("${0}")
    }

    var yText by remember {
        mutableStateOf("${0}")
    }

    Row {
        basicTextFieldWithTitle(titleText = i18nState.getString("x_direction"), xText, Modifier.padding(top = 5.dp)) { str ->
            xText = str
        }

        basicTextFieldWithTitle(titleText = i18nState.getString("y_direction"), yText, Modifier.padding(top = 5.dp)) { str ->
            yText = str
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        confirmButton(state.isBasic) {

                            val x = getValidateField(block = { xText.toFloat() } , failed = { showCenterToast(i18nState.getString("x_direction_needs_float")) }) ?: return@confirmButton
                val y = getValidateField(block = { yText.toFloat() } , failed = { showCenterToast(i18nState.getString("y_direction_needs_float")) }) ?: return@confirmButton
            viewModel.shearing(x, y, state)
        }
    }
}