package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import cn.netdiscovery.monica.rxcache.Param
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.rxcache.getFilterRemark
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.collator
import cn.netdiscovery.monica.utils.extensions.safelyConvertToInt
import filterNames
import loadingDisplay
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.filter.FilterView
 * @author: Tony Shen
 * @date: 2025/3/6 15:34
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

private var selectedIndex = mutableStateOf(-1)
private val tempMap: HashMap<Pair<String, String>, String> = hashMapOf() // 存放当前滤镜的参数信息

private var showTopToast by mutableStateOf(false)
private var toastMessage by mutableStateOf("")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun filter(state: ApplicationState) {

    val viewModel: FilterViewModel = koinInject()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Row (
            modifier = Modifier.fillMaxSize().padding(bottom = 160.dp, end = 400.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                    chooseImage(state) { file ->
                        state.rawImage = BufferedImages.load(file)
                        state.currentImage = state.rawImage
                        state.rawImageFile = file
                    }
                },
                enabled = state.currentImage == null
            ) {
                if (state.currentImage == null) {
                    Text(
                        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                        text = "请点击选择图像",
                        textAlign = TextAlign.Center
                    )
                } else {
                    Image(
                        painter = state.currentImage!!.toPainter(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 160.dp).align(Alignment.BottomStart)) {
            subTitle(text = "选择下列滤镜", modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold)

            desktopLazyRow(modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(100.dp)) {
                filterNames.forEachIndexed{ index,label ->
                    Card(
                        elevation = 16.dp,
                        modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{
                            selectedIndex.value = index
                        }
                    ) {
                        Row(horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = label,
                                fontSize = 22.sp,
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth())
                        }
                    }
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.width(400.dp).height(400.dp).align(Alignment.CenterEnd), backgroundColor = Color.White, percent = 3) {

            Column {
                if (selectedIndex.value>=0) {
                    subTitle(text = "${filterNames[selectedIndex.value]} 滤镜", modifier = Modifier.padding(start =10.dp, bottom = 10.dp), fontWeight = FontWeight.Bold)
                    generateFilterParams(selectedIndex.value)
                    generateFilterRemark(selectedIndex.value)
                } else {
                    subTitle(text = "请先选择一款滤镜", modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter // 将内容对齐到底部中心
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly // 按钮水平分布
                ) {
//                    toolTipButton(text = "更新滤镜参数",
//                        enable = { state.currentImage != null && tempMap.size>0 },
//                        painter = painterResource("images/filters/update_params.png"),
//                        onClick = {
//                            viewModel.updateFilterParams()
//                            showTopToast("滤镜修改参数生效")
//                        })

                    toolTipButton(text = "预览效果",
                        enable = { state.currentImage != null && selectedIndex.value >= 0 },
                        painter = painterResource("images/filters/preview.png"),
                        onClick = {
                            viewModel.applyFilter(state, selectedIndex.value, tempMap)
                        })

                    toolTipButton(text = "上一步",
                        painter = painterResource("images/doodle/previous_step.png"),
                        onClick = {
                            state.getLastImage()?.let {
                                state.currentImage = it
                            }
                        })

                    toolTipButton(text = "保存",
                        painter = painterResource("images/doodle/save.png"),
                        onClick = {
                            state.closePreviewWindow()
                        })

                    toolTipButton(text = "删除原图",
                        painter = painterResource("images/preview/delete.png"),
                        onClick = {
                            state.clearImage()
                        })
                }
            }
        }

        if (loadingDisplay) {
            showLoading()
        }

        if (showTopToast) {
            topToast(message = toastMessage) {
                showTopToast = false
            }
        }
    }
}

/**
 * 根据不同的滤镜，生成不同的参数
 */
@Composable
private fun generateFilterParams(selectedIndex:Int) {

    tempMap.clear()

    val filterName = filterNames[selectedIndex]
    val params: List<Param>? = getFilterParam(filterName)

    Collections.sort(params) { o1, o2 -> collator.compare(o1.key, o2.key) }

    params?.forEach {

        val paramName = it.key
        val type = it.type
        var text by remember(filterName, paramName) {

            if (type == "Int") {
                mutableStateOf((it.value.toString().safelyConvertToInt()?:0).toString())
            } else {
                mutableStateOf(it.value.toString())
            }
        }

        tempMap[Pair(paramName, type)] = text

        Row(
            modifier = Modifier.padding(top = 15.dp, start = 10.dp)
        ) {
            basicTextFieldWithTitle(titleText = paramName, text) { str ->
                text = str
                tempMap[Pair(paramName, type)] = text
            }
        }
    }
}

@Composable
private fun generateFilterRemark(selectedIndex:Int) {
    val filterName = filterNames[selectedIndex]
    val remark = getFilterRemark(filterName)

    if (!remark.isNullOrEmpty()) {
        Card(
            modifier = Modifier.padding(top = 15.dp, start = 10.dp),
            shape = RoundedCornerShape(5.dp),
            elevation = 4.dp,
            backgroundColor = Color.LightGray
        ) {
            Text(remark, color = Color.Black, fontSize = 12.sp , modifier = Modifier.padding(10.dp))
        }
    }
}

private fun showTopToast(message:String) {
    toastMessage = message
    showTopToast = true
}