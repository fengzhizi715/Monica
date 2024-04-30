package cn.netdiscovery.monica.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.click
import cn.netdiscovery.monica.utils.extension.to2fStr
import cn.netdiscovery.monica.utils.showFileSelector
import filterNames
import javax.swing.JFileChooser

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ControlContent
 * @author: Tony Shen
 * @date: 2024/4/26 11:10
 * @version: V1.0 <描述当前版本功能>
 */

val tempMap: HashMap<Pair<String, String>, String> = hashMapOf()

var selectedIndex = mutableStateOf(0)

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
                Checkbox(state.isHLS, onCheckedChange = {
                    state.isHLS = it
                })
                Text("图像处理：", color = Color.Black, fontSize = 20.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "饱和度增益：",
                    color = if (state.isHLS) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.saturation,
                        onValueChange = {
                            state.saturation = it
                        },
                        enabled = state.isHLS,
                        modifier = Modifier.weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.saturation.to2fStr(),
                        color = if (state.isHLS) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "色相增益：",
                    color = if (state.isHLS) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.hue,
                        onValueChange = {
                            state.hue = it
                        },
                        enabled = state.isHLS,
                        modifier = Modifier.padding(start = 15.dp).weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.hue.to2fStr(),
                        color = if (state.isHLS) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "亮度增益：",
                    color = if (state.isHLS) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.luminance,
                        onValueChange = {
                            state.luminance = it
                        },
                        enabled = state.isHLS,
                        modifier = Modifier.padding(start = 15.dp).weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.luminance.to2fStr(),
                        color = if (state.isHLS) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }

            Row {
                Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).height(1.dp).weight(1.0f).background(color = Color.LightGray))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isFilter, onCheckedChange = {
                    state.isFilter = it
                })
                Text("滤镜效果：", color = Color.Black, fontSize = 20.sp)
            }

            dropdownFilterMenuForSelect(state)

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

@Preview
@Composable
fun dropdownFilterMenuForSelect(state:ApplicationState){
    var expanded by remember { mutableStateOf(false) }


    Row(
        modifier = Modifier.wrapContentSize().offset(x = 15.dp,y = 0.dp)
    ) {
        Column {
            Button(modifier = Modifier.width(160.dp), onClick = {
                expanded =true
            }){
                Text(text = filterNames[selectedIndex.value])
            }

            DropdownMenu(expanded=expanded, onDismissRequest = {expanded =false}){
                filterNames.forEachIndexed{ index,label ->
                    DropdownMenuItem(onClick = {
                        selectedIndex.value = index
                        expanded = false
                    }){
                        Text(text = label)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp)
        ) {
            if (selectedIndex.value > 0) {
                Text(text = "滤镜相关参数")
                generateFilterParams(selectedIndex.value)
            }
        }
    }

    Row(modifier = Modifier.padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Button(
            modifier = Modifier.offset(x = 280.dp,y = 0.dp),
            onClick = {
                click {
                    if (state.rawImageFile == null) return@click

                    val filterName = filterNames[selectedIndex.value]
                    val list = mutableListOf<Triple<String,String,Any>>()
                    tempMap.forEach { (t, u) ->
                        val value = when(t.second) {
                            "Int" -> u.toInt()
                            "Float" -> u.toFloat()
                            "Double" -> u.toDouble()
                            else -> u
                        }

                        list.add(Triple(t.first, t.second, value))
                    }

                    rxCache.saveOrUpdate(filterName, list)
                }
            },
            enabled = true
        ) {
            Text("应用滤镜")
        }
    }
}

@Composable
fun generateFilterParams(selectedIndex:Int) {

    tempMap.clear()

    val filterName = filterNames[selectedIndex]
    val params: List<Triple<String,String,Any>>? = getFilterParam(filterName)

    params?.forEach {

        val paramName = it.first
        val type = it.second
        var text by remember(filterName, paramName) {
            mutableStateOf(it.third.toString())
        }

        tempMap[Pair(paramName, type)] = text

        Row(
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(text = paramName)

            BasicTextField(
                value = text,
                onValueChange = { str ->
                    text = str
                    tempMap[Pair(paramName, type)] = text
                },
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions.Default,
                cursorBrush = SolidColor(Color.Gray),
                singleLine = true,
                modifier = Modifier.padding(start = 10.dp).background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)).height(20.dp),
                textStyle = TextStyle(Color.Black, fontSize = 12.sp)
            )
        }
    }
}
