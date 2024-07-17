package cn.netdiscovery.monica.ui.controlpanel

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
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.image2ByteArray
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.click
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
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
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolTipButton(text = "直方图均衡化",
            painter = painterResource("images/imageenhance/histogram.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = EqualizeHistStatus

                val width = state.currentImage!!.width
                val height = state.currentImage!!.height
                val byteArray = state.currentImage!!.image2ByteArray()

                state.scope.launch(IO) {
                    clickLoadingDisplay {
                        val outPixels = ImageProcess.equalizeHist(byteArray)
                        state.addQueue(state.currentImage!!)
                        state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
                    }
                }
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

                val width = state.currentImage!!.width
                val height = state.currentImage!!.height
                val byteArray = state.currentImage!!.image2ByteArray()

                state.scope.launch(IO) {
                    clickLoadingDisplay {
                        val outPixels = ImageProcess.laplace(byteArray)
                        state.addQueue(state.currentImage!!)
                        state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
                    }
                }
            })

        toolTipButton(text = "USM 锐化",
            painter = painterResource("images/imageenhance/usm.png"),
            enable = { state.isEnhance },
            onClick = {
                state.currentStatus = USMStatus
            })
    }

    Column {

        when(state.currentStatus) {
            GammaStatus -> {
                generateGammaParams(state)
            }

            USMStatus -> {
                generateUSMParams(state)
            }
        }
    }
}

@Composable
private fun generateGammaParams(state: ApplicationState) {

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

        Row {
            Button(
                modifier = Modifier.offset(x = 140.dp,y = 0.dp),
                onClick = {
                    click {
                        val width = state.currentImage!!.width
                        val height = state.currentImage!!.height
                        val byteArray = state.currentImage!!.image2ByteArray()
                        state.scope.launch(IO) {
                           clickLoadingDisplay {
                               val outPixels = ImageProcess.gammaCorrection(byteArray,gammaText.toFloat())
                               state.addQueue(state.currentImage!!)
                               state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
                           }
                        }
                    }
                },
                enabled = state.isEnhance
            ) {
                Text(text = "确定",
                    color = if (state.isEnhance) Color.Unspecified else Color.LightGray)
            }
        }
    }
}

@Composable
private fun generateUSMParams(state: ApplicationState) {

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

        Row {
            Button(
                modifier = Modifier.offset(x = 140.dp,y = 0.dp),
                onClick = {
                    click {
                        val width = state.currentImage!!.width
                        val height = state.currentImage!!.height
                        val byteArray = state.currentImage!!.image2ByteArray()

                        state.scope.launch(IO) {
                            clickLoadingDisplay {
                                val outPixels = ImageProcess.unsharpMask(byteArray,radiusText.toInt(),thresholdText.toInt(),amountText.toInt())
                                state.addQueue(state.currentImage!!)
                                state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
                            }
                        }
                    }
                },
                enabled = state.isEnhance
            ) {
                Text(text = "确定",
                    color = if (state.isEnhance) Color.Unspecified else Color.LightGray)
            }
        }
    }
}