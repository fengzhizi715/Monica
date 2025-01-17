package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MatchTemplateSettings
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MatchTemplateViewModel
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
import cn.netdiscovery.monica.ui.widget.toolTipButton
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.MatchTemplateView
 * @author: Tony Shen
 * @date: 2024/12/29 14:03
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val matchingMethodTag = arrayListOf("原图匹配","灰度匹配","边缘匹配")

var matchTemplateSettings: MatchTemplateSettings = MatchTemplateSettings()

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun matchTemplate(state: ApplicationState, title: String) {

    val viewModel: MatchTemplateViewModel = koinInject()

    var matchingMethodOption by remember { mutableStateOf("原图匹配") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "模版", color = Color.Black)

            Row {
                Text(modifier = Modifier.width(100.dp).padding(top = 10.dp), text = "导入模版：", color = Color.Unspecified)

                Card(
                    modifier = Modifier.padding(10.dp).width(150.dp).height(150.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    onClick = {

                    },
                    enabled = viewModel.templateImage == null
                ) {
                    if (viewModel.templateImage == null) {
                        Text(
                            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                            text = "请点击选择图像",
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Box {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "target",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colors.primary,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Image(
                                    painter = viewModel.templateImage!!.toPainter(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier)
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "匹配方式", color = Color.Black)

            Row {
                matchingMethodTag.forEach {

                    RadioButton(
                        selected = (it == matchingMethodOption),
                        onClick = {
                            matchingMethodOption = it
                            val index = matchingMethodTag.indexOf(it)
                            matchTemplateSettings = matchTemplateSettings.copy(matchType = index)
                        }
                    )
                    Text(text = it, modifier = Modifier.width(120.dp).align(Alignment.CenterVertically))
                }
            }

        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "旋转", color = Color.Black)
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "尺度", color = Color.Black)
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "NMS 相关参数", color = Color.Black)
        }

        Button(
            modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
            onClick = experimentViewClick(state) {

            }
        ) {
            Text(text = "模版匹配", color = Color.Unspecified)
        }
    }
}