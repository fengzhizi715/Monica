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
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.subTitleWithDivider
import cn.netdiscovery.monica.ui.widget.title
import cn.netdiscovery.monica.utils.getValidateField
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MorphologicalOperationsView
 * @author: Tony Shen
 * @date: 2024/12/21 20:16
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

val operatingElementsTag = arrayListOf("膨胀", "腐蚀", "开操作", "闭操作", "顶帽", "黑帽", "差分", "击中击不中")

val structuralElementsTag = arrayListOf("矩形","圆形","十字交叉")

@Composable
fun morphologicalOperations(state: ApplicationState, title: String) {

    var operatingElementOption by remember { mutableStateOf("Null") }
    var structuralElementOption by remember { mutableStateOf("Null") }

    var widthText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        Column {
            subTitleWithDivider(text = "操作元素", color = Color.Black)

            Row {
                operatingElementsTag.forEach {

                    RadioButton(
                        selected = (it == operatingElementOption),
                        onClick = {
                            operatingElementOption = it
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
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
                        }
                    )
                    Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(modifier = Modifier.width(70.dp), text = "结构元素：", color = Color.Unspecified)

                basicTextFieldWithTitle(titleText = "宽度", widthText) { str ->

                }

                basicTextFieldWithTitle(titleText = "高度", heightText) { str ->

                }
            }
        }

        Button(
            modifier = Modifier.padding(top = 10.dp).align(Alignment.End),
            onClick = experimentViewClick(state) {

            }
        ) {
            Text(text = "应用", color = Color.Unspecified)
        }
    }
}