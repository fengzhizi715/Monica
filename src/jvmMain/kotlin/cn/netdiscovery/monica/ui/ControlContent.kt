package cn.netdiscovery.monica.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.to2fStr

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ControlContent
 * @author: Tony Shen
 * @date: 2024/4/26 11:10
 * @version: V1.0 <描述当前版本功能>
 */
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isFilterImg, onCheckedChange = {
                    state.isFilterImg = it
                })
                Text("图像处理：")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "饱和度增益：",
                    color = if (state.isFilterImg) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.saturation,
                        onValueChange = {
                            state.saturation = it
                        },
                        enabled = state.isFilterImg,
                        modifier = Modifier.weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.saturation.to2fStr(),
                        color = if (state.isFilterImg) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "色相增益：",
                    color = if (state.isFilterImg) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.hue,
                        onValueChange = {
                            state.hue = it
                        },
                        enabled = state.isFilterImg,
                        modifier = Modifier.weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.hue.to2fStr(),
                        color = if (state.isFilterImg) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "亮度增益：",
                    color = if (state.isFilterImg) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.luminance,
                        onValueChange = {
                            state.luminance = it
                        },
                        enabled = state.isFilterImg,
                        modifier = Modifier.weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.luminance.to2fStr(),
                        color = if (state.isFilterImg) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isBlur, onCheckedChange = {
                    state.isBlur = it
                })
                Text("滤镜效果：")
            }

            dropdownMenuForSelect()

//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text("保存位置：")
//                OutlinedTextField(
//                    value = state.outputPath,
//                    onValueChange = { state.outputPath = it },
//                    enabled = !state.isUsingSourcePath,
//                    modifier = Modifier.fillMaxWidth(0.5f)
//                )
//                Button(
//                    onClick = {
//                        showFileSelector(
//                            isMultiSelection = false,
//                            selectionMode = JFileChooser.DIRECTORIES_ONLY,
//                            selectionFileFilter = null
//                        ) {
//                            state.outputPath = it[0].absolutePath
//                        }
//                    },
//                    modifier = Modifier.padding(start = 8.dp),
//                    enabled = !state.isUsingSourcePath
//                ) {
//                    Text("选择")
//                }
//                Checkbox(
//                    checked = state.isUsingSourcePath,
//                    onCheckedChange = {
//                        state.isUsingSourcePath = it
//                        state.outputPath = if (it) "原位置" else ""
//                    }
//                )
//                Text("输出至原位置", fontSize = 12.sp)
//            }
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Button(
//                    onClick = {
//                        state.isShowGuideLine = false
//                        state.onClickBuildImg()
//                    },
//                    enabled = state.rawImg != null
//                ) {
//                    Text("预览")
//                }
//
//                Button(
//                    onClick = {
//                        state.isShowGuideLine = false
//                        state.onClickSave()
//                    },
//                    enabled = state.rawImg != null
//                ) {
//                    Text("保存")
//                }
//            }
        }
    }
}

@Preview
@Composable
fun dropdownMenuForSelect(){
    var expanded by remember { mutableStateOf(false) }
    val items= listOf("正面","背面")
    var selectedIndex by remember{ mutableStateOf(0) }

    Box(
        Modifier.wrapContentSize().offset(x = 15.dp,y = 0.dp)
    ) {
        Button(modifier = Modifier.width(100.dp), onClick = { expanded =true}){
            Text(text =items[selectedIndex])
        }

        DropdownMenu(expanded=expanded, onDismissRequest = {expanded =false}){
            items.forEachIndexed{ index,label ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
//                    Store.engineerModeDevice.captureMode.value = if(selectedIndex==0) "front" else "back"
                }){
                    Text(text = label)
                }
            }
        }
    }
}