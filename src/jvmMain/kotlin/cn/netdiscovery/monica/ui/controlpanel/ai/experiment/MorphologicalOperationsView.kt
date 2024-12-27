package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MorphologicalOperationSettings
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
import cn.netdiscovery.monica.utils.getValidateField
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MorphologicalOperationsView
 * @author: Tony Shen
 * @date: 2024/12/21 20:16
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val operatingElementsTag = arrayListOf("腐蚀", "膨胀", "开操作", "闭操作", "形态学梯度", "顶帽", "黑帽", "击中击不中")
val structuralElementsTag = arrayListOf("矩形","十字交叉","椭圆形")
val tagList1 = operatingElementsTag.take(4)
val tagList2 = operatingElementsTag.takeLast(4)

var morphologicalOperationSettings: MorphologicalOperationSettings = MorphologicalOperationSettings()

@Composable
fun morphologicalOperations(state: ApplicationState, title: String) {

    val viewModel: MorphologicalOperationsViewModel = koinInject()

    var operatingElementOption by remember { mutableStateOf("Null") }
    var structuralElementOption by remember { mutableStateOf("矩形") }

    var widthText by remember { mutableStateOf("3") }
    var heightText by remember { mutableStateOf("3") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "操作元素", color = Color.Black)

            Row {
                tagList1.forEach {
                    RadioButton(
                        selected = (it == operatingElementOption),
                        onClick = {
                            operatingElementOption = it
                            val index = operatingElementsTag.indexOf(it)
                            morphologicalOperationSettings = morphologicalOperationSettings.copy(op = index)
                        }
                    )
                    Text(text = it, modifier = Modifier.width(120.dp).align(Alignment.CenterVertically))
                }
            }

            Row {
                tagList2.forEach {

                    RadioButton(
                        selected = (it == operatingElementOption),
                        onClick = {
                            operatingElementOption = it
                            val index = operatingElementsTag.indexOf(it)
                            morphologicalOperationSettings = morphologicalOperationSettings.copy(op = index)
                        }
                    )
                    Text(text = it, modifier = Modifier.width(120.dp).align(Alignment.CenterVertically))
                }
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            subTitleWithDivider(text = "结构元素", color = Color.Black)

            Row {
                structuralElementsTag.forEach {

                    RadioButton(
                        selected = (it == structuralElementOption),
                        onClick = {
                            structuralElementOption = it
                            val index = structuralElementsTag.indexOf(it)
                            morphologicalOperationSettings = morphologicalOperationSettings.copy(shape = index)
                        }
                    )
                    Text(text = it, modifier = Modifier.width(120.dp).align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(modifier = Modifier.width(70.dp), text = "结构元素：", color = Color.Unspecified)

                basicTextFieldWithTitle(titleText = "宽度", widthText) { str ->
                    widthText = str
                }

                basicTextFieldWithTitle(titleText = "高度", heightText) { str ->
                    heightText = str
                }
            }
        }

        Button(
            modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
            onClick = experimentViewClick(state) {

                if(state.currentImage?.type == BufferedImage.TYPE_BYTE_BINARY) {
                    val width = getValidateField(block = { widthText.toInt() } , failed = { experimentViewVerifyToast("width 需要 int 类型") }) ?: return@experimentViewClick
                    val height = getValidateField(block = { heightText.toInt() } , failed = { experimentViewVerifyToast("height 需要 int 类型") }) ?: return@experimentViewClick

                    morphologicalOperationSettings = morphologicalOperationSettings.copy(width = width, height = height)

                    viewModel.morphologyEx(state, morphologicalOperationSettings)
                } else {
                    experimentViewVerifyToast("请先将当前图像进行二值化")
                }
            }
        ) {
            Text(text = "应用", color = Color.Unspecified)
        }
    }
}