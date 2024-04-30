package cn.netdiscovery.monica.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.showFileSelector
import javax.swing.JFileChooser

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ControlContent
 * @author: Tony Shen
 * @date: 2024/4/26 11:10
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun divider() {
    Row {
        Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).height(1.dp).weight(1.0f).background(color = Color.LightGray))
    }
}

@Composable
fun ControlContent(
    state: ApplicationState,
    modifier: Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            basicContent(state)

            divider()

            imageProcessContent(state)

            divider()

            // 滤镜相关的内容
            filterContent(state)

            divider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = state.isUsingSourcePath,
                    onCheckedChange = {
                        state.isUsingSourcePath = it
                        state.outputPath = if (it) "原位置" else ""
                    }
                )
                Text("加载网络图片", fontSize = 20.sp)
            }

            Row {
                Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).height(1.dp).weight(1.0f).background(color = Color.LightGray))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = state.isUsingSourcePath,
                    onCheckedChange = {
                        state.isUsingSourcePath = it
                        state.outputPath = if (it) "原位置" else ""
                    }
                )
                Text("输出至原位置", fontSize = 20.sp)
            }

            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("保存位置：")
                OutlinedTextField(
                    value = state.outputPath,
                    onValueChange = { state.outputPath = it },
                    enabled = !state.isUsingSourcePath,
                    modifier = Modifier.fillMaxWidth(0.75f)
                )
                Button(
                    onClick = {
                        showFileSelector(
                            isMultiSelection = false,
                            selectionMode = JFileChooser.DIRECTORIES_ONLY,
                            selectionFileFilter = null
                        ) {
                            state.outputPath = it[0].absolutePath
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = !state.isUsingSourcePath
                ) {
                    Text("选择")
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        state.isShowGuideLine = false
                        state.onClickBuildImage()
                    },
                    enabled = state.rawImage != null
                ) {
                    Text("预览")
                }

                Button(
                    onClick = {
                        state.isShowGuideLine = false
//                        state.onClickSave()
                    },
                    enabled = state.rawImage != null
                ) {
                    Text("保存")
                }
            }
        }
    }
}
