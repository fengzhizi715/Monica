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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.domain.MatchTemplateSettings
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MatchTemplateViewModel
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getBufferedImage
import cn.netdiscovery.monica.utils.getValidateField
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
    val i18nState = rememberI18nState()
    val viewModel: MatchTemplateViewModel = koinInject()

    var matchingMethodOption by remember { mutableStateOf("原图匹配") }

    var angleStartText by remember { mutableStateOf("0") }
    var angleEndText by remember { mutableStateOf("360") }
    var angleStepText by remember { mutableStateOf("10") }
    var scaleStartText by remember { mutableStateOf("0.1") }
    var scaleEndText by remember { mutableStateOf("1.0") }
    var scaleStepText by remember { mutableStateOf("0.1") }

    var matchTemplateThresholdText by remember { mutableStateOf("0.8") }
    var scoreThresholdText by remember { mutableStateOf("0.6") }
    var nmsThresholdText by remember { mutableStateOf("0.3") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = i18nState.getString("template"), color = Color.Black)

            Row {
                Text(modifier = Modifier.width(100.dp).padding(top = 10.dp), text = "导入模版：", color = Color.Unspecified)

                Card(
                    modifier = Modifier.padding(10.dp).width(150.dp).height(150.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    onClick = {
                        chooseImage(state) { file ->
                            CVState.templateImage = getBufferedImage(file)
                        }
                    },
                    enabled = CVState.templateImage == null
                ) {
                    if (CVState.templateImage == null) {
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
                                Image(
                                    painter = CVState.templateImage!!.toPainter(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier)
                            }

                            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                                toolTipButton(text = "删除 source 的图",
                                    painter = painterResource("images/preview/delete.png"),
                                    buttonModifier = Modifier,
                                    iconModifier = Modifier.size(20.dp),
                                    onClick = {
                                        viewModel.clearTemplateImage()
                                    })
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("matching_method"), color = Color.Black)

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
            subTitleWithDivider(text = i18nState.getString("rotation"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)) {
                basicTextFieldWithTitle(titleText = i18nState.getString("min_angle"), angleStartText) { str ->
                    angleStartText = str
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("max_angle"), angleEndText) { str ->
                    angleEndText = str
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("angle_step"), angleStepText) { str ->
                    angleStepText = str
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("scale"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)) {
                basicTextFieldWithTitle(titleText = i18nState.getString("min_scale"), scaleStartText) { str ->
                    scaleStartText = str
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("max_scale"), scaleEndText) { str ->
                    scaleEndText = str
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("scale_step"), scaleStepText) { str ->
                    scaleStepText = str
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("template_matching_params"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)) {
                basicTextFieldWithTitle(titleText = i18nState.getString("threshold"), matchTemplateThresholdText) { str ->
                    matchTemplateThresholdText = str
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = i18nState.getString("nms_params"), color = Color.Black)

            Row(modifier = Modifier.padding(top = 20.dp)) {
                basicTextFieldWithTitle(titleText = i18nState.getString("score_threshold"), scoreThresholdText) { str ->
                    scoreThresholdText = str
                }

                basicTextFieldWithTitle(titleText = i18nState.getString("nms_threshold"), nmsThresholdText) { str ->
                    nmsThresholdText = str
                }
            }
        }

        Button(
            modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
            onClick = experimentViewClick(state) {

                if (CVState.templateImage == null) {
                    experimentViewVerifyToast("请先导入模版文件")
                    return@experimentViewClick
                }

                val angleStart = getValidateField(block = { angleStartText.toInt() } ,
                    condition = { it in 0..360 },
                    failed = { experimentViewVerifyToast("angleStart 需要 int 类型， 且 angleStart >= 0") }) ?: return@experimentViewClick
                val angleEnd = getValidateField(block = { angleEndText.toInt() } ,
                    condition = { it in 0..360 },
                    failed = { experimentViewVerifyToast("angleEnd 需要 int 类型， 且 angleEnd <= 360") }) ?: return@experimentViewClick
                val angleStep = getValidateField(block = { angleStepText.toInt() } ,
                    condition = { it > 0 },
                    failed = { experimentViewVerifyToast("angleStep 需要 int 类型， 且 angleStep > 0") }) ?: return@experimentViewClick

                val scaleStart = getValidateField(block = { scaleStartText.toDouble() } ,
                    condition = { it in 0.0..1.0 },
                    failed = { experimentViewVerifyToast("scaleStart 需要 double 类型， 且 scaleStart >= 0") }) ?: return@experimentViewClick
                val scaleEnd = getValidateField(block = { scaleEndText.toDouble() } ,
                    condition = { it in 0.0..1.0 },
                    failed = { experimentViewVerifyToast("scaleEnd 需要 double 类型， 且 scaleStart <= 1.0") }) ?: return@experimentViewClick
                val scaleStep = getValidateField(block = { scaleStepText.toDouble() } ,
                    condition = { it > 0 },
                    failed = { experimentViewVerifyToast("scaleStep 需要 double 类型， 且 scaleStep > 0") }) ?: return@experimentViewClick

                val matchTemplateThreshold = getValidateField(block = { matchTemplateThresholdText.toDouble() } ,
                    condition = { it in 0.0..1.0 },
                    failed = { experimentViewVerifyToast("matchTemplateThreshold 需要 double 类型， 且 matchTemplateThreshold >= 0") }) ?: return@experimentViewClick
                val scoreThreshold = getValidateField(block = { scoreThresholdText.toFloat() } ,
                    condition = { it in 0.0..1.0 },
                    failed = { experimentViewVerifyToast("scoreThreshold 需要 float 类型， 且 scoreThreshold >= 0") }) ?: return@experimentViewClick
                val nmsThreshold = getValidateField(block = { nmsThresholdText.toFloat() } ,
                    condition = { it in 0.0..1.0 },
                    failed = { experimentViewVerifyToast("nmsThreshold 需要 float 类型， 且 nmsThreshold >= 0") }) ?: return@experimentViewClick

                matchTemplateSettings = matchTemplateSettings.copy(angleStart = angleStart, angleEnd = angleEnd, angleStep = angleStep,
                    scaleStart = scaleStart, scaleEnd = scaleEnd, scaleStep = scaleStep,
                    matchTemplateThreshold = matchTemplateThreshold, scoreThreshold = scoreThreshold, nmsThreshold = nmsThreshold)

                viewModel.matchTemplate(state, matchTemplateSettings)
            }
        ) {
            Text(text = "模版匹配", color = Color.Unspecified)
        }
    }
}