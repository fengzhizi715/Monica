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
import filterNames
import java.text.Collator
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.FilterContent
 * @author: Tony Shen
 * @date: 2024/4/30 23:24
 * @version: V1.0 <描述当前版本功能>
 */
val tempMap: HashMap<Pair<String, String>, String> = hashMapOf()

var selectedIndex = mutableStateOf(0)

@Composable
fun filterContent(state: ApplicationState) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state.isFilter, onCheckedChange = {
            state.isFilter = it

            if (!state.isFilter) {
                selectedIndex.value = 0
            }
        })
        Text("滤镜", color = Color.Black, fontSize = 20.sp)
    }

    dropdownFilterMenuForSelect(state)

    Row(modifier = Modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Button(
            modifier = Modifier.offset(x = 290.dp,y = 0.dp),
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

                    // 按照参数名首字母进行排序
                    list.sortWith(Comparator { o1, o2 -> Collator.getInstance(Locale.UK).compare(o1.first, o2.first); })

                    println("sort params: $list")
                    rxCache.saveOrUpdate(filterName, list)
                }
            },
            enabled = state.isFilter && selectedIndex.value>0 && tempMap.size>0
        ) {
            Text(text = "应用参数",
                color = if (state.isFilter) Color.Unspecified else Color.LightGray)
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
            Button(modifier = Modifier.width(180.dp),
                onClick = { expanded =true },
                enabled = state.isFilter){

                Text(text = filterNames[selectedIndex.value],
                    fontSize = 11.5.sp,
                    color = if (state.isFilter) Color.Unspecified else Color.LightGray)
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
}

/**
 * 根据不同的滤镜，生成不同的参数
 */
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
            modifier = Modifier.padding(top = 10.dp)
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