package cn.netdiscovery.monica.ui.controlpanel.enhance

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
import cn.netdiscovery.monica.ui.widget.confirmButton
import cn.netdiscovery.monica.ui.widget.subTitle
import cn.netdiscovery.monica.ui.widget.toolTipButton
import org.koin.compose.koinInject
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

    val viewModel: ImageEnhanceViewModel = koinInject()

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
        subTitle(text = "图像增强", color = Color.Black)
    }
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = "直方图均衡化",
            painter = painterResource("images/imageenhance/histogram.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = EqualizeHistStatus

                viewModel.equalizeHist(state)
            })

        toolTipButton(text = "限制对比度自适应直方图均衡(clahe)",
            painter = painterResource("images/imageenhance/clahe.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = ClaheStatus
            })

        toolTipButton(text = "gamma 变换",
            painter = painterResource("images/imageenhance/gamma.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = GammaStatus
            })

        toolTipButton(text = "Laplace 锐化",
            painter = painterResource("images/imageenhance/laplace.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = LaplaceStatus

                viewModel.laplace(state)
            })

        toolTipButton(text = "USM 锐化",
            painter = painterResource("images/imageenhance/usm.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = USMStatus
            })

        toolTipButton(text = "自动色彩均衡",
            painter = painterResource("images/imageenhance/ace.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = ACEStatus
            })
    }

    Column {
        when(state.currentStatus) {
            ClaheStatus -> {
                generateClaheParams(state,viewModel)
            }

            GammaStatus -> {
                generateGammaParams(state,viewModel)
            }

            USMStatus -> {
                generateUSMParams(state,viewModel)
            }

            ACEStatus -> {
                generateACEParams(state,viewModel)
            }
        }
    }
}

@Composable
private fun generateClaheParams(state: ApplicationState, viewModel: ImageEnhanceViewModel) {

    var clipLimitText by remember {
        mutableStateOf("4")
    }

    var sizeText by remember {
        mutableStateOf("10")
    }

    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "clipLimit")

        BasicTextField(
            value = clipLimitText,
            onValueChange = { str ->
                clipLimitText = str
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
        Text(text = "size")

        BasicTextField(
            value = sizeText,
            onValueChange = { str ->
                sizeText = str
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )

        confirmButton(state.isEnhance) {
            viewModel.clahe(state,clipLimitText.toDouble(), sizeText.toInt())
        }
    }
}

@Composable
private fun generateGammaParams(state: ApplicationState, viewModel: ImageEnhanceViewModel) {

    var gammaText by remember {
        mutableStateOf("1.0")
    }

    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "gamma")

        BasicTextField(
            value = gammaText,
            onValueChange = { str ->
                gammaText = str
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )

        confirmButton(state.isEnhance) {
            viewModel.gammaCorrection(state, gammaText.toFloat())
        }
    }
}

@Composable
private fun generateUSMParams(state: ApplicationState, viewModel: ImageEnhanceViewModel) {

    var amountText by remember {
        mutableStateOf("25")
    }

    var thresholdText by remember {
        mutableStateOf("0")
    }

    var radiusText by remember {
        mutableStateOf("50")
    }

    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "Radius")

        BasicTextField(
            value = radiusText,
            onValueChange = { str ->
                radiusText = str
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
        Text(text = "Threshold")

        BasicTextField(
            value = thresholdText,
            onValueChange = { str ->
                thresholdText = str
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
        Text(text = "Amount")

        BasicTextField(
            value = amountText,
            onValueChange = { str ->
                amountText = str
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )

        confirmButton(state.isEnhance) {
            viewModel.unsharpMask(state, radiusText.toInt(),thresholdText.toInt(),amountText.toInt())
        }
    }
}

@Composable
private fun generateACEParams(state: ApplicationState, viewModel: ImageEnhanceViewModel) {

    var ratioText by remember {
        mutableStateOf("4")
    }

    var radiusText by remember {
        mutableStateOf("1")
    }

    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "Ratio")

        BasicTextField(
            value = ratioText,
            onValueChange = { str ->
                ratioText = str
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
        Text(text = "Radius")

        BasicTextField(
            value = radiusText,
            onValueChange = { str ->
                radiusText = str
            },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            modifier = Modifier.padding(start = 10.dp).width(120.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
            textStyle = TextStyle(Color.Black, fontSize = 12.sp)
        )

        confirmButton(state.isEnhance) {
            viewModel.ace(state,ratioText.toInt(), radiusText.toInt())
        }
    }
}