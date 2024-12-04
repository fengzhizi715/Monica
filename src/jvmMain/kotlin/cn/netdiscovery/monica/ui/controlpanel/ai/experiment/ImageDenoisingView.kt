package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ImageDenoisingView
 * @author: Tony Shen
 * @date: 2024/12/4 14:17
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)


@Composable
fun imageDenoising(state: ApplicationState, title: String) {

    var gaussianBlurKSizeText by remember { mutableStateOf("") }
    var sigmaXText by remember { mutableStateOf("") }
    var sigmaYText by remember { mutableStateOf("") }

    var medianBlurKSizeText by remember { mutableStateOf("") }

    var dText by remember { mutableStateOf("") }
    var sigmaColorText by remember { mutableStateOf("") }
    var sigmaSpaceText by remember { mutableStateOf("") }

    var spText by remember { mutableStateOf("") }
    var srText by remember { mutableStateOf("") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "高斯滤波", color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "ksize", gaussianBlurKSizeText) { str ->
                    gaussianBlurKSizeText = str
                }

                basicTextFieldWithTitle(titleText = "sigmaX", sigmaXText) { str ->
                    sigmaXText = str
                }

                basicTextFieldWithTitle(titleText = "sigmaY", sigmaYText) { str ->
                    sigmaYText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {
                }
            ) {
                Text(text = "高斯滤波", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "中值滤波", color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "ksize", medianBlurKSizeText) { str ->
                    medianBlurKSizeText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {
                }
            ) {
                Text(text = "中值滤波", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "高斯双边滤波", color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "d", dText) { str ->
                    dText = str
                }

                basicTextFieldWithTitle(titleText = "sigmaColor", sigmaColorText) { str ->
                    sigmaColorText = str
                }

                basicTextFieldWithTitle(titleText = "sigmaSpace", sigmaSpaceText) { str ->
                    sigmaSpaceText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                }
            ) {
                Text(text = "高斯双边滤波", color = Color.Unspecified)
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "均值迁移滤波", color = Color.Black)

            Row(modifier = Modifier.padding(top = 10.dp)) {
                basicTextFieldWithTitle(titleText = "sp", spText) { str ->
                    spText = str
                }

                basicTextFieldWithTitle(titleText = "sr", srText) { str ->
                    srText = str
                }
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = experimentViewClick(state) {

                }
            ) {
                Text(text = "均值迁移滤波", color = Color.Unspecified)
            }
        }
    }
}